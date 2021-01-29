package org.spring.springboot.service;

import org.spring.springboot.domain.Api;
import org.spring.springboot.domain.CallResult;
import org.spring.springboot.domain.Route;
import org.spring.springboot.domain.TypeEnum;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.vo.*;

import java.util.List;
import java.util.Map;

public interface CallResultService {
    PageVO<CallResultRsp> findByCondition(CallResultReq req);
    PageVO<CallResultRsp> finCallResultItemByCondition(CallResultReq req);
    PageVO<RouteRsp> findRouteByCondition(Route req);
    void insertRoute(Route req) throws BusinessException;
    PageVO<Api> findApiByCondition(Api req);
    void insertCallResult(CallResult req);
    void insertServer(Api req) throws BusinessException;
    TypeEnum findTypeById(Integer id);
    Route findRouteById(Integer id);
    Api findApiById(Integer id);
    PageVO<TypeEnum> findTypeEnumByCondition(TypeEnum req);

    void insertEnum(TypeEnum req) throws BusinessException;
    Map<String, String> getTypeEnumByType(String type);


    List<KeyValueVO> findTypeMap();


    void updateTypeEnum(TypeEnum req) throws BusinessException;

    void deleteTypeEnum(Integer id) throws BusinessException;

    void logicDeleteTypeEnum(Integer id) throws BusinessException;

    void updateRoute(Route req) throws BusinessException;

    void updateApi(Api req) throws BusinessException;

    void logicDeleteRoute(Integer id) throws BusinessException;

    void logicDeleteApi(Integer id) throws BusinessException;

    PageVO<CallResultRsp> finCallResultPatchByCondition(CallResultPatchReq req);
}
