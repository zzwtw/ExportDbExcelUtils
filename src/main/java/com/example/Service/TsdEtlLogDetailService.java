package com.example.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TsdEtlLogDetailService {
    Map<String, Integer> getPageNumByWordTransferPdf(List<String> keyWordList, String filePath) throws IOException;
}
