package org.spring.springboot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String DAY = "yyyy-MM-dd";
    public static Date strToDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DAY);
            return sdf.parse(dateStr);
        }catch (ParseException e){
            return null;
        }
    }
}
