package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.CallResult;
import org.spring.springboot.domain.RoleMenu;
import org.spring.springboot.domain.StsMonth;
import org.spring.springboot.vo.CallResultPatchReq;
import org.spring.springboot.vo.CallResultReq;
import org.spring.springboot.vo.CallResultRsp;
import org.spring.springboot.vo.StsMonthPatchReq;

import java.util.List;

@Mapper
public interface StsMonthDao {
    List<StsMonth> findStsMonthByCondition(@Param("vo") StsMonthPatchReq req);
//    void insertStsMonth(@Param("vo") StsMonth entity);
    void insertStsMonth( @Param("stsMonthList") List<StsMonth> stsMonthList);

    List<StsMonth> groupProvinceStsMonth(@Param("vo") StsMonthPatchReq req);

}
