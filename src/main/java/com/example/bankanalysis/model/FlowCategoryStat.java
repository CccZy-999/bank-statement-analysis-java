package com.example.bankanalysis.model;

import java.math.BigDecimal;

public class FlowCategoryStat {
    private String category;      // 摘要归类
    private BigDecimal amount = BigDecimal.ZERO;
    private int count;
    private BigDecimal ratio = BigDecimal.ZERO;   // 占比

    public FlowCategoryStat() {}
    public FlowCategoryStat(String category) { this.category = category; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public BigDecimal getRatio() { return ratio; }
    public void setRatio(BigDecimal ratio) { this.ratio = ratio; }
}
