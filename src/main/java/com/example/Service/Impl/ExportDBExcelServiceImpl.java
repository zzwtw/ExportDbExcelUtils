package com.example.Service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.Dao.Const.CatalogColumnsName;
import com.example.Dao.Const.TsDictionaryItemsConst;
import com.example.Dao.ExcelData.ExcelHeadData;
import com.example.Dao.ExcelData.ExcelHeadDataCatalog;
import com.example.Dao.ExcelData.ExcelHeadDataLengthWays;
import com.example.Dao.Oracle.TsDictionaryItems;
import com.example.Dao.Oracle.UserTalColumns;
import com.example.Mapper.ExportDBExcelMapper;
import com.example.Service.ExportDBExcelService;
import com.example.Utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author zwt
 * @date 2024/04/30
 */
@Slf4j
@Service
public class ExportDBExcelServiceImpl implements ExportDBExcelService {
    @Autowired
    ExportDBExcelMapper exportDBExcelMapper;

    // 目录页名称
    private static final String catalogSheetName = "目录页";
    // excel表sheetName的最大长度
    private static final int excelSheetNameMaxLength = 31;
    // excel列宽单位长度
    private static final int columnWidthUnitLength = 256;
    // excel导出version信息
    private static final String version = "1.0.0";
    // 默认导出路径
    private static final String defaultExportPath = "C:/ExcelTool/";
    // 纵向excel名称
    private static final String lengthWaysExcelName = "纵向.xlsx";
    // 横向excel名称
    private static final String crossWiseExcelName = "横向.xlsx";

    /**
     * 获取该表空间下的所有表名称
     *
     * @return 表名称列表
     */
    @Override
    public List<String> getAllTablesName() {
        return exportDBExcelMapper.getAllTablesName();
    }

    /**
     * 目前是一个循环导出的方法
     *
     * @param response HttpServletResponse
     */
    @Override
    public void downLoadExcel(HttpServletResponse response) throws IOException {
        // 获取所有的表名
        List<String> allFilterTablesName = getAllFilterTablesName();
        // 开始逐个表的导出Excel，先用一个表名测
        String tableName = allFilterTablesName.get(0);
        List<UserTalColumns> userTalColumnsList = getUserTalColumnsByTableName(tableName);
        simpleWrite(response, userTalColumnsList);
    }

    /**
     * 下载纵向excel到本地，easyExcel版本
     *
     * @param exportPath 文件夹路径 example: ./ExcelTool/，为空，则采用当前目录的相对路径下创建的新目录
     */
    public void download2LocalLengthWays(String exportPath) throws IOException, InvalidFormatException {
        // 获取所有的表名
        List<String> allFilterTablesName = getAllFilterTablesName();
        // 开始逐个表的导出Excel
        log.info("=====开始导出纵向excel=====");
        if (exportPath == null) {
            exportPath = "./ExcelTool/";
        }
        mkFile(exportPath);
        String filePath = exportPath + "纵向" + ".xlsx";
        // 导出多个sheet的excel文件
        File file = new File(filePath);
        try (ExcelWriter excelWriter = EasyExcel.write(file).build()) {
            // 先生成目录页
            WriteSheet writeSheetCatalog;
            writeSheetCatalog = EasyExcel.writerSheet("目录页").sheetName("目录页").head(ExcelHeadDataCatalog.class).build();
            List<ExcelHeadDataCatalog> catalogList = new ArrayList<>();
            excelWriter.write(catalogList, writeSheetCatalog);
            WriteSheet writeSheet;
            for (int i = 0; i < 5; i++) {
                String tableName = allFilterTablesName.get(i);
                List<UserTalColumns> userTalColumnsList = getUserTalColumnsByTableName(tableName);
                // 构建sheet对象
                writeSheet = EasyExcel.writerSheet(tableName).sheetName(tableName).head(ExcelHeadDataLengthWays.class).build();
                // 写出sheet数据
                excelWriter.write(data(userTalColumnsList), writeSheet);
            }
            // 关流
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("=====导出纵向excel完毕=====");
        log.info("=====开始导出横向excel=====");
        lengthWaysTransferCrossWise(filePath, allFilterTablesName);
        log.info("=====导出横向excel完毕=====");
    }

    /**
     * 下载纵向excel到本地，poi版本
     *
     * @param exportPath 文件夹路径 example: ./ExcelTool/，为空，则采用当前目录的相对路径下创建的新目录
     */
    @Override
    public void download2LocalLengthWaysByPOI(String exportPath) throws IOException, InvalidFormatException {
        // 获取所有的表名
        List<String> allFilterTablesName = getAllFilterTablesName();
        // 开始逐个表的导出Excel
        log.info("=====开始导出纵向excel=====");
        if (exportPath == null) {
            exportPath = defaultExportPath;
        }
        mkFile(exportPath);
        String filePath = exportPath + lengthWaysExcelName;
        // workbook
        Workbook workbook = new XSSFWorkbook();
        // 创建目录表
        createCatalog(workbook, filePath);
        // 头单元格样式
        CellStyle headCellStyle = setHeadCellStyle(workbook);
        // 普通单元格样式
        CellStyle normalCellStyle = setNormalCellStyle(workbook);
        for (String tableName : allFilterTablesName) {
            // 每张表创建一个sheet
            Sheet sheet = workbook.createSheet(tableName);
            log.info(allFilterTablesName.indexOf(tableName) + "正在生成" + tableName);
            // 设置纵向excel列宽
            setLengthWaysColumnLength(sheet);
            // 获取需要插入的数据
            List<UserTalColumns> userTalColumnsList = getUserTalColumnsByTableName(tableName);
            // 行数
            int rowCount = userTalColumnsList.size();
            // 列数
            int colCount = ExcelHeadDataLengthWays.getColNum();
            // 开始将数据写入到sheet，留第一行为空行
            for (int row = 1; row < rowCount + 1; row++) {
                // 创建行
                Row rowTemp = sheet.createRow(row);
                // 准备要插入该行的数据列表
                List<String> colData = getColData(userTalColumnsList.get(row - 1));
                for (int col = 0; col < colCount; col++) {
                    Cell cell = rowTemp.createCell(col);
                    cell.setCellValue(colData.get(col));
                    // 给每个单元格设置样式，如果是头单元格，则走设置头单元格的逻辑，其他的则普通逻辑
                    if (row == 1) {
                        cell.setCellStyle(headCellStyle);
                    } else {
                        cell.setCellStyle(normalCellStyle);
                    }
                }
            }
            // 向sheet中插入目录表的超链接
            createSheet2CatalogHyperLink(workbook, sheet, filePath, colCount);
            // 创建输出流
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            // 写出文件
            workbook.write(fileOutputStream);
        }
        // 向目录表中插入跳转sheet超链接
        insertIntoCatalog(workbook, allFilterTablesName, filePath);
        // 关闭workbook
        workbook.close();
        log.info("=====导出纵向excel完毕=====");
        log.info("=====开始导出横向excel=====");
        lengthWaysTransferCrossWise(filePath, allFilterTablesName);
        log.info("=====导出横向excel完毕=====");
    }
    /**
     * 纵向转横向，行列转换
     */
    @Override
    public void lengthWaysTransferCrossWise(String filePath, List<String> allFilterTableName) throws IOException, InvalidFormatException {
        // 纵向excel文件的导出路径
        int idx = filePath.lastIndexOf("/");
        String newDirPath = filePath.substring(0, idx + 1);
        mkFile(newDirPath);
        // 横向excel文件导出路径
        String newFilePath = newDirPath + crossWiseExcelName;
        // 创建横向.xlsx(如果不存在)
        File file = new File(newFilePath);
        file.createNewFile();
        // 与横向.xlsx相关联的workbook
        Workbook transposeWorkbook = new XSSFWorkbook();
        // 创建纵向excel引用workbook
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        // 创建目录表
        createCatalog(transposeWorkbook, newFilePath);
        // 头单元格样式
        CellStyle headCellStyle = setHeadCellStyle(transposeWorkbook);
        // 普通单元格样式
        CellStyle normalCellStyle = setNormalCellStyle(transposeWorkbook);
        for (int l = 1; l < allFilterTableName.size() + 1; l++) {
            // 读取Excel文件的逻辑
            // 转置第l个Sheet
            Sheet sheet = workbook.getSheetAt(l);
            log.info("正在生成" + sheet.getSheetName() + "这张sheet表");
            // 原表行数
            int rowCount = sheet.getPhysicalNumberOfRows();
            // 原表列数
            int colCount = sheet.getRow(1).getLastCellNum();
            // 用于存储从纵向excel文件中读取到的数据
            String[][] data = new String[rowCount][colCount];
            // 读取纵向excel数据
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    data[i - 1][j] = cell.getStringCellValue();
                }
            }
            // 行列转换，写入新的横向excel中
            String tableName = sheet.getSheetName();
            Sheet transposeSheet = transposeWorkbook.createSheet(tableName);
            // 转换表行数
            int transposeRowCount = colCount;
            // 转换表列数
            int transposeColCount = rowCount - 1;
            // 设置横向excel的sheet表列宽
            setCrossWiseColumnLength(transposeSheet, rowCount);
            for (int i = 0; i < transposeRowCount; i++) {
                Row transposeRow = transposeSheet.createRow(i + 1);
                for (int j = 0; j < transposeColCount; j++) {
                    Cell transposeCell = transposeRow.createCell(j);
                    transposeCell.setCellValue(data[j][i]);
                    // 设置单元格样式
                    if (j == 0) {
                        transposeCell.setCellStyle(headCellStyle);
                    } else {
                        transposeCell.setCellStyle(normalCellStyle);
                    }
                }
            }
            // 向sheet中插入目录表的超链接
            createSheet2CatalogHyperLink(transposeWorkbook, transposeSheet, newFilePath, transposeColCount);
            // 写入新的excel
            FileOutputStream fileOutputStream = new FileOutputStream(newFilePath);
            transposeWorkbook.write(fileOutputStream);
        }
        // 向目录表中插入内容
        insertIntoCatalog(transposeWorkbook, allFilterTableName, newFilePath);
        // 关闭
        transposeWorkbook.close();
    }

    /**
     * 导出oracle字典项excel
     *
     * @param exportPath 导出路径
     * @throws IOException IOEXCEPTION异常
     */
    @Override
    public void downloadTsDictionaryItems2LengthWays(String exportPath) throws IOException {
        // 如果路径文件夹不存在
        if (exportPath == null) {
            exportPath = defaultExportPath;
        }
        mkFile(exportPath);
        String filePath = exportPath + TsDictionaryItemsConst.TS_DICTIONARY_ITEMS_EXCEL_NAME;
        // 创建workbook对象
        Workbook workbook = new XSSFWorkbook();
        // 创建sheet
        Sheet sheet = workbook.createSheet(TsDictionaryItemsConst.TS_DICTIONARY_ITEMS_SHEET_NAME);
        // 设置sheet列宽
        setTsDictionaryItemsColumnLength(sheet);
        // 获取数据
        List<TsDictionaryItems> tsDictionaryItems = getTsDictionaryItems();
        int rowCount = tsDictionaryItems.size();
        // 插入数据
        for (int r = 0; r <= rowCount; r++) {
            Row row = sheet.createRow(r);
            if (r == 0) {
                setTsDictionaryItemsColumnHead(row, workbook);
            } else {
                TsDictionaryItems tsDictionaryItem = tsDictionaryItems.get(r - 1);
                setTsDictionaryItemsCellValue(tsDictionaryItem, row, workbook);
            }
        }
        // 输出流
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        workbook.write(fileOutputStream);
        workbook.close();
    }

    /**
     * 设置TsDictionaryItems sheet列宽
     *
     * @param sheet TsDictionaryItems表
     */
    public void setTsDictionaryItemsColumnLength(Sheet sheet) {
        sheet.setColumnWidth(0, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(1, 50 * columnWidthUnitLength);
        sheet.setColumnWidth(2, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(3, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(4, 50 * columnWidthUnitLength);
    }

    /**
     * 设置TsDictionaryItems列头
     *
     * @param row 行
     */
    public void setTsDictionaryItemsColumnHead(Row row, Workbook workbook) {
        CellStyle cellStyle = setHeadCellStyle(workbook);
        List<String> columnHeadList = TsDictionaryItemsConst.getTsDictionaryItemsColumnHeadList();
        int colCount = TsDictionaryItemsConst.TS_DICTIONARY_ITEMS_COLUMNS_NUMBER;
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columnHeadList.get(i));
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 设置TsDictionaryItems sheet的值
     *
     * @param tsDictionaryItem TsDictionaryItems对象
     * @param row              行
     */
    public void setTsDictionaryItemsCellValue(TsDictionaryItems tsDictionaryItem, Row row, Workbook workbook) {
        CellStyle cellStyle = setNormalCellStyle(workbook);
        List<String> tsDictionaryItemList = getTsDictionaryItemList(tsDictionaryItem);
        int colCount = TsDictionaryItemsConst.TS_DICTIONARY_ITEMS_COLUMNS_NUMBER;
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(tsDictionaryItemList.get(i));
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 将TsDictionaryItems对象元素拆分到一个List中
     *
     * @param tsDictionaryItem TsDictionaryItems对象
     * @return 拆分之后的列表
     */
    public List<String> getTsDictionaryItemList(TsDictionaryItems tsDictionaryItem) {
        List<String> res = new ArrayList<>();
        res.add(tsDictionaryItem.getDicNo());
        res.add(tsDictionaryItem.getDicName());
        res.add(tsDictionaryItem.getDicType());
        res.add(tsDictionaryItem.getDicCode());
        res.add(tsDictionaryItem.getDicItem());
        return res;
    }

    /**
     * 获取TsDictionaryItems对象列表
     *
     * @return TsDictionaryItems对象列表
     */
    @Override
    public List<TsDictionaryItems> getTsDictionaryItems() {
        return exportDBExcelMapper.getTsDictionaryItems();
    }


    /**
     * 创建目录表结构
     *
     * @param workbook 相当于打开了一个excel
     * @param filePath 文件路径
     */
    public void createCatalog(Workbook workbook, String filePath) {
        try {
            Sheet sheet = workbook.createSheet(catalogSheetName);
            // 设置目录页的列宽
            sheet.setColumnWidth(0, 40 * columnWidthUnitLength);
            sheet.setColumnWidth(1, 40 * columnWidthUnitLength);
            sheet.setColumnWidth(2, 10 * columnWidthUnitLength);
            sheet.setColumnWidth(3, 30 * columnWidthUnitLength);
            sheet.setColumnWidth(4, 10 * columnWidthUnitLength);
            Row row = sheet.createRow(0);
            List<String> catalogList = new ArrayList<>();
            ExcelHeadDataCatalog catalog = new ExcelHeadDataCatalog();
            catalogList.add(catalog.getTableName());
            catalogList.add(catalog.getChineseName());
            catalogList.add(catalog.getType());
            catalogList.add(catalog.getUpdateTime());
            catalogList.add(catalog.getVersion());
            for (int i = 0; i < catalogList.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(catalogList.get(i));
                // 设置目录页头单元格样式
                CellStyle cellStyle = setHeadCellStyle(workbook);
                cell.setCellStyle(cellStyle);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入目录表,设置sheet超链接
     *
     * @param workbook            代表一个excel表
     * @param allFilterTablesName 所有的表名,也就是此excel中所有的sheet的名称列表
     * @param filePath            此excel的导出路径
     */
    public void insertIntoCatalog(Workbook workbook, List<String> allFilterTablesName, String filePath) {
        // 给allFilterTablesName列表进行表名排序
        Collections.sort(allFilterTablesName);
        Sheet sheet = workbook.getSheetAt(0);
        int index = 1;
        for (String sheetName : allFilterTablesName) {
            // 获取目录页需要插入的数据对象列表
            Map<String, String> columnData = getCatalogColumnData(sheetName);
            // 插入行
            Row row = sheet.createRow(index);
            // 向目录表中插入数据
            setValue2CatalogColumns(row, columnData, workbook, sheetName, filePath);
            index++;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向目录表中插入数据
     *
     * @param row        当前行
     * @param columnData 当前行数据
     * @param workbook   excel对象
     * @param sheetName  sheet名称
     * @param filePath   excel路径
     */
    public void setValue2CatalogColumns(Row row, Map<String, String> columnData, Workbook workbook, String sheetName, String filePath) {
        Cell cell = row.createCell(0);
        cell.setCellValue(columnData.get(CatalogColumnsName.sheetName));
        // 给此数据表名（第一个单元格）单元格设置超链接
        creatCellHyperLink(workbook, sheetName, cell, filePath);
        cell = row.createCell(1);
        cell.setCellValue(columnData.get(CatalogColumnsName.chineseName));
        cell = row.createCell(2);
        cell.setCellValue(columnData.get(CatalogColumnsName.type));
        cell = row.createCell(3);
        cell.setCellValue(columnData.get(CatalogColumnsName.time));
        cell = row.createCell(4);
        cell.setCellValue(columnData.get(CatalogColumnsName.version));
    }

    /**
     * 获取目录页需要插入的数据对象列表
     *
     * @param sheetName 表名
     * @return 数据对象列表
     */
    public Map<String, String> getCatalogColumnData(String sheetName) {
        // 获取该表的表中文名
        String chineseName = getTableComment(sheetName);
        // 获取类型
        String type = getTableType(sheetName);
        // 列数据列表
        Map<String, String> columnData = new HashMap<>();
        columnData.put(CatalogColumnsName.sheetName, sheetName);
        columnData.put(CatalogColumnsName.chineseName, chineseName);
        columnData.put(CatalogColumnsName.type, type);
        // 获取当前时间
        columnData.put(CatalogColumnsName.time, TimeUtil.getLocalTimeNow());
        // 设置版本信息
        columnData.put(CatalogColumnsName.version, version);
        return columnData;
    }

    /**
     * 获取数据表的注释
     *
     * @param tableName 表名
     * @return 注释
     */
    public String getTableComment(String tableName) {
        return exportDBExcelMapper.getTableComment(tableName);
    }

    /**
     * 获取数据表的类型
     *
     * @param tableName 表名
     * @return 类型
     */
    public String getTableType(String tableName) {
        return exportDBExcelMapper.getTableType(tableName);
    }

    /**
     * 给每张sheet添加返回目录页的超链接
     *
     * @param workbook 代表一个excel表
     * @param sheet    一张sheet表
     * @param fileName excel文件路径
     */
    public void createSheet2CatalogHyperLink(Workbook workbook, Sheet sheet, String fileName, int count) {
        // 给每张sheet的0,0的单元格设置成目录页的超链接
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("返回" + catalogSheetName);
        // sheetName代表需要跳转的sheet的名称
        creatCellHyperLink(workbook, catalogSheetName, cell, fileName);
        // 合并单元格
        mergeBackCatalogLinkCell(sheet, count);
    }

    /**
     * 合并返回目录页单元格，并设置边框
     */
    public void mergeBackCatalogLinkCell(Sheet sheet, int count) {
        // 合并单元格
        CellRangeAddress cellAddresses = new CellRangeAddress(0, 0, 0, count - 1);
        sheet.addMergedRegion(cellAddresses);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
    }

    /**
     * 给单元格设置超链接
     *
     * @param workbook  代表一个excel表
     * @param sheetName 表名称,sheet名称
     * @param cell      设置超链接的单元格
     */
    private void creatCellHyperLink(Workbook workbook, String sheetName, Cell cell, String fileName) {
        // 创建超链接
        try {
            // 判断原因:excel的sheet的名称最大长度为31,poi导入时,如果sheetName超过31,会截取31以及以内的部分
            if (sheetName.length() > excelSheetNameMaxLength) {
                sheetName = sheetName.substring(0, excelSheetNameMaxLength);
            }
            CreationHelper createHelper = workbook.getSheet(sheetName).getWorkbook().getCreationHelper();
            Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
            // 类型设置为 FILE ,兼容wps需要加上fileName
            hyperlink.setAddress("#" + sheetName + "!A1");
            cell.setHyperlink(hyperlink);
            cell.setCellStyle(createHyperlinkCellStyle(workbook));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改超链接单元格的样式,高亮,蓝色
     *
     * @param workbook 代表excel表
     * @return 单元格样式
     */
    private static CellStyle createHyperlinkCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 获取每一行的数据列表
     *
     * @param userTalColumns userTalColumns对象
     * @return 行数据列表
     */
    private static List<String> getColData(UserTalColumns userTalColumns) {
        List<String> ret = new ArrayList<>();
        ret.add(userTalColumns.getColumnName());
        ret.add(userTalColumns.getDataType());
        ret.add(userTalColumns.getDataLength());
        ret.add(userTalColumns.getDataPrecision());
        ret.add(userTalColumns.getDataScale());
        ret.add(userTalColumns.getNullAble());
        ret.add(userTalColumns.getDataDefault());
        ret.add(userTalColumns.getComments());
        return ret;
    }

    /**
     * 根据表名获取该表对应的字段结构表，包括头信息
     *
     * @param tableName 需要导出的表名称
     * @return UserTalColumns对象列表
     */
    @Override
    public List<UserTalColumns> getUserTalColumnsByTableName(String tableName) {
        List<UserTalColumns> userTalColumnsRetList = new ArrayList<>();
        // 设置纵向头信息，并设置列宽
        UserTalColumns userTalColumns = new UserTalColumns();
        ExcelHeadDataLengthWays excelHeadData = new ExcelHeadDataLengthWays();
        BeanUtils.copyProperties(excelHeadData, userTalColumns);
        userTalColumnsRetList.add(userTalColumns);
        List<UserTalColumns> userTalColumnsList = exportDBExcelMapper.getUserTalColumnsByTableName(tableName);
        userTalColumnsRetList.addAll(userTalColumnsList);
        return userTalColumnsRetList;
    }

    /**
     * 设置纵向excel列宽
     */
    public void setLengthWaysColumnLength(Sheet sheet) {
        sheet.setColumnWidth(0, 40 * columnWidthUnitLength);
        sheet.setColumnWidth(1, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(2, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(3, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(4, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(5, 10 * columnWidthUnitLength);
        sheet.setColumnWidth(6, 50 * columnWidthUnitLength);
        sheet.setColumnWidth(7, 100 * columnWidthUnitLength);
    }

    /**
     * 设置横向excel列宽
     */
    public void setCrossWiseColumnLength(Sheet sheet, int rowCount) {
        // 设置表头列宽
        sheet.setColumnWidth(0, 10 * columnWidthUnitLength);
        // 因为横向excel的列数是会根据表的字段数量而变化的
        for (int i = 1; i < rowCount; i++) {
            sheet.setColumnWidth(i, 50 * columnWidthUnitLength);
        }
    }

    /**
     * 写入excel并导出到浏览器
     *
     * @param response           HttpServletResponse
     * @param userTalColumnsList userTalColumns对象列表
     * @throws IOException
     */
    public void simpleWrite(HttpServletResponse response, List<UserTalColumns> userTalColumnsList) throws IOException {
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelHeadData.class).sheet("模板").doWrite(data(userTalColumnsList));
    }

    /**
     * 获取数据
     *
     * @param userTalColumnsList userTalColumnsList对象列表
     * @return ExcelHeadData对象列表
     */
    private List<ExcelHeadData> data(List<UserTalColumns> userTalColumnsList) {
        List<ExcelHeadData> list = ListUtils.newArrayList();
        int len = userTalColumnsList.size();
        for (int i = 0; i < len; i++) {
            ExcelHeadData data = new ExcelHeadData();
            BeanUtils.copyProperties(userTalColumnsList.get(i), data);
            list.add(data);
        }
        return list;
    }

    /**
     * 获取表空间下的所有过滤好的表名
     *
     * @return 过滤表名列表
     */
    public List<String> getAllFilterTablesName() {
        // 获取所有的表名
        List<String> allTablesName = getAllTablesName();
        // 过滤掉备份的表名，_BAK结尾，数字结尾
        List<String> allFilterTablesName = new ArrayList<>();
        for (String s : allTablesName) {
            int idx = s.lastIndexOf("_") + 1;
            String bak = "BAK";
            int bakLen = bak.length();
            char[] ch = s.toCharArray();
            if (idx + bakLen <= s.length()) {
                String s1 = s.substring(idx, idx + bakLen);
                if (!(s1.equals(bak) || ch[idx] >= '0' && ch[idx] <= '9')) {
                    allFilterTablesName.add(s);
                }
            } else if (!(ch[idx] >= '0' && ch[idx] <= '9')) {
                allFilterTablesName.add(s);
            }
        }
        return allFilterTablesName;
    }

    /**
     * 创建文件夹
     *
     * @param filePath 文件夹路径
     */
    public static void mkFile(String filePath) {
        File newFile = new File(filePath);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
    }

    /**
     * 设置列头单元格样式
     */
    public CellStyle setHeadCellStyle(Workbook workbook) {
        // 创建单元格样式对象
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置边框
        setBorder(cellStyle);
        // 设置背景色
        cellStyle.setFillForegroundColor((short) 13);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    /**
     * 设置普通单元格样式
     */
    public CellStyle setNormalCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置边框
        setBorder(cellStyle);
        return cellStyle;
    }

    /**
     * 给单元格设置边框
     */
    public void setBorder(CellStyle cellStyle) {
        // 设置底边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 设置左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 设置右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        // 设置顶边框
        cellStyle.setBorderTop(BorderStyle.THIN);
    }
}
