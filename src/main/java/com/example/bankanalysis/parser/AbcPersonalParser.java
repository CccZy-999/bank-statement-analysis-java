package com.example.bankanalysis.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class AbcPersonalParser extends AbstractExcelParser {

    private Map<String, List<String>> cachedMapping;

    @Override
    public String bankCode() { return "ABC"; }

    @Override
    public String accountType() { return "PERSONAL"; }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, List<String>> columnMapping() {
        if (cachedMapping != null) return cachedMapping;
        try (InputStream in = getClass().getResourceAsStream("/bank-formats/abc-personal.yml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> root = mapper.readValue(in, Map.class);
            cachedMapping = (Map<String, List<String>>) root.get("columnMapping");
            return cachedMapping;
        } catch (Exception e) {
            throw new IllegalStateException("无法加载 abc-personal.yml", e);
        }
    }
}
