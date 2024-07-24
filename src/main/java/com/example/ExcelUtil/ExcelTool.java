package com.example.ExcelUtil;

import com.example.ExcelUtil.Model.ExportDO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTool {
    private static final String filePath = "C:\\Users\\hspcadmin\\Desktop\\TA资金清算余额报表（操伟）.xls";
    private static final Map<String, BigDecimal> companyValueMap = new HashMap<>();

    // 生成workbook对象
    public static HSSFWorkbook generateWorkBook(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return new HSSFWorkbook(fileInputStream);
    }

    // 读取excel内容
    public static List<ExportDO> readExcel(HSSFWorkbook workbook) {
        List<ExportDO> res = new ArrayList<>();
        HSSFSheet sheet = workbook.getSheet("余额报表");
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 6; i <= lastRowNum; i++) {
            HSSFRow row = sheet.getRow(i);
            HSSFCell cell = row.getCell(10);
            HSSFCell cell1 = row.getCell(1);
            String number = cell1.getStringCellValue();
            if (cell != null && number.length() >= 9) {
                double numericCellValue = cell.getNumericCellValue();
                // 用BigDecimal处理相加精度丢失问题
                BigDecimal numericBigDecimalValue = BigDecimal.valueOf(numericCellValue);
                if (numericCellValue != 0.0) {
                    // 科目号
                    String subNumber = number.substring(3, 9);
                    // 科目名称
                    HSSFCell cell2 = row.getCell(2);
                    String name = cell2.getStringCellValue();
                    if (name.equals("公司")) {
                        companyValueMap.put(subNumber, numericBigDecimalValue);
                    } else if (name.equals("金信基金") && companyValueMap.get(subNumber) != null){
                        BigDecimal add = numericBigDecimalValue.add(companyValueMap.get(subNumber));
                        numericBigDecimalValue = add;
                    }
                    // 最终生成字符串
                    ExportDO exportDO = new ExportDO(subNumber,name,numericBigDecimalValue);
                    res.add(exportDO);
                }
            }
        }
        return res;
    }

    // 主调用函数
    public static List<ExportDO> run() throws IOException {
        HSSFWorkbook workbook = generateWorkBook(filePath);
        return readExcel(workbook);
    }

    public static void main(String[] args) throws IOException {
        List<ExportDO> list = run();
        System.out.println(list);
        System.out.println(1&0);
    }
}
