package com.example.bankanalysis.processor;

import com.example.bankanalysis.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DataStandardizer {

    public List<Transaction> standardize(List<Transaction> input) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : input) {
            // 归一化：借方/贷方至少一个非空
            if (t.getDebitAmount() == null && t.getCreditAmount() == null) continue;

            // 若贷方/借方同时有值，保留两者（某些银行用正负号区分）
            if (t.getDebitAmount() != null && t.getDebitAmount().signum() < 0) {
                t.setCreditAmount(t.getCreditAmount().add(t.getDebitAmount().abs()));
                t.setDebitAmount(BigDecimal.ZERO);
            }

            // 对手方名称清洗
            if (t.getCounterpartyName() != null) {
                t.setCounterpartyName(t.getCounterpartyName().trim()
                        .replaceAll("\\s+", ""));
            }
            // 摘要清洗
            if (t.getSummary() != null) {
                t.setSummary(t.getSummary().trim().replaceAll("\\s+", " "));
            }
            out.add(t);
        }
        out.sort(Comparator.comparing(Transaction::getTransactionDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return out;
    }
}
