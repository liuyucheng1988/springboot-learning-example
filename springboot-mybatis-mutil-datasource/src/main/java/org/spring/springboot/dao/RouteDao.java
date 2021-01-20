package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.CallResult;
import org.spring.springboot.domain.Route;
import org.spring.springboot.domain.TypeEnum;
import org.spring.springboot.vo.CallResultReq;
import org.spring.springboot.vo.CallResultRsp;
import org.spring.springboot.vo.RouteRsp;

import java.util.List;

@Mapper
public interface RouteDao {
    List<RouteRsp> findRouteByCondition(@Param("vo") Route req);
    List<RouteRsp> findRouteApiInfoByCondition(@Param("vo") Route req);
    void insert(@Param("vo") Route entity);
    void updateRoute(@Param("vo") Route req);
    void logicDeleteRoute(@Param("id") Integer id);
//    Integer countByCondition(@Param("vo") CallResultReq req);
}
