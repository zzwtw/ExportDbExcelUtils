package com.example.Mapper;

import com.example.Dao.Oracle.TsDictionaryItems;
import com.example.Dao.Oracle.UserTalColumns;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExportDBExcelMapper {
    List<String> getAllTablesName();

    List<UserTalColumns> getUserTalColumnsByTableName(@Param("tableName") String tableName);

    String getDataDefault();

    /**
     * 获取数据表的注释
     *
     * @param tableName 表名
     * @return 注释
     */
    String getTableComment(@Param("tableName") String tableName);

    /**
     * 获取数据表的类型（table或者view）
     *
     * @param tableName 表名
     * @return 类型
     */
    String getTableType(@Param("tableName") String tableName);

    /**
     * 获取TsDictionaryItems对象列表
     *
     * @return TsDictionaryItems对象列表
     */
    List<TsDictionaryItems> getTsDictionaryItems();
}
