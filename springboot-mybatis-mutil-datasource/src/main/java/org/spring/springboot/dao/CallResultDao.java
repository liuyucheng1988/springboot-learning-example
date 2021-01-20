package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.CallResult;
import org.spring.springboot.vo.CallResultPatchReq;
import org.spring.springboot.vo.CallResultReq;
import org.spring.springboot.vo.CallResultRsp;

import java.util.List;

@Mapper
public interface CallResultDao {
    List<CallResultRsp> findByCondition(@Param("vo") CallResultReq callResultReq);
    void insert(@Param("vo") CallResult entity);
    List<CallResultRsp> finCallResultItemByCondition(@Param("vo") CallResultReq callResultReq);
    List<CallResultRsp> finCallResultPatchByCondition(@Param("vo") CallResultPatchReq callResultPatchReq);

//    Integer countByCondition(@Param("vo") CallResultReq req);
}
