package org.spring.springboot.util;

public class Constant {
    public static String KEY_MonthCallSizeJob = "MonthCallSizeJob";
    public static String Month_Morning =  "-01 00:00:00";//2021-03 月初末尾内容
    public static String MinDayTime = "2020-01-01 00:00:00";
    public static String LEFT_SQUAREBRACKETS = "[";
    public static String RIGHT_SQUAREBRACKETS = "]";
    public static String NULL = "null";
    public static String COMMA = ",";
    public static String UNDERSCORE = "_";
//    public static String FIRST_LEVLE = "一级";
//    public static String SECOND_LEVLE = "二级";
//    public static String THIRD_LEVLE = "三级";
    // 配置类型
    public static class Type{
        public static String Province = "province";
        public static String Insurance_category = "insurance_category";//险种
        public static String Bills_type = "bills_type";//票据类型
    }
    public static class HashKey{
        public static String SERVER = "server";//服务
        public static String TYPEENUM = "enum";//参数枚举
        public static String ROUTE = "route";//路由
    }
    public static class Field{
        public static String API = "api";//api

    }
}
