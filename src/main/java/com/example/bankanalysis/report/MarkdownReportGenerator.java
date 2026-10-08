package com.example.bankanalysis.report;

import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.CounterpartyStat;
import com.example.bankanalysis.model.FlowCategoryStat;
import com.example.bankanalysis.model.MonthlyCashFlow;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Component
public class MarkdownReportGenerator {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generate(AnalysisResult result, String outputPath) throws Exception {
        try (Writer w = new FileWriter(outputPath)) {
            w.write("# 银行流水资金分析报告\n\n");
            w.write("**企业名称**：" + result.getCompanyName() + "\n\n");
            w.write("**账号**：" + result.getAccountNo() + "\n\n");
            w.write("**数据区间**：" +
                    (result.getStartDate() == null ? "-" : DTF.format(result.getStartDate())) +
                    " ~ " +
                    (result.getEndDate() == null ? "-" : DTF.format(result.getEndDate())) + "\n\n");
            w.write("**报告日期**：" + DTF.format(result.getReportDate()) + "\n\n");
            w.write("---\n\n");

            w.write("## 一、账户概览\n\n");
            w.write("| 指标 | 金额 |\n|---|---|\n");
            w.write("| 总进账 | " + fmt(result.getTotalInflow()) + " |\n");
            w.write("| 总出账 | " + fmt(result.getTotalOutflow()) + " |\n");
            w.write("| 净现金流 | " + fmt(result.getNetFlow()) + " |\n");
            w.write("| 交易笔数 | " + result.getTransactions().size() + " |\n\n");

            w.write("## 二、数据质量\n\n");
            w.write("- 余额连续性：" + result.getQualityReport().getBalanceCheckDetail() + "\n");
            w.write("- Benford 检验：" + result.getQualityReport().getBenfordDetail() + "\n\n");

            w.write("## 三、月度现金流\n\n");
            w.write("| 月份 | 进账 | 出账 |\n|---|---|---|\n");
            for (MonthlyCashFlow m : result.getMonthlyFlows()) {
                w.write("| " + m.getMonth() + " | " + fmt(m.getInflow()) + " | " + fmt(m.getOutflow()) + " |\n");
            }
            w.write("\n");

            w.write("## 四、进账来源 TOP\n\n");
            w.write("| 排名 | 对手方 | 金额 | 笔数 | 占比 |\n|---|---|---|---|---|\n");
            writeCounterpartyRows(w, result.getInflowCounterparties(), true);
            w.write("\n");

            w.write("## 五、出账对端 TOP\n\n");
            w.write("| 排名 | 对手方 | 金额 | 笔数 | 占比 |\n|---|---|---|---|---|\n");
            writeCounterpartyRows(w, result.getOutflowCounterparties(), false);
            w.write("\n");

            w.write("## 六、进账构成\n\n");
            w.write("| 类别 | 金额 | 笔数 | 占比 |\n|---|---|---|---|\n");
            for (FlowCategoryStat s : result.getInflowCategories()) {
                w.write("| " + s.getCategory() + " | " + fmt(s.getAmount()) + " | " + s.getCount() +
                        " | " + pct(s.getRatio()) + " |\n");
            }
            w.write("\n");

            w.write("## 七、出账构成\n\n");
            w.write("| 类别 | 金额 | 笔数 | 占比 |\n|---|---|---|---|\n");
            for (FlowCategoryStat s : result.getOutflowCategories()) {
                w.write("| " + s.getCategory() + " | " + fmt(s.getAmount()) + " | " + s.getCount() +
                        " | " + pct(s.getRatio()) + " |\n");
            }
            w.write("\n");
        }
    }

    private void writeCounterpartyRows(Writer w,
                                       java.util.List<CounterpartyStat> list,
                                       boolean inflow) throws Exception {
        int idx = 1;
        for (CounterpartyStat s : list) {
            BigDecimal amt = inflow ? s.getInflow() : s.getOutflow();
            BigDecimal ratio = inflow ? s.getInflowRatio() : s.getOutflowRatio();
            int cnt = inflow ? s.getInflowCount() : s.getOutflowCount();
            w.write("| " + (idx++) + " | " + safe(s.getName()) + " | " + fmt(amt) + " | " +
                    cnt + " | " + pct(ratio) + " |\n");
        }
    }

    private String fmt(BigDecimal v) {
        if (v == null) return "0.00";
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String pct(BigDecimal r) {
        if (r == null) return "0%";
        return r.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", "\\|"); }
}
