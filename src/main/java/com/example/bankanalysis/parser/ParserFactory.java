package com.example.bankanalysis.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ParserFactory {

    private final Map<String, BankStatementParser> registry = new HashMap<>();

    @Autowired
    public ParserFactory(List<BankStatementParser> parsers) {
        for (BankStatementParser p : parsers) {
            registry.put(key(p.bankCode(), p.accountType()), p);
        }
    }

    public BankStatementParser getParser(String bank, String type) {
        BankStatementParser p = registry.get(key(bank, type));
        if (p == null) {
            throw new IllegalArgumentException("不支持的银行/账户类型组合: " + bank + "/" + type);
        }
        return p;
    }

    private String key(String bank, String type) {
        return (bank == null ? "" : bank.toUpperCase()) + ":" +
               (type == null ? "" : type.toUpperCase());
    }
}
