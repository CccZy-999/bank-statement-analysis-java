package com.example.bankanalysis.metrics;

import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.CounterpartyStat;
import com.example.bankanalysis.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CounterpartyMetrics {

    public void compute(AnalysisResult result) {
        Map<String, CounterpartyStat> inflowMap = new HashMap<>();
        Map<String, CounterpartyStat> outflowMap = new HashMap<>();

        for (Transaction t : result.getTransactions()) {
            String name = safe(t.getCounterpartyName());
            if (name.isEmpty()) name = "未知对手方";

            if (t.isInflow()) {
                CounterpartyStat s = inflowMap.computeIfAbsent(name, k -> newStat(k, t.getCounterpartyAccount()));
                s.setInflow(s.getInflow().add(t.getCreditAmount()));
                s.setInflowCount(s.getInflowCount() + 1);
            }
            if (t.isOutflow()) {
                CounterpartyStat s = outflowMap.computeIfAbsent(name, k -> newStat(k, t.getCounterpartyAccount()));
                s.setOutflow(s.getOutflow().add(t.getDebitAmount()));
                s.setOutflowCount(s.getOutflowCount() + 1);
            }
        }

        // 计算占比
        for (CounterpartyStat s : inflowMap.values()) {
            if (result.getTotalInflow().signum() > 0) {
                s.setInflowRatio(s.getInflow().divide(result.getTotalInflow(), 4, RoundingMode.HALF_UP));
            }
        }
        for (CounterpartyStat s : outflowMap.values()) {
            if (result.getTotalOutflow().signum() > 0) {
                s.setOutflowRatio(s.getOutflow().divide(result.getTotalOutflow(), 4, RoundingMode.HALF_UP));
            }
        }

        List<CounterpartyStat> inflowList = inflowMap.values().stream()
                .sorted(Comparator.comparing(CounterpartyStat::getInflow).reversed())
                .limit(20)
                .collect(Collectors.toList());

        List<CounterpartyStat> outflowList = outflowMap.values().stream()
                .sorted(Comparator.comparing(CounterpartyStat::getOutflow).reversed())
                .limit(20)
                .collect(Collectors.toList());

        result.setInflowCounterparties(inflowList);
        result.setOutflowCounterparties(outflowList);
    }

    private CounterpartyStat newStat(String name, String account) {
        CounterpartyStat s = new CounterpartyStat();
        s.setName(name);
        s.setAccount(account);
        return s;
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
