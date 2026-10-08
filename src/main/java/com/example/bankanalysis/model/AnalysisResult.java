package com.example.bankanalysis.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private String companyName;
    private String accountNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate reportDate = LocalDate.now();

    private BigDecimal totalInflow = BigDecimal.ZERO;
    private BigDecimal totalOutflow = BigDecimal.ZERO;
    private BigDecimal netFlow = BigDecimal.ZERO;

    private List<Transaction> transactions = new ArrayList<>();
    private DataQualityReport qualityReport = new DataQualityReport();

    private List<CounterpartyStat> inflowCounterparties = new ArrayList<>();
    private List<CounterpartyStat> outflowCounterparties = new ArrayList<>();

    private List<MonthlyCashFlow> monthlyFlows = new ArrayList<>();
    private List<FlowCategoryStat> inflowCategories = new ArrayList<>();
    private List<FlowCategoryStat> outflowCategories = new ArrayList<>();

    // getters / setters ...
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public BigDecimal getTotalInflow() { return totalInflow; }
    public void setTotalInflow(BigDecimal totalInflow) { this.totalInflow = totalInflow; }

    public BigDecimal getTotalOutflow() { return totalOutflow; }
    public void setTotalOutflow(BigDecimal totalOutflow) { this.totalOutflow = totalOutflow; }

    public BigDecimal getNetFlow() { return netFlow; }
    public void setNetFlow(BigDecimal netFlow) { this.netFlow = netFlow; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public DataQualityReport getQualityReport() { return qualityReport; }
    public void setQualityReport(DataQualityReport qualityReport) { this.qualityReport = qualityReport; }

    public List<CounterpartyStat> getInflowCounterparties() { return inflowCounterparties; }
    public void setInflowCounterparties(List<CounterpartyStat> inflowCounterparties) { this.inflowCounterparties = inflowCounterparties; }

    public List<CounterpartyStat> getOutflowCounterparties() { return outflowCounterparties; }
    public void setOutflowCounterparties(List<CounterpartyStat> outflowCounterparties) { this.outflowCounterparties = outflowCounterparties; }

    public List<MonthlyCashFlow> getMonthlyFlows() { return monthlyFlows; }
    public void setMonthlyFlows(List<MonthlyCashFlow> monthlyFlows) { this.monthlyFlows = monthlyFlows; }

    public List<FlowCategoryStat> getInflowCategories() { return inflowCategories; }
    public void setInflowCategories(List<FlowCategoryStat> inflowCategories) { this.inflowCategories = inflowCategories; }

    public List<FlowCategoryStat> getOutflowCategories() { return outflowCategories; }
    public void setOutflowCategories(List<FlowCategoryStat> outflowCategories) { this.outflowCategories = outflowCategories; }
}
