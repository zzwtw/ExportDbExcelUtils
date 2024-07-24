package com.example.ExcelUtil.Model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExportDO {
    // 科目号
    String number;
    // 科目名称
    String name;
    // 贷方金额
    BigDecimal numeric;

    public ExportDO(String subNumber, String number, BigDecimal numericBigDecimalValue) {
        this.number = subNumber;
        this.name = number;
        this.numeric = numericBigDecimalValue;
    }

    @Override
    public String toString() {
        return "ExportDO{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", numeric=" + numeric +
                '}';
    }
}
