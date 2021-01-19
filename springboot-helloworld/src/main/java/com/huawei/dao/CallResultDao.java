package com.huawei.dao;

import com.huawei.domain.vo.CallResultReq;
import com.huawei.domain.vo.CallResultRsp;
import com.huawei.entity.CallResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CallResultDao {
    List<CallResultRsp> findByCondition(@Param("vo") CallResultReq callResultReq);
    void insert(@Param("vo") CallResultReq callResultReq);
}
