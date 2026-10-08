package com.example.bankanalysis.parser;

import com.example.bankanalysis.model.Transaction;

import java.io.InputStream;
import java.util.List;

public interface BankStatementParser {
    List<Transaction> parse(InputStream inputStream) throws Exception;

    String bankCode();
    String accountType();
}
