package com.example.Dao.Const;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TsDictionaryItemsConst {
    // Oracle字典项excel名称
    public static final String TS_DICTIONARY_ITEMS_EXCEL_NAME = "OracleTsDictionaryItems.xlsx";
    // Oracle字典项sheet名称
    public static final String TS_DICTIONARY_ITEMS_SHEET_NAME = "OracleTsDictionaryItems";
    // sheet表列数
    public static final int TS_DICTIONARY_ITEMS_COLUMNS_NUMBER = 5;
    // 列头元素
    public static final String DIC_NO = "字典代码";
    public static final String DIC_NAME = "字典名称";
    public static final String DIC_V_TYPE = "类型";
    public static final String DIC_CODE = "字典项";
    public static final String DIC_ITEM = "字典项名称";

    public static List<String> getTsDictionaryItemsColumnHeadList(){
        List<String> columnHeadList = new ArrayList<>();
        columnHeadList.add(DIC_NO);
        columnHeadList.add(DIC_NAME);
        columnHeadList.add(DIC_V_TYPE);
        columnHeadList.add(DIC_CODE);
        columnHeadList.add(DIC_ITEM);
        return columnHeadList;
    }

}
