package org.spring.springboot.util;

import org.apache.commons.lang.StringUtils;
import org.spring.springboot.vo.PaperBase;

public class PageUtils {
    public static String getOrderBy(PaperBase req){
        String orderBy = null;
        if(StringUtils.isNotBlank(req.getField())){
            orderBy = req.getField();
            if(req.getDesc() != null && 1 == req.getDesc()){
                orderBy = orderBy  + " desc";
            }
        }
        return orderBy;
    }
}
