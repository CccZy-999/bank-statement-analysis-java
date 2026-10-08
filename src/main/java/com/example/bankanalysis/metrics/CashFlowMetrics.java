package com.example.bankanalysis.metrics;

import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.MonthlyCashFlow;
import com.example.bankanalysis.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CashFlowMetrics {

    public void compute(AnalysisResult result) {
        Map<String, MonthlyCashFlow> map = new TreeMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Transaction t : result.getTransactions()) {
            if (t.getTransactionDate() == null) continue;
            String m = t.getTransactionDate().format(fmt);
            MonthlyCashFlow f = map.computeIfAbsent(m, MonthlyCashFlow::new);
            if (t.isInflow()) f.setInflow(f.getInflow().add(t.getCreditAmount()));
            if (t.isOutflow()) f.setOutflow(f.getOutflow().add(t.getDebitAmount()));
        }
        result.setMonthlyFlows(new ArrayList<>(map.values()));
    }
}
