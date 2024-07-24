package com.example.Service.Impl;

import com.aspose.words.License;
import com.example.Service.TsdEtlLogDetailService;
import com.example.Service.WordService;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zwt
 * @date 2024/05/15
 */
@Service
public class WordServiceImpl implements WordService {
    @Autowired
    TsdEtlLogDetailService tsdEtlLogDetailService;

    /**
     * 写入正确的页码到word中
     *
     * @param filePath         word文件的路径
     * @param title4PageNumMap 章节对应的正确的页码的map
     * @throws IOException 异常
     */
    @Override
    public void changePageNum2Word(String filePath, Map<String, Integer> title4PageNumMap) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        XWPFDocument doc = new XWPFDocument(fileInputStream);
        // 获取sdt,sdt相当于目录模块
        CTSdtBlock[] sdtArray = doc.getDocument().getBody().getSdtArray();
        // 获取block,相当于获取到了这个目录表
        CTSdtBlock block = sdtArray[0];
        // 获取该block下的内容
        CTSdtContentBlock sdtContent = block.getSdtContent();
        // 获取目录下的所有的CTP,CTP代表目录的一行
        CTP[] pArray = sdtContent.getPArray();
        // 对目录的每一行进行页码的更换
        for (CTP ctp : pArray) {
            // 因为该行内容（标题,页码）存储在<w:hyperlink>这个标签中,通过getHyperlinkArray获取该行的<w:hyperlink>标签的内容
            CTHyperlink[] hyperlinkArray = ctp.getHyperlinkArray();
            // 只有一个<w:hyperlink>标签，只需要获取第一个
            CTHyperlink ctHyperlink = hyperlinkArray[0];
            // 标题，页码都是存储在<w:r>标签中，所以获取到所有的<w:r>标签
            CTR[] rArray = ctHyperlink.getRArray();
            // 获取该目录行中的页码CTText对象
            CTText pageNumCtText = getCtText(rArray, 1);
            // 获取该目录行中的标题CTText对象
            CTText titleCtText = getCtText(rArray, 2);
            // 获取标题序号CTText对象
            CTText titlePageNumCtText = getCtText(rArray, 3);
            // 从title4PageNumMap获取对应title的正确页码
            Integer pageNum = title4PageNumMap.get(titlePageNumCtText.getStringValue() + titleCtText.getStringValue());
            // 更改页码<w:t>中的值
            pageNumCtText.setStringValue(pageNum.toString());
        }
        // 输出到word中
        doc.write(Files.newOutputStream(Paths.get(filePath)));
        doc.close();
    }

    /**
     * 根据x获取该目录行中不同的CTText对象(标题序号：3，标题：2，页码：1)
     *
     * @param rArray 所有的<w:r>标签集合
     * @param x      所在的索引位置
     * @return CTText <w:t></w:t> 对象
     */
    private static CTText getCtText(CTR[] rArray, int x) {
        // 存储页码的<w:r>标签存在最后一个
        CTR pageNumCtr = rArray[rArray.length - x];
        // 页码存在<w:r>标签中的<w:t>中
        CTText[] pageNumTArray = pageNumCtr.getTArray();
        // 存储页码的<w:t>标签存在最后一个
        return pageNumTArray[pageNumTArray.length - 1];
    }


    /**
     * word转pdf
     *
     * @param filePath       word的文件路径
     * @param outPutFilePath pdf的输出路径
     */
    @Override
    public void wordTransferPdf(String filePath, String outPutFilePath) {
        try {
            String s = "<License><Data><Products><Product>Aspose.Total for Java</Product><Product>Aspose.Words for Java</Product></Products><EditionType>Enterprise</EditionType><SubscriptionExpiry>20991231</SubscriptionExpiry><LicenseExpiry>20991231</LicenseExpiry><SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber></Data><Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature></License>";
            ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
            License license = new License();
            license.setLicense(is);
            com.aspose.words.Document document = new com.aspose.words.Document(filePath);
            document.save(outPutFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改word目录的页码
     *
     * @param filePath word文件路径
     * @throws Exception 异常
     */
    @Override
    public void modifyWordCatalogPageNum(String filePath) throws Exception {
        // 将word转pdf,获取pdf路径
        int index = filePath.lastIndexOf(".");
        // 导出pdf路径
        String outPutFilePath = filePath.substring(0, index) + ".pdf";
        wordTransferPdf(filePath, outPutFilePath);
        // 获取目录中的所有章节
        List<String> titles = getCatalogNames(filePath);
        // 获取正确页码
        Map<String, Integer> title4PageNumMap = tsdEtlLogDetailService.getPageNumByWordTransferPdf(titles, outPutFilePath);
        // 更改word
        changePageNum2Word(filePath, title4PageNumMap);
    }

    /**
     * 获取所有目录章节的名称
     *
     * @param filePath word文件的路径
     * @return 返回章节名称列表
     * @throws IOException 异常
     */
    @Override
    public List<String> getCatalogNames(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        XWPFDocument doc = new XWPFDocument(fileInputStream);
        List<String> titleList = new ArrayList<>();
        CTSdtBlock[] sdtArray = doc.getDocument().getBody().getSdtArray();
        // 目录
        CTSdtBlock block = sdtArray[0];
        // 获取该block下的内容
        CTSdtContentBlock sdtContent = block.getSdtContent();
        // 获取目录下的所有的CTP,CTP代表目录的一行
        CTP[] pArray = sdtContent.getPArray();
        // 对目录的每一行进行页码的更换
        for (CTP ctp : pArray) {
            // 因为该行内容（标题,页码）存储在<w:hyperlink>这个标签中,通过getHyperlinkArray获取该行的<w:hyperlink>标签的内容
            CTHyperlink[] hyperlinkArray = ctp.getHyperlinkArray();
            // 只有一个<w:hyperlink>标签，只需要获取第一个
            CTHyperlink ctHyperlink = hyperlinkArray[0];
            // 标题，标签都是存储在<w:r>标签中，所以获取到所有的<w:r>标签
            CTR[] rArray = ctHyperlink.getRArray();
            // 标题序号
            CTText ctText = getCtText(rArray, 3);
            String titleNum = ctText.getStringValue();
            ctText = getCtText(rArray, 2);
            String title = ctText.getStringValue();
            titleList.add(titleNum + title);
        }
        doc.close();
        return titleList;
    }

}
