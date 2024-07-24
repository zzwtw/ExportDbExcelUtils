package com.example.Controller;

import com.example.Dao.Oracle.UserTalColumns;
import com.example.Service.ExportDBExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController // == @ResponseBody + @Controller
public class ExportDBExcelController {
    @Autowired
    ExportDBExcelService exportDBExcelService;

    /**
     * 获取表空间下的所有表名
     * @return 表名列表
     */
    @RequestMapping("/getAllTablesName")
    @ResponseBody
    public List<String> getAllTablesName(){
        return exportDBExcelService.getAllTablesName();
    }

    /**
     * 文件下载（失败了会返回一个有部分数据的Excel）
     */
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        exportDBExcelService.downLoadExcel(response);
    }

    /**
     * 根据表明获取该表的userTabColumns表
     * @return UserTalColumns对象列表
     */
    @RequestMapping("/getUserTalColumnsByTableName")
    public List<UserTalColumns> getUserTalColumnByTableName(){
        List<UserTalColumns> userTalColumnsList = exportDBExcelService.getUserTalColumnsByTableName("TB_FUNDINFO_FORREVIEW");
        return userTalColumnsList;
    }

}
