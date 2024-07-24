使用方法说明：
1.打开src/test/java/com/example/ExportOracleDbExcelUtilApplicationTests.java
2.运行downLoad方法即可导出相关数据库的所有表结构/运行downLoadTsDictionaryItems方法即可导出oracle字典项
3.等待导出完毕即可
4.如传入路径为Null，则默认导出到C:/exportExcel下
5.切换数据源，在application.yml中切换即可
oracle新增函数脚本到XBRL_FA3中
-------------------------------------脚本开始
CREATE OR REPLACE FUNCTION LONG_TO_CHAR(
       in_columnid NUMBER,
			 in_tablename VARCHAR2,
       in_table_name varchar,
       in_column varchar2
			 )
RETURN varchar AS
       text_c1 varchar2(32767);
       sql_cur varchar2(2000);
--set serveroutput on size 10000000000;--
begin
      DBMS_OUTPUT.ENABLE (buffer_size=>null);
      sql_cur := 'select '||in_column||' from '||in_table_name|| ' where COLUMN_ID = ' ||chr(39)|| in_columnid ||chr(39)|| ' and TABLE_NAME = ' ||chr(39)|| in_tablename||chr(39);
      dbms_output.put_line (sql_cur);
      execute immediate sql_cur into text_c1;
      text_c1 := substr(text_c1, 1, 4000);
      RETURN TEXT_C1;
END;
-------------------------------------脚本结束