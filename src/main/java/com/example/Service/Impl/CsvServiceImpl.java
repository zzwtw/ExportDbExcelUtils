package com.example.Service.Impl;

import com.csvreader.CsvReader;
import com.example.Service.CsvService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zwt
 * @date 2024/05/16
 */
@Service
public class CsvServiceImpl implements CsvService {
    /**
     * csv转txt
     * @param filePath csv文件路径
     * @throws IOException 异常
     */
    @Override
    public void csvTransferTxt(String filePath) throws IOException {
        // 读取csv文件
        Map<String, List<String>> csvContent = readerCsv(filePath);
        // 写入txt
        write2Txt(csvContent,filePath);
    }

    /**
     * 读取csv内容
     * @param filePath csv文件路径
     * @return csv文件内容
     * @throws IOException 异常
     */
    @Override
    public Map<String, List<String>> readerCsv(String filePath) throws IOException {
        // 第一参数：读取文件的路径 第二个参数：分隔符（不懂仔细查看引用百度百科的那段话） 第三个参数：字符集
        CsvReader csvReader = new CsvReader(filePath, ',', Charset.forName("gbk"));
        Map<String, List<String>> map = new HashMap<>();
        // 如果你的文件没有表头，这行不用执行
        // 这行不要是为了从表头的下一行读，也就是过滤表头
        csvReader.readHeaders();

        // 读取每行的内容
        while (csvReader.readRecord()) {
            // 获取表名
            String tableName = csvReader.get(0);
            tableName = tableName.toUpperCase();
            // 获取字段 + 备注
            String field = csvReader.get(3);
            String comment = csvReader.get(10);
            String res = "comment on column  " + tableName +"." + field +"  is '" + comment + "';";
            if (map.get(tableName) == null){
                List<String> resList = new ArrayList<>();
                resList.add(res);
                map.put(tableName, resList);
            }else {
                List<String> resList = map.get(tableName);
                resList.add(res);
            }
        }
        return map;
    }

    /**
     * 写入txt
     * @param csvContent csv内容
     * @param filePath csv文件路径
     * @throws IOException 异常
     */
    @Override
    public void write2Txt(Map<String, List<String>> csvContent, String filePath) throws IOException {
        int index = filePath.lastIndexOf("\\");
        filePath = filePath.substring(0,index + 1);
        for (String tableName :  csvContent.keySet()){
            String txtFilePath = filePath + tableName + ".txt";
            FileWriter fileWriter = new FileWriter(txtFilePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            List<String> contents = csvContent.get(tableName);
            for (String content : contents) {
                bufferedWriter.write(content + '\n');
            }
            bufferedWriter.flush();
            fileWriter.close();
        }

    }
}
