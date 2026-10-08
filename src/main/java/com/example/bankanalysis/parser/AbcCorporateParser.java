package com.example.bankanalysis.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class AbcCorporateParser extends AbstractExcelParser {

    private Map<String, List<String>> cachedMapping;

    @Override
    public String bankCode() { return "ABC"; }

    @Override
    public String accountType() { return "CORPORATE"; }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, List<String>> columnMapping() {
        if (cachedMapping != null) return cachedMapping;
        try (InputStream in = getClass().getResourceAsStream("/bank-formats/abc-corporate.yml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> root = mapper.readValue(in, Map.class);
            Object cm = root.get("columnMapping");
            cachedMapping = (Map<String, List<String>>) cm;
            return cachedMapping;
        } catch (Exception e) {
            throw new IllegalStateException("无法加载 abc-corporate.yml", e);
        }
    }
}
