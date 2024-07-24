package com.example.Dao.Oracle;

import com.alibaba.excel.annotation.ExcelProperty;
import javafx.beans.binding.ObjectExpression;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class UserTalColumns {

    private String columnName;

    private String dataType;

    private String dataLength;

    private String dataPrecision;

    private String dataScale;

    private String nullAble;

    private String dataDefault;

    private String comments;
}
