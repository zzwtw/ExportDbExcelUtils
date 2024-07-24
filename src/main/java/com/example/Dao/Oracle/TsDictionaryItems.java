package com.example.Dao.Oracle;

import jdk.nashorn.internal.objects.annotations.Property;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

/**
 * @author zwt
 */
@Data
public class TsDictionaryItems {
    /**
     * 字典代码
     */
    public String dicNo;
    /**
     * 字典名称
     */
    public String dicName;
    /**
     * 类型
     */
    public String dicType;
    /**
     * 字典项
     */
    public String dicCode;
    /**
     * 字典项名称
     */
    public String dicItem;
}
