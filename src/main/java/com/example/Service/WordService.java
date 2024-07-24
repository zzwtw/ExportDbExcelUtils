package com.example.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WordService {
    void changePageNum2Word(String filePath, Map<String, Integer> title4PageNumMap) throws IOException;
    void wordTransferPdf(String filePath,String outPutFilePath) throws Exception;
    void modifyWordCatalogPageNum(String filePath) throws Exception;
    List<String> getCatalogNames(String filePath) throws IOException;
}
