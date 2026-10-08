package com.example.bankanalysis.report;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Texts;
import com.example.bankanalysis.chart.ChartGenerator;
import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.CounterpartyStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class WordReportGenerator {

    @Autowired private ChartGenerator chartGenerator;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public void generate(AnalysisResult result, String outputPath) throws Exception {
        // 优先从 classpath 加载模板；若不存在则生成一个最小可用报告（纯文本）
        var tplStream = getClass().getResourceAsStream("/templates/report-template.docx");
        File output = new File(outputPath);
        output.getParentFile().mkdirs();

        if (tplStream == null) {
            // 退化为 Markdown，避免 Word 模板缺失导致失败
            new MarkdownReportGenerator().generate(result,
                    outputPath.replaceAll("\\.docx$", ".md"));
            System.err.println("[WARN] 未找到 report-template.docx，已输出 Markdown 报告到 " +
                    outputPath.replaceAll("\\.docx$", ".md"));
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("companyName", result.getCompanyName());
        data.put("accountNo", result.getAccountNo());
        data.put("reportDate", DTF.format(result.getReportDate()));
        data.put("startDate", result.getStartDate() == null ? "-" : DTF.format(result.getStartDate()));
        data.put("endDate", result.getEndDate() == null ? "-" : DTF.format(result.getEndDate()));
        data.put("totalInflow", fmt(result.getTotalInflow()));
        data.put("totalOutflow", fmt(result.getTotalOutflow()));
        data.put("netFlow", fmt(result.getNetFlow()));
        data.put("balanceCheckResult",
                result.getQualityReport().isBalanceContinuous() ? "余额连续，数据完整" :
                        "发现 " + result.getQualityReport().getDiscontinuousPoints() + " 处不连续");
        data.put("benfordResult", result.getQualityReport().getBenfordDetail());

        // 图表
        BufferedImage monthly = chartGenerator.monthlyTrendChart(result.getMonthlyFlows());
        data.put("monthlyTrendChart", Pictures.ofBufferedImage(monthly, PictureType.PNG)
                .size(600, 360).create());

        if (!result.getInflowCategories().isEmpty()) {
            data.put("inflowCompositionChart", Pictures.ofBufferedImage(
                    chartGenerator.compositionChart(result.getInflowCategories(), "进账构成"),
                    PictureType.PNG).size(480, 320).create());
        }
        if (!result.getOutflowCategories().isEmpty()) {
            data.put("outflowCompositionChart", Pictures.ofBufferedImage(
                    chartGenerator.compositionChart(result.getOutflowCategories(), "出账构成"),
                    PictureType.PNG).size(480, 320).create());
        }
        if (!result.getInflowCounterparties().isEmpty()) {
            data.put("topCounterpartyChart", Pictures.ofBufferedImage(
                    chartGenerator.topCounterpartyChart(result.getInflowCounterparties(), "主要进账对手方"),
                    PictureType.PNG).size(600, 360).create());
        }

        // 表格
        data.put("topInflowCounterparties",
                buildTable(result.getInflowCounterparties(), true));
        data.put("topOutflowCounterparties",
                buildTable(result.getOutflowCounterparties(), false));

        try (XWPFTemplate template = XWPFTemplate.compile(tplStream).render(data);
             FileOutputStream fos = new FileOutputStream(output)) {
            template.write(fos);
        }
    }

    private TableRenderData buildTable(List<CounterpartyStat> list, boolean inflow) {
        List<RowRenderData> rows = new ArrayList<>();
        rows.add(Rows.of("排名", "对手方", "金额", "笔数", "占比").center().create());
        int idx = 1;
        for (CounterpartyStat s : list) {
            BigDecimal amt = inflow ? s.getInflow() : s.getOutflow();
            BigDecimal ratio = inflow ? s.getInflowRatio() : s.getOutflowRatio();
            int count = inflow ? s.getInflowCount() : s.getOutflowCount();
            rows.add(Rows.of(
                    String.valueOf(idx++),
                    s.getName(),
                    fmt(amt),
                    String.valueOf(count),
                    ratio.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) + "%"
            ).create());
        }
        return Rows.of().createTable(rows);
    }

    private String fmt(BigDecimal v) {
        if (v == null) return "0.00";
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
