package com.example.bankanalysis.parser;

import com.example.bankanalysis.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class AbstractExcelParser implements BankStatementParser {

    protected static final Logger log = LoggerFactory.getLogger(AbstractExcelParser.class);

    protected static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );

    /** 子类提供列映射候选表 */
    protected abstract Map<String, List<String>> columnMapping();
    
    public List<Transaction> parse(InputStream inputStream, String filename) throws Exception {
        if (filename != null && filename.toLowerCase().endsWith(".csv")) {
            return parseCsv(inputStream);
        }
        return parseExcel(inputStream);
    }
    @Override
    public List<Transaction> parse(InputStream inputStream) throws Exception {
        List<Transaction> result = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(inputStream)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) return result;

            // 找表头行
            int headerRowIdx = findHeaderRow(sheet);
            if (headerRowIdx < 0) {
                log.warn("未找到表头行");
                return result;
            }
            Row headerRow = sheet.getRow(headerRowIdx);

            Map<String, Integer> colIndex = new HashMap<>();
            Map<String, List<String>> mapping = columnMapping();

            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                String header = getCellString(headerRow.getCell(c));
                if (header == null) continue;
                String norm = normalizeHeader(header);
                for (Map.Entry<String, List<String>> e : mapping.entrySet()) {
                    if (colIndex.containsKey(e.getKey())) continue;
                    for (String candidate : e.getValue()) {
                        if (normalizeHeader(candidate).equals(norm)) {
                            colIndex.put(e.getKey(), c);
                            break;
                        }
                    }
                }
            }

            log.info("识别到列映射: {}", colIndex);

            for (int r = headerRowIdx + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String dateVal = getCellString(row.getCell(colIndex.getOrDefault("transactionDate", -1)));
                String creditVal = getCellString(row.getCell(colIndex.getOrDefault("creditAmount", -1)));
                String debitVal = getCellString(row.getCell(colIndex.getOrDefault("debitAmount", -1)));
                if (isBlank(dateVal) && isBlank(creditVal) && isBlank(debitVal)) continue;

                Transaction t = new Transaction();
                t.setTransactionDate(parseDate(dateVal));
                t.setTransactionId(getCellString(row.getCell(colIndex.getOrDefault("transactionId", -1))));
                t.setCreditAmount(parseAmount(creditVal));
                t.setDebitAmount(parseAmount(debitVal));
                t.setBalance(parseAmount(getCellString(row.getCell(colIndex.getOrDefault("balance", -1)))));
                t.setCounterpartyName(getCellString(row.getCell(colIndex.getOrDefault("counterpartyName", -1))));
                t.setCounterpartyAccount(getCellString(row.getCell(colIndex.getOrDefault("counterpartyAccount", -1))));
                t.setSummary(getCellString(row.getCell(colIndex.getOrDefault("summary", -1))));
                t.setTransactionType(getCellString(row.getCell(colIndex.getOrDefault("transactionType", -1))));

                result.add(t);
            }
        }
        return result;
    }

    protected int findHeaderRow(Sheet sheet) {
        // 在前 10 行中找到包含"日期"或"交易"字样的行
        for (int r = sheet.getFirstRowNum(); r <= Math.min(sheet.getLastRowNum(), 10); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = 0; c < row.getLastCellNum(); c++) {
                String v = getCellString(row.getCell(c));
                if (v != null && (v.contains("日期") || v.contains("交易") || v.contains("摘要"))) {
                    return r;
                }
            }
        }
        return -1;
    }

    protected String getCellString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) return String.valueOf((long) d);
                return BigDecimal.valueOf(d).toPlainString();
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); }
                catch (Exception ex) { return String.valueOf(cell.getNumericCellValue()); }
            default: return null;
        }
    }

    protected String normalizeHeader(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", "").replace("（", "(").replace("）", ")");
    }

    protected boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    protected LocalDate parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        String v = s.trim();
        for (DateTimeFormatter f : DATE_FORMATS) {
            try { return LocalDate.parse(v, f); } catch (Exception ignored) {}
        }
        // 尝试从 Excel 数字格式的日期字符串（含时间）
        try {
            String datePart = v.split(" ")[0];
            for (DateTimeFormatter f : DATE_FORMATS) {
                try { return LocalDate.parse(datePart, f); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return null;
    }

    protected BigDecimal parseAmount(String s) {
        if (s == null || s.isEmpty()) return BigDecimal.ZERO;
        String cleaned = s.replace(",", "").replace("，", "").replace(" ", "")
                .replace("¥", "").replace("￥", "").replace("元", "");
        if (cleaned.isEmpty() || "-".equals(cleaned)) return BigDecimal.ZERO;
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
