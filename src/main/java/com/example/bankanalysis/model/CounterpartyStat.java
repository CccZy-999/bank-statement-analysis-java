package com.example.bankanalysis.model;

import java.math.BigDecimal;

public class CounterpartyStat {
    private String name;
    private String account;
    private BigDecimal inflow = BigDecimal.ZERO;
    private BigDecimal outflow = BigDecimal.ZERO;
    private int inflowCount;
    private int outflowCount;
    private BigDecimal inflowRatio = BigDecimal.ZERO;   // 占全部进账比例
    private BigDecimal outflowRatio = BigDecimal.ZERO;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public BigDecimal getInflow() { return inflow; }
    public void setInflow(BigDecimal inflow) { this.inflow = inflow; }

    public BigDecimal getOutflow() { return outflow; }
    public void setOutflow(BigDecimal outflow) { this.outflow = outflow; }

    public int getInflowCount() { return inflowCount; }
    public void setInflowCount(int inflowCount) { this.inflowCount = inflowCount; }

    public int getOutflowCount() { return outflowCount; }
    public void setOutflowCount(int outflowCount) { this.outflowCount = outflowCount; }

    public BigDecimal getInflowRatio() { return inflowRatio; }
    public void setInflowRatio(BigDecimal inflowRatio) { this.inflowRatio = inflowRatio; }

    public BigDecimal getOutflowRatio() { return outflowRatio; }
    public void setOutflowRatio(BigDecimal outflowRatio) { this.outflowRatio = outflowRatio; }
}
