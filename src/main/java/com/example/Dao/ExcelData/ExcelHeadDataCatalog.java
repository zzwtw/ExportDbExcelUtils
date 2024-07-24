package com.example.Dao.ExcelData;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelHeadDataCatalog {
    @ExcelProperty("数据表名")
    public String tableName;
    @ExcelProperty("表中文名")
    public String chineseName;
    @ExcelProperty("类型")
    public String type;
    @ExcelProperty("修改时间")
    public String updateTime;
    @ExcelProperty("版本")
    public String version;

    public ExcelHeadDataCatalog(){
        this.tableName = "数据表名";
        this.chineseName = "表中文名";
        this.type = "类型";
        this.updateTime = "修改时间";
        this.version = "版本";
    }
}
