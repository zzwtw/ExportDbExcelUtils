package com.example.Service.Impl;

import com.example.Service.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final String CREATE_TABLE = "create table";
    private static final String SELECT = "select";
    private static final String FROM = "from";
    private static final String LEFT_ROUND = "(";
    private static final String RIGHT_ROUND = ")";
    private static final String DEFAULT_NULL = "DEFAULT null";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";

    /**
     * 读取excel中的表结构，构造建表语句
     * create table scott.student_info (
     * sno         number(10) DEFAULT null,
     * sname       varchar2(10) DEFAULT null,
     * sex         varchar2(2)DEFAULT null,
     * create_date date
     * );
     */
    @Override
    public void readExcelTableStructure4ExportCreateTableStatement(String filePath1, String filePath2, String filePath3) throws IOException {
        // excel输入流
        FileInputStream fileInputStream = new FileInputStream(filePath1);
        // createTable 输出流
        FileOutputStream fileOutputStream1 = new FileOutputStream(filePath2);
        // select 输出流
        FileOutputStream fileOutputStream2 = new FileOutputStream(filePath3);
        // select 输出流
        // 获取excel
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        for (int j = 0; j < 25; j++) {
            // 获取sheet
            Sheet sheet = workbook.getSheetAt(j);
            // 排除重复字段
            Set<String> hs = new HashSet<>();
            // 获取表名
            String tableName = sheet.getSheetName();
            System.out.println("drop table " + tableName);
            int rowNum = sheet.getLastRowNum();
            StringBuilder createTableStatement = new StringBuilder(CREATE_TABLE + " " + tableName + " " + LEFT_ROUND + '\n');
            StringBuilder selectStatement = new StringBuilder(SELECT + " ");
            for (int i = 0; i <= rowNum; i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(0);
                String columnName = cell.getStringCellValue();
                if (!hs.contains(columnName)) {
                    hs.add(columnName);
                } else {
                    continue;
                }
                if (columnName != null && !columnName.equals("")) {
                    createTableStatement.append(columnName).append(" ");
                    cell = row.getCell(2);
                    String type = cell.getStringCellValue();
                    String num = type.substring(2, type.length() - 1);
                    type = type.substring(0, 1);
                    switch (type) {
                        case "C":
                            type = "varchar2(" + num + ")" + " DEFAULT null";
                            break;
                        case "N":
                            type = "number(" + num + ")" + " DEFAULT null";
                            break;
                        case "D":
                            type = "date" + " DEFAULT null";
                            break;
                    }
                    createTableStatement.append(type).append(COMMA).append('\n');
                    selectStatement.append(columnName).append(COMMA).append(" ");
                }
            }
            createTableStatement.deleteCharAt(createTableStatement.length() - 2);
            selectStatement.deleteCharAt(selectStatement.length() - 2);
            createTableStatement.append(RIGHT_ROUND).append(SEMICOLON).append('\n').append('\n');
            selectStatement.append(FROM).append(" ").append(tableName).append('\n').append('\n');
            String s1 = createTableStatement.toString();
            String s2 = selectStatement.toString();
            byte[] bytes1 = s1.getBytes();
            byte[] bytes2 = s2.getBytes();
            fileOutputStream1.write(bytes1);
            fileOutputStream2.write(bytes2);
        }
    }
}
