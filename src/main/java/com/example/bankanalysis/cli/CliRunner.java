package com.example.bankanalysis.cli;

import com.example.bankanalysis.metrics.CounterpartyMetrics;
import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.Transaction;
import com.example.bankanalysis.parser.BankStatementParser;
import com.example.bankanalysis.parser.ParserFactory;
import com.example.bankanalysis.processor.DataQualityValidator;
import com.example.bankanalysis.processor.DataStandardizer;
import com.example.bankanalysis.report.MarkdownReportGenerator;
import com.example.bankanalysis.report.WordReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Component
public class CliRunner {

    private static final Logger log = LoggerFactory.getLogger(CliRunner.class);

    @Autowired private ParserFactory parserFactory;
    @Autowired private DataStandardizer dataStandardizer;
    @Autowired private DataQualityValidator dataQualityValidator;
    @Autowired private CounterpartyMetrics counterpartyMetrics;
    @Autowired private WordReportGenerator wordReportGenerator;
    @Autowired private MarkdownReportGenerator markdownReportGenerator;

    public void run(String[] args) throws Exception {
        String input = getArg(args, "input", null);
        String bank = getArg(args, "bank", "ABC");
        String type = getArg(args, "type", "CORPORATE");
        String company = getArg(args, "company", "示例公司");
        String output = getArg(args, "output", "./output/report.docx");
        String format = getArg(args, "format", "WORD");

        if (input == null) {
            log.error("必须指定 --input 参数。用法示例：\n" +
                "  --input=flow.xlsx --bank=ABC --type=CORPORATE --company=XX公司 --output=report.docx");
            return;
        }

        log.info("开始解析流水文件: {}", input);
        BankStatementParser parser = parserFactory.getParser(bank, type);

        // 同时传入文件名以支持 CSV 自动识别
        List<Transaction> transactions;
        try (var in = Files.newInputStream(Paths.get(input))) {
            if (parser instanceof com.example.bankanalysis.parser.AbstractExcelParser aep) {
                transactions = aep.parse(in, input);
            } else {
                transactions = parser.parse(in);
            }
        }

        log.info("解析完成，共 {} 笔交易，开始标准化...", transactions.size());
        transactions = dataStandardizer.standardize(transactions);

        log.info("开始数据质量校验...");
        var qualityReport = dataQualityValidator.validate(transactions);

        log.info("开始分析...");
        AnalysisResult result = new AnalysisResult();
        result.setCompanyName(company);
        result.setAccountNo(transactions.isEmpty() ? "" : transactions.get(0).getAccountNo());
        result.setTransactions(transactions);
        result.setQualityReport(qualityReport);
        result.setTotalInflow(sum(transactions, true));
        result.setTotalOutflow(sum(transactions, false));
        result.setNetFlow(result.getTotalInflow().subtract(result.getTotalOutflow()));

        LocalDate start = null, end = null;
        for (Transaction t : transactions) {
            if (t.getTransactionDate() == null) continue;
            if (start == null || t.getTransactionDate().isBefore(start)) start = t.getTransactionDate();
            if (end == null || t.getTransactionDate().isAfter(end)) end = t.getTransactionDate();
        }
        result.setStartDate(start);
        result.setEndDate(end);

        counterpartyMetrics.compute(result);
        // 补充调用现金流和构成分析
        new com.example.bankanalysis.metrics.CashFlowMetrics().compute(result);
        new com.example.bankanalysis.metrics.FlowCompositionMetrics().compute(result);

        File outFile = new File(output);
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();

        if ("MARKDOWN".equalsIgnoreCase(format)) {
            markdownReportGenerator.generate(result, output);
            log.info("Markdown 报告已生成: {}", output);
        } else {
            wordReportGenerator.generate(result, output);
            log.info("Word 报告已生成: {}", output);
        }
    }

    private BigDecimal sum(List<Transaction> list, boolean inflow) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : list) {
            BigDecimal v = inflow ? t.getCreditAmount() : t.getDebitAmount();
            if (v != null) total = total.add(v);
        }
        return total;
    }

    private String getArg(String[] args, String key, String defaultValue) {
        for (String a : args) {
            if (a.startsWith("--" + key + "=")) {
                return a.substring(("--" + key + "=").length());
            }
        }
        return defaultValue;
    }
}
