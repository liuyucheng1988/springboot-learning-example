package com.huawei.service.impl;

import com.huawei.dao.CallResultDao;
import com.huawei.domain.vo.CallResultReq;
import com.huawei.domain.vo.CallResultRsp;
import com.huawei.service.CallResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CallResultServiceImpl implements CallResultService {
    @Autowired
    private CallResultDao callResultDao;


    @Override
    public List<CallResultRsp> findByCondition(CallResultReq req) {
        return callResultDao.findByCondition(req);
    }
}
