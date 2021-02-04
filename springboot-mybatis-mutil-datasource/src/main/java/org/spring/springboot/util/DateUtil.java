package org.spring.springboot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String DAY = "yyyy-MM-dd";
    public static String YEAR_MONTH = "yyyy-MM";
    public static String DAY_TIME = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DAY);
            return sdf.parse(dateStr);
        }catch (ParseException e){
            return null;
        }
    }
    public static Date addMonth(int month){
        Calendar calendar = Calendar.getInstance();//获取对日期操作的类对象
        calendar.add(Calendar.MONTH, month);
        Date time = calendar.getTime();
        return time;
    }
    public static Date addDay(int addDay){
        Calendar calendar = Calendar.getInstance();//获取对日期操作的类对象
        //两种写法都可以获取到前三天的日期
        // calendar1.set(Calendar.DAY_OF_YEAR,calendar1.get(Calendar.DAY_OF_YEAR) -3);
        //在当前时间的基础上获取前三天的日期
        calendar.add(Calendar.DATE, addDay);
        //add方法 参数也可传入 月份，获取的是前几月或后几月的日期//calendar1.add(Calendar.MONTH, -3);
        Date time = calendar.getTime();
        return time;
    }
    public static Date addDayAndFormatMorning(int addDay) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//格式化一下
        Calendar calendar = Calendar.getInstance();//获取对日期操作的类对象
        calendar.add(Calendar.DATE, addDay);
        //add方法 参数也可传入 月份，获取的是前几月或后几月的日期//calendar1.add(Calendar.MONTH, -3);
        Date time = calendar.getTime();
        String timeStr = sdf.format(time);
        return sdf.parse(timeStr);
    }

}
