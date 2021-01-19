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
    PageVO<CallResultRsp> finCallResultdItemByCondition(CallResultReq req);
    Integer countByCondition(CallResultReq req);
    PageVO<RouteRsp> findRouteByCondition(Route req);
    void insertRoute(Route req) throws BusinessException;
    PageVO<Api> findApiByCondition(Api req);
    Integer countServerByCondition(Api req);
    void insertCallResult(CallResult req);
    void insertServer(Api req) throws BusinessException;
    TypeEnum findTypeById(Integer id);
    Route findRouteById(Integer id);
    Api findApiById(Integer id);
    PageVO<TypeEnum> findTypeEnumByCondition(TypeEnum req);
    Integer countTypeEnumByCondition(TypeEnum req);

    void insertEnum(TypeEnum req) throws BusinessException;
    Map<String, String> getTypeEnumByType(String type);


    List<KeyValueVO> findTypeMap();

    void updateTypeEnum(TypeEnum req) throws BusinessException;

    void deleteTypeEnum(Integer id);

    void logicDeleteTypeEnum(Integer id);

    void updateRoute(Route req) throws BusinessException;

    void updateApi(Api req) throws BusinessException;

    void logicDeleteRoute(Integer id);

    void logicDeleteApi(Integer id);
}
