package com.example;

import com.example.Dao.Oracle.TsDictionaryItems;
import com.example.Dao.Oracle.UserTalColumns;
import com.example.Mapper.ExportDBExcelMapper;
import com.example.Service.CsvService;
import com.example.Service.ExcelService;
import com.example.Service.ExportDBExcelService;
import com.example.Service.Impl.ExcelServiceImpl;
import com.example.Service.Impl.WordServiceImpl;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = ExportOracleDbExcelUtilApplication.class)
@RunWith(SpringRunner.class)
public class ExportOracleDbExcelUtilApplicationTests {
    @Autowired
    ExportDBExcelMapper exportDBExcelMapper;
    @Autowired
    ExportDBExcelService exportDBExcelService;

    @Autowired
    WordServiceImpl wordService;
    @Autowired
    CsvService csvService;

    @Autowired
    ExcelServiceImpl excelService;

    /**
     * 测试读取csv数据
     */
    @Test
    public void testReaderCsv() throws IOException {
        String filePath = "C:\\Users\\hspcadmin\\Desktop\\csvTransferTxt\\PhysicalDataModel_1.csv";
//        Map<String, List<String>> stringListMap = csvService.readerCsv(filePath);
        csvService.csvTransferTxt(filePath);
//        System.out.println(stringListMap);
    }

    /**
     * 测试修改word页码，只需要传入word的文件路径
     */
    @Test
    public void testModifyWordCatalogPageNum() throws Exception {
        String filePath = "C:\\Users\\hspcadmin\\Desktop\\使用aspose刷新页码不正确.docx";
        wordService.modifyWordCatalogPageNum(filePath);
    }

    /**
     * 测试修改word中的页码
     */
    @Test
    public void addPageNum2Word() {
//        wordService.changePageNum2Word();
    }

    /**
     * 测试word转pdf
     */
    @Test
    public void testWordTransferPdf() {
//        wordService.wordTransferPdf();
    }

    /**
     * 测试获取目录的所有title
     */
    @Test
    public void testGetCatalogNames() throws IOException {
        String filePath = "C:\\Users\\hspcadmin\\Desktop\\使用aspose刷新页码不正确.docx";
        List<String> catalogNames = wordService.getCatalogNames(filePath);
        System.out.println(catalogNames);
    }


    /**
     * 导出excel到本地
     */
    @Test
    public void downLoad() throws IOException, InvalidFormatException {
        // 自定义导出文件夹格式示例：C:/xx/xx/
        String exportPath = "C:/Users/hspcadmin/Desktop/ExcelTool/";
        exportDBExcelService.download2LocalLengthWaysByPOI(exportPath);
    }

    /**
     * 导出oracle字典项excel到本地
     */
    @Test
    public void downLoadTsDictionaryItems() throws IOException {
        // 自定义导出文件夹格式示例：C:/xx/xx/
        String exportPath = "C:/Users/hspcadmin/Desktop/ExcelTool/";
        exportDBExcelService.downloadTsDictionaryItems2LengthWays(exportPath);
    }

    /**
     * 测试获取DATA_DEFAULT
     */
    @Test
    public void testGetDataDefault() {
        List<UserTalColumns> txbrl_cer_base = exportDBExcelMapper.getUserTalColumnsByTableName("TXBRL_CER_BASE");
        for (UserTalColumns userTalColumns : txbrl_cer_base) {
            String dataDefault = userTalColumns.getDataDefault();
            System.out.println(dataDefault);
        }
    }

    /**
     * 测试获取UserTalColumns对象
     */
    @Test
    public void testGetUserTalColumns() {
        List<UserTalColumns> tb_fundinfo_forreview = exportDBExcelMapper.getUserTalColumnsByTableName("tb_account");
        for (UserTalColumns userTalColumns : tb_fundinfo_forreview) {
            System.out.println(userTalColumns.toString());
        }

    }

    /**
     * 测试纵向转横向
     */
    @Test
    public void testTransferData() throws IOException, InvalidFormatException {
        List<String> objects = new ArrayList<>();
        exportDBExcelService.lengthWaysTransferCrossWise(" ", objects);
    }

    /**
     * 测试获取到所有的数据库表名
     */
    @Test
    public void getAllTablesName() {
        List<String> allTablesName = exportDBExcelService.getAllTablesName();
        for (String s : allTablesName) {
            System.out.println(s);
        }
    }

    /**
     * 测试获取表注释
     */
    @Test
    public void getTableComment() {
        System.out.println(exportDBExcelMapper.getTableComment("tb_account"));
    }

    /**
     * 测试获取表类型
     */
    @Test
    public void getTableType() {
        System.out.println(exportDBExcelMapper.getTableType("txbrl_function"));
    }

    /**
     * 测试获取TsDictionaryItems列表
     */
    @Test
    public void getTsDictionaryItems() {
        List<TsDictionaryItems> tsDictionaryItems = exportDBExcelService.getTsDictionaryItems();
        for (TsDictionaryItems tsDictionaryItem : tsDictionaryItems) {
            System.out.println(tsDictionaryItem.toString());
        }
    }

    /**
     * 测试从excel中读取
     */
    @Test
    public void testReadExcel() throws IOException {
        String filePath1 = "C:\\Users\\hspcadmin\\Desktop\\xbrl6需求\\读取表结构excel输出建表语句\\demo1.xlsx";
        String filePath2 = "C:\\Users\\hspcadmin\\Desktop\\xbrl6需求\\读取表结构excel输出建表语句\\createTable.txt";
        String filePath3 = "C:\\Users\\hspcadmin\\Desktop\\xbrl6需求\\读取表结构excel输出建表语句\\select.txt";
        excelService.readExcelTableStructure4ExportCreateTableStatement(filePath1,filePath2,filePath3);
    }





    /**
     * 测试任意
     */
    @Test
    public void test() {
        int[] arr = new int[3];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        int[] arr2 = new int[arr.length + 1];
        arr2[0] = 1;
        arr2[1] = 2;
        arr2[2] = 3;
        arr2[3] = 4;
        arr = arr2;
        for (int i : arr) {
            System.out.println(i);
        }
    }
}
