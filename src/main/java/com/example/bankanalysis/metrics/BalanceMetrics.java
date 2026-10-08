package com.example.bankanalysis.metrics;

import com.example.bankanalysis.model.AnalysisResult;
import com.example.bankanalysis.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class BalanceMetrics {

    public BigDecimal averageBalance(List<Transaction> transactions) {
        BigDecimal sum = BigDecimal.ZERO;
        int n = 0;
        for (Transaction t : transactions) {
            if (t.getBalance() != null) { sum = sum.add(t.getBalance()); n++; }
        }
        return n == 0 ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal maxBalance(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getBalance)
                .filter(java.util.Objects::nonNull)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public BigDecimal minBalance(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getBalance)
                .filter(java.util.Objects::nonNull)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public void compute(AnalysisResult result) {
        // 预留，可扩展写入 result
    }
}
