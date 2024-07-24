package com.example.Utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    /**
     * 返回String类型的日期
     */
    public static String getLocalTimeNow(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
