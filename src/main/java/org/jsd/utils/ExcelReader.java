package org.jsd.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    private static final Logger log = LogManager.getLogger(ExcelReader.class);
    
    public static Object[][] readExcelData(String filePath, String sheetName) {
        String resolvedPath = resolvePath(filePath);
        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();
            
            Object[][] data = new Object[rowCount][colCount];
            
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    data[i-1][j] = getCellValue(cell);
                }
            }
            
            log.info("Excel data read successfully from: {}", filePath);
            return data;
            
        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
            throw new RuntimeException("Excel file not found or corrupted", e);
        }
    }
    
    public static List<Map<String, String>> readExcelAsMap(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        String resolvedPath = resolvePath(filePath);
        
        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> rowData = new HashMap<>();
                
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    String header = getCellValue(headerRow.getCell(j)).toString();
                    String value = getCellValue(row.getCell(j)).toString();
                    rowData.put(header, value);
                }
                data.add(rowData);
            }
            
            log.info("Excel data read as map from: {}", filePath);
            return data;
            
        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
            throw new RuntimeException("Excel file not found or corrupted", e);
        }
    }
    
    public static String getCellData(String filePath, String sheetName, int rowIndex, int colIndex) {
        String resolvedPath = resolvePath(filePath);
        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowIndex);
            Cell cell = row.getCell(colIndex);
            
            return getCellValue(cell).toString();
            
        } catch (IOException e) {
            log.error("Failed to read cell data: {}", filePath, e);
            return "";
        }
    }
    
    public static String getCellDataByHeader(String filePath, String sheetName, int rowIndex, String headerName) {
        String resolvedPath = resolvePath(filePath);
        try (FileInputStream fis = new FileInputStream(resolvedPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);
            
            // Find column index by header name
            int colIndex = -1;
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                if (getCellValue(headerRow.getCell(i)).toString().equals(headerName)) {
                    colIndex = i;
                    break;
                }
            }
            
            if (colIndex == -1) {
                log.warn("Header '{}' not found in sheet '{}'", headerName, sheetName);
                return "";
            }
            
            Row dataRow = sheet.getRow(rowIndex);
            Cell cell = dataRow.getCell(colIndex);
            
            return getCellValue(cell).toString();
            
        } catch (IOException e) {
            log.error("Failed to read cell data by header: {}", filePath, e);
            return "";
        }
    }
    
    private static Object getCellValue(Cell cell) {
        if (cell == null) return "";
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? 
                cell.getDateCellValue() : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
    
    private static String resolvePath(String filePath) {
        // If path starts with classpath indicator, resolve from resources
        if (filePath.startsWith("testdata/") || filePath.startsWith("configs/")) {
            try {
                return ExcelReader.class.getClassLoader().getResource(filePath).getPath();
            } catch (Exception e) {
                log.warn("Could not resolve classpath, using original path: {}", filePath);
                return filePath;
            }
        }
        return filePath;
    }
}