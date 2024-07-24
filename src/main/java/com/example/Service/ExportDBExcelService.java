package com.example.Service;

import com.example.Dao.Oracle.TsDictionaryItems;
import com.example.Dao.Oracle.UserTalColumns;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author zwt
 */
public interface ExportDBExcelService {
    List<String> getAllTablesName();

    void downLoadExcel(HttpServletResponse response) throws IOException;

    List<UserTalColumns> getUserTalColumnsByTableName(String tableName);

    void download2LocalLengthWays(String exportPath) throws SQLException, IOException, InvalidFormatException;

    void download2LocalLengthWaysByPOI(String exportPath) throws IOException, InvalidFormatException;

    List<String> getAllFilterTablesName();

    void lengthWaysTransferCrossWise(String filePth, List<String> allFilterTableName) throws IOException, InvalidFormatException;

    void downloadTsDictionaryItems2LengthWays(String exportPath) throws IOException;

    List<TsDictionaryItems> getTsDictionaryItems();
}
