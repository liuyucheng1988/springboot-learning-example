package com.huawei.service;

import com.huawei.domain.vo.CallResultReq;
import com.huawei.domain.vo.CallResultRsp;

import java.util.List;

public interface CallResultService {
    List<CallResultRsp> findByCondition(CallResultReq req);
}
