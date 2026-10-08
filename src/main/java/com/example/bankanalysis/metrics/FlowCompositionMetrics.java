package com.example.bankanalysis.metrics;

import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.FlowCategoryStat;
import com.example.bankanalysis.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlowCompositionMetrics {

    public void compute(AnalysisResult result) {
        Map<String, FlowCategoryStat> inflow = new HashMap<>();
        Map<String, FlowCategoryStat> outflow = new HashMap<>();

        for (Transaction t : result.getTransactions()) {
            String cat = categorize(t.getSummary());
            if (t.isInflow()) {
                FlowCategoryStat s = inflow.computeIfAbsent(cat, FlowCategoryStat::new);
                s.setAmount(s.getAmount().add(t.getCreditAmount()));
                s.setCount(s.getCount() + 1);
            }
            if (t.isOutflow()) {
                FlowCategoryStat s = outflow.computeIfAbsent(cat, FlowCategoryStat::new);
                s.setAmount(s.getAmount().add(t.getDebitAmount()));
                s.setCount(s.getCount() + 1);
            }
        }

        for (FlowCategoryStat s : inflow.values()) {
            if (result.getTotalInflow().signum() > 0) {
                s.setRatio(s.getAmount().divide(result.getTotalInflow(), 4, RoundingMode.HALF_UP));
            }
        }
        for (FlowCategoryStat s : outflow.values()) {
            if (result.getTotalOutflow().signum() > 0) {
                s.setRatio(s.getAmount().divide(result.getTotalOutflow(), 4, RoundingMode.HALF_UP));
            }
        }

        result.setInflowCategories(inflow.values().stream()
                .sorted(Comparator.comparing(FlowCategoryStat::getAmount).reversed())
                .collect(Collectors.toList()));
        result.setOutflowCategories(outflow.values().stream()
                .sorted(Comparator.comparing(FlowCategoryStat::getAmount).reversed())
                .collect(Collectors.toList()));
    }

    private String categorize(String summary) {
        if (summary == null || summary.isEmpty()) return "其他";
        String s = summary.toLowerCase();
        if (s.contains("工资") || s.contains("薪")) return "工资薪酬";
        if (s.contains("税")) return "税费";
        if (s.contains("利息")) return "利息";
        if (s.contains("贷") || s.contains("借款") || s.contains("放款")) return "借贷融资";
        if (s.contains("货款") || s.contains("采购")) return "采购付款";
        if (s.contains("销售") || s.contains("收入") || s.contains("回款")) return "销售回款";
        if (s.contains("租")) return "租赁";
        if (s.contains("报销") || s.contains("费用")) return "费用报销";
        if (s.contains("转账") || s.contains("汇款")) return "转账汇款";
        return "其他";
    }
}
