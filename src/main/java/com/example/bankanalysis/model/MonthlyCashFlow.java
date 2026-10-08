package com.example.bankanalysis.model;

import java.math.BigDecimal;

public class MonthlyCashFlow {
    private String month;         // yyyy-MM
    private BigDecimal inflow = BigDecimal.ZERO;
    private BigDecimal outflow = BigDecimal.ZERO;

    public MonthlyCashFlow() {}
    public MonthlyCashFlow(String month) { this.month = month; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getInflow() { return inflow; }
    public void setInflow(BigDecimal inflow) { this.inflow = inflow; }

    public BigDecimal getOutflow() { return outflow; }
    public void setOutflow(BigDecimal outflow) { this.outflow = outflow; }
}
