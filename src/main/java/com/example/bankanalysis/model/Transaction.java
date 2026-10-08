package com.example.bankanalysis.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private LocalDate transactionDate;
    private String transactionId;
    private BigDecimal debitAmount;      // 借方（出账）
    private BigDecimal creditAmount;     // 贷方（进账）
    private BigDecimal balance;          // 余额
    private String counterpartyName;     // 对方户名/单位名称
    private String counterpartyAccount;  // 对方账号
    private String summary;              // 摘要/用途
    private String transactionType;      // 交易类型
    private String accountNo;            // 本方账号
    private String rawLine;              // 原始行（可选）

    // getters / setters
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public BigDecimal getDebitAmount() { return debitAmount == null ? BigDecimal.ZERO : debitAmount; }
    public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount; }

    public BigDecimal getCreditAmount() { return creditAmount == null ? BigDecimal.ZERO : creditAmount; }
    public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }

    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }

    public String getRawLine() { return rawLine; }
    public void setRawLine(String rawLine) { this.rawLine = rawLine; }

    public boolean isInflow() {
        return creditAmount != null && creditAmount.signum() > 0
                && (debitAmount == null || debitAmount.signum() == 0);
    }

    public boolean isOutflow() {
        return debitAmount != null && debitAmount.signum() > 0;
    }
}
