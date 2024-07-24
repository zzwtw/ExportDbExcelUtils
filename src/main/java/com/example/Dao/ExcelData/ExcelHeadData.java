package com.example.Dao.ExcelData;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 需要导出的excel的头信息
 */

@Getter
@Setter
@EqualsAndHashCode
public class ExcelHeadData {

    @ExcelProperty("TABLE_NAME")
    private String tableName;

    @ExcelProperty("COLUMN_NAME")
    private String columnName;

    @ExcelProperty("DATA_TYPE")
    private String dataType;

    @ExcelProperty("DATA_TYPE_MOD")
    private String dataTypeMod;

    @ExcelProperty("DATA_TYPE_OWNER")
    private String dataTypeOwner;

    @ExcelProperty("DATA_LENGTH")
    private String dataLength;

    @ExcelProperty("DATA_PRECISION")
    private String dataPrecision;

    @ExcelProperty("DATA_SCALE")
    private String dataScale;

    @ExcelProperty("NULLABLE")
    private String nullAble;

    @ExcelProperty("COLUMN_ID")
    private String columnId;

    @ExcelProperty("DEFAULT_LENGTH")
    private String defaultLength;

    @ExcelProperty("DATA_DEFAULT")
    private String dataDefault;

    @ExcelProperty("NUM_DISTINCT")
    private String numDistinct;

    @ExcelProperty("LOW_VALUE")
    private String lowValue;

    @ExcelProperty("HIGH_VALUE")
    private String highValue;

    @ExcelProperty("DENSITY")
    private String density;

    @ExcelProperty("NUM_NULLS")
    private String numNulls;

    @ExcelProperty("NUM_BUCKETS")
    private String numBuckets;

    @ExcelProperty("LAST_ANALYZED")
    private String lastAnalyzed;

    @ExcelProperty("SAMPLE_SIZE")
    private String sampleSize;

    @ExcelProperty("CHARACTER_SET_NAME")
    private String characterSetName;

    @ExcelProperty("CHAR_COL_DECL_LENGTH")
    private String charColDeclLength;

    @ExcelProperty("GLOBAL_STATS")
    private String globalStats;

    @ExcelProperty("USER_STATS")
    private String userStats;

    @ExcelProperty("AVG_COL_LEN")
    private String avgColLen;

    @ExcelProperty("CHAR_LENGTH")
    private String charLength;

    @ExcelProperty("CHAR_USED")
    private String charUsed;

    @ExcelProperty("V80_FMT_IMAGE")
    private String v80FmtImage;

    @ExcelProperty("DATA_UPGRADED")
    private String dataUpGraded;

    @ExcelProperty("HISTOGRAM")
    private String histogram;

    @ExcelProperty("DEFAULT_ON_NULL")
    private String defaultOnNull;

    @ExcelProperty("IDENTITY_COLUMN")
    private String identityColumn;

    @ExcelProperty("EVALUATION_EDITION")
    private String evaluationEdition;

    @ExcelProperty("UNUSABLE_BEFORE")
    private String unusableBefore;

    @ExcelProperty("UNUSABLE_BEGINNING")
    private String unusableBeginning;

    @ExcelProperty("COLLATION")
    private String collation;

    @ExcelProperty("COMMENTS")
    private String comments;

}
