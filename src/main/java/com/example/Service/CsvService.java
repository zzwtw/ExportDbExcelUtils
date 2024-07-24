package com.example.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CsvService {
    void csvTransferTxt(String filePath) throws IOException;
    Map<String, List<String>> readerCsv(String filePath) throws IOException;
    void write2Txt(Map<String, List<String>> csvContent, String filePath) throws IOException;
}
