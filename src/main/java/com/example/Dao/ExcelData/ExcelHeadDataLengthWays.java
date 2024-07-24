package com.example.Dao.ExcelData;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 纵向导出头
 */
@Data
public class ExcelHeadDataLengthWays {
    private static final int colNum = 8;
    @ExcelProperty("字段名")
    private String columnName;

    @ExcelProperty("数据类型")
    private String dataType;

    @ExcelProperty("长度")
    private String dataLength;

    @ExcelProperty("整数位")
    private String dataPrecision;

    @ExcelProperty("小数位")
    private String dataScale;

    @ExcelProperty("允许空值")
    private String nullAble;

    @ExcelProperty("缺省值")
    private String dataDefault;

    @ExcelProperty("备注")
    private String comments;

    public ExcelHeadDataLengthWays(){
        this.columnName = "字段名";
        this.dataType = "数据类型";
        this.dataLength = "长度";
        this.dataPrecision = "整数位";
        this.dataScale = "小数位";
        this.nullAble = "允许空值";
        this.dataDefault = "缺省值";
        this.comments = "备注";
    }
    public static int getColNum(){
        return colNum;
    }
}
