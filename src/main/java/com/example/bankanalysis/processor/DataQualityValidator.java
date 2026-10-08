package com.example.bankanalysis.processor;

import com.example.bankanalysis.model.DataQualityReport;
import com.example.bankanalysis.model.Transaction;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class DataQualityValidator {

    private static final double[] BENFORD_EXPECTED = {
            0.0,        // 索引0不使用
            0.30103,    // 1
            0.17609,    // 2
            0.12494,    // 3
            0.09691,    // 4
            0.07918,    // 5
            0.06695,    // 6
            0.05799,    // 7
            0.05115,    // 8
            0.04576     // 9
    };

    public DataQualityReport validate(List<Transaction> transactions) {
        DataQualityReport report = new DataQualityReport();

        // 1. 余额连续性
        int discontinuities = 0;
        StringBuilder sb = new StringBuilder();
        BigDecimal prevBalance = null;
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            BigDecimal bal = t.getBalance();
            if (bal == null || prevBalance == null) {
                prevBalance = bal;
                continue;
            }
            BigDecimal delta = t.getCreditAmount().subtract(t.getDebitAmount());
            BigDecimal expected = prevBalance.add(delta);
            if (expected.subtract(bal).abs().compareTo(new BigDecimal("0.01")) > 0) {
                discontinuities++;
                if (sb.length() < 500) {
                    sb.append("第").append(i + 1).append("笔: 期望余额=")
                      .append(expected.setScale(2, RoundingMode.HALF_UP))
                      .append("，实际余额=").append(bal).append("\n");
                }
            }
            prevBalance = bal;
        }
        report.setBalanceContinuous(discontinuities == 0);
        report.setDiscontinuousPoints(discontinuities);
        report.setBalanceCheckDetail(discontinuities == 0
                ? "全部交易余额连续，数据完整。"
                : "发现 " + discontinuities + " 处余额不连续，明细：\n" + sb);

        // 2. Benford 检验
        int[] counts = new int[10];
        int total = 0;
        for (Transaction t : transactions) {
            BigDecimal amt = t.getCreditAmount().signum() > 0 ? t.getCreditAmount() : t.getDebitAmount();
            if (amt == null || amt.signum() == 0) continue;
            String s = amt.abs().stripTrailingZeros().toPlainString().replace(".", "");
            s = s.replaceFirst("^0+", "");
            if (s.isEmpty()) continue;
            int first = s.charAt(0) - '0';
            if (first >= 1 && first <= 9) { counts[first]++; total++; }
        }
        double chi2 = 0;
        if (total > 0) {
            for (int d = 1; d <= 9; d++) {
                double expected = BENFORD_EXPECTED[d] * total;
                double diff = counts[d] - expected;
                chi2 += (diff * diff) / expected;
            }
        }
        double pValue = 1.0;
        try {
            ChiSquaredDistribution dist = new ChiSquaredDistribution(8);
            pValue = 1.0 - dist.cumulativeProbability(chi2);
        } catch (Exception ignored) {}
        report.setBenfordChiSquare(chi2);
        report.setBenfordPass(pValue > 0.05);
        report.setBenfordDetail(String.format(
                "卡方值=%.4f，p=%.4f，%s（样本 %d 笔）",
                chi2, pValue,
                pValue > 0.05 ? "符合 Benford 定律" : "偏离 Benford 定律，建议关注",
                total));

        return report;
    }
}
