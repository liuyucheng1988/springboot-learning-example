package org.spring.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.spring.springboot.dao.*;
import org.spring.springboot.domain.*;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.CallResultService;
import org.spring.springboot.util.Constant;
import org.spring.springboot.util.DateUtil;
import org.spring.springboot.util.PageUtils;
import org.spring.springboot.util.RedisUtil;
import org.spring.springboot.vo.*;
import org.spring.springboot.vo.echarts.ProvinceDayData;
import org.spring.springboot.vo.echarts.ProvinceDayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CallResultServiceImpl implements CallResultService {
    @Autowired
    private CallResultDao callResultDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private TypeEnumDao typeEnumDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StsMonthDao stsMonthDao;
    public CallResultServiceImpl() {
    }

    @Override
    public PageVO<CallResultRsp> findByCondition(CallResultReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<CallResultRsp> callResultRsps = callResultDao.findByCondition(req);

        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> categoryMap = getTypeEnumByType(Constant.Type.Insurance_category);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        List<Api> servers = getServer();
        Map<String, Api> serverMap =
                servers.stream().collect(Collectors.toMap(Api::getCodesn, a -> a,(k1, k2)->k1));
        StringBuffer buffer = new StringBuffer();
        callResultRsps.stream().map(rsp ->{
            if(serverMap.get(rsp.getApiCodesn()) != null){
                rsp.setApiName(serverMap.get(rsp.getApiCodesn()).getName());
                rsp.setApiUrl(serverMap.get(rsp.getApiCodesn()).getUrl());
            }
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            rsp.setBillsTypeName(billsTypeMap.get(rsp.getBillsType()));
            rsp.setCategoryName(categoryMap.get(rsp.getCategory()));
            buffer.setLength(0);
            buffer.append(Constant.LEFT_SQUAREBRACKETS).append(rsp.getDurationMin() == null ? "null" : rsp.getDurationMin() ).append(Constant.COMMA)
                    .append(rsp.getDurationMax() == null ? "null" : rsp.getDurationMax()).append(Constant.RIGHT_SQUAREBRACKETS);
            rsp.setDurationRange(buffer.toString());
            return rsp;
        }).collect(Collectors.toList());


        PageInfo<CallResultRsp> info = new PageInfo<>(callResultRsps);
        return new PageVO<CallResultRsp>(info.getTotal(), callResultRsps);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void groupProvinceStsMonth(){
        Date now = new Date();
        Date lastMonth = DateUtil.addMonth(-1);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YEAR_MONTH);
        SimpleDateFormat sdfDateTime = new SimpleDateFormat(DateUtil.DAY_TIME);
        String nowMonth = sdf.format(now);
        try {
            Date nowFormat = sdfDateTime.parse(sdf.format(now) + Constant.Month_Morning);
            Date lastMonthFormat = sdfDateTime.parse(sdf.format(lastMonth) + Constant.Month_Morning);
            log.info("start to groupProvinceStsMonth by cronjob MonthCallSizeJob");
            boolean getLock = redisUtil.getLock(nowMonth);
            if(getLock){
                log.info("获取锁成功，key="+nowMonth);
                String value = (String) redisUtil.hget(Constant.KEY_MonthCallSizeJob, nowMonth);
                if(value != null && value.equals("1")){
                    log.info("已经汇总过上个月的地区调用次数，key="+nowMonth);
                    return;
                }
                redisUtil.hset(Constant.KEY_MonthCallSizeJob, nowMonth, "1");
                StsMonthPatchReq req = new StsMonthPatchReq();
                req.setCreateTimeFrom(lastMonthFormat);
                req.setCreateTimeTo(nowFormat);
                List<StsMonth>  stsMonths = stsMonthDao.groupProvinceStsMonth(req);
                log.info("groupProvinceStsMonth result size= "+stsMonths.size());
                if(!CollectionUtils.isEmpty(stsMonths)){
                    stsMonthDao.insertStsMonth(stsMonths);
                }
            }else{
                log.info("获取锁失败，key="+nowMonth);
            }
        } catch (Exception e) {
            log.error("MonthCallSizeJob 异常", e);
            redisUtil.hdel(Constant.KEY_MonthCallSizeJob, nowMonth);
        } finally {
            redisUtil.del(nowMonth);
        }
    }
    //type=0 1 2 3 4对应:当月\上月\最近3月\全部\搜索按钮
    @Override
    public List<NameValueVO> pieDisplayData(Integer type, CallResultPatchReq resultPatchReq) throws BusinessException {
        CallResultPatchReq req = new CallResultPatchReq();
        SimpleDateFormat sdfDaytime = new SimpleDateFormat(DateUtil.DAY_TIME);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YEAR_MONTH);
        Date now = new Date();
        if(type == 0){
            //当月
            try {
                Date dateFrom = sdfDaytime.parse(sdf.format(now) + Constant.Month_Morning);
                req.setCreateTimeFrom(dateFrom);
                req.setCreateTimeTo(now);
            } catch (ParseException e) {
                throw new BusinessException(500, "获取开始时间异常。",e.getMessage());
            }
        }else if(type == 1){
            //上个月
            try {
                Date nowMonthMorning = sdfDaytime.parse(sdf.format(now) + Constant.Month_Morning);
                Date lastMonth = DateUtil.addMonth(-1);
                Date lastMonthFormat = sdfDaytime.parse(sdf.format(lastMonth) + Constant.Month_Morning);
                req.setCreateTimeFrom(lastMonthFormat);
                req.setCreateTimeTo(nowMonthMorning);
            } catch (ParseException e) {
                throw new BusinessException(500, "获取开始时间异常。",e.getMessage());
            }
        }else if(type == 2){
            //最近3个月
            try {
                Date nowMonthMorning = sdfDaytime.parse(sdf.format(now) + Constant.Month_Morning);
                Date lastMonth = DateUtil.addMonth(-3);
                Date lastMonthFormat = sdfDaytime.parse(sdf.format(lastMonth) + Constant.Month_Morning);
                req.setCreateTimeFrom(lastMonthFormat);
                req.setCreateTimeTo(nowMonthMorning);
            } catch (ParseException e) {
                throw new BusinessException(500, "获取开始时间异常。",e.getMessage());
            }
        }else if(type == 3){
            //全部
            try {
                Date nowMonthMorning = sdfDaytime.parse(sdf.format(now) + Constant.Month_Morning);
                Date startDayTime = sdfDaytime.parse(Constant.MinDayTime);
                req.setCreateTimeFrom(startDayTime);
                req.setCreateTimeTo(now);
            } catch (ParseException e) {
                throw new BusinessException(500, "获取开始时间异常。",e.getMessage());
            }
        }else if(type == 4){
            //页面传入搜索条件
            req.setCreateTimeFrom(resultPatchReq.getCreateTimeFrom());
            req.setCreateTimeTo(resultPatchReq.getCreateTimeTo());
            req.setProvinceList(resultPatchReq.getProvinceList());
        }
        List<CallResultRsp> callResultRsps = callResultDao.groupByProvince(req);
        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        StringBuffer buffer = new StringBuffer();
        callResultRsps.stream().map(rsp ->{
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            rsp.setBillsTypeName(billsTypeMap.get(rsp.getBillsType()));
            return rsp;
        }).collect(Collectors.toList());

        Map<String, List<CallResultRsp>> groupByProvince = callResultRsps.stream().collect(Collectors.groupingBy(CallResultRsp::getProvinceName));
        List<NameValueVO> nameValues = new ArrayList<>();
        for(String name : groupByProvince.keySet()){
            List<CallResultRsp> resultRsps = groupByProvince.get(name);
            List<NameValueVO> billTypeNameList= resultRsps.stream().map(rsp -> new NameValueVO(rsp.getBillsTypeName(),rsp.getSize())).collect(Collectors.toList());
            NameValueVO vo = new NameValueVO(name, resultRsps.stream().mapToInt(CallResultRsp::getSize).sum(), billTypeNameList);
            nameValues.add(vo);
        }
        return nameValues;
    }

    @Override
    public ProvinceDayVo groupByProvinceAndMonth() throws BusinessException {
        int maxMonthLast = 12;//最大12个月
        //查询当月数据
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YEAR_MONTH);
        SimpleDateFormat sdfDateTime = new SimpleDateFormat(DateUtil.DAY_TIME);
        try {
            Date monthStartFormat = sdfDateTime.parse(sdf.format(now) + Constant.Month_Morning);
            StsMonthPatchReq req = new StsMonthPatchReq();
            req.setCreateTimeFrom(monthStartFormat);
            req.setCreateTimeTo(now);
            List<StsMonth> monthNowSts = stsMonthDao.groupProvinceStsMonth(req);
            //查询上月及以上数据
            List<StsMonth> stsMonths = stsMonthDao.findStsMonthByCondition(new StsMonthPatchReq());
            List<StsMonth> stsMonthList = new ArrayList<>(stsMonths);
            stsMonthList.addAll(monthNowSts);

            Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
            stsMonthList.stream().map(rsp ->{
                rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
                return rsp;
            }).collect(Collectors.toList());
            //总共有多少月
            ProvinceDayVo vo = new ProvinceDayVo();
            vo.setTitle("地区每月趋势");
            List<String> allDistinctMonths = stsMonthList.stream().map(StsMonth::getMonth).distinct().sorted().collect(Collectors.toList());
            int distinctMonthSize = allDistinctMonths.size();
            List<String> distinctMonths = allDistinctMonths;
            if(distinctMonthSize > maxMonthLast){
                int startIndex = distinctMonthSize - maxMonthLast;
                distinctMonths = new ArrayList<>();
                for(int i = startIndex; i < distinctMonthSize; i++){
                    distinctMonths.add(allDistinctMonths.get(i));
                }
                distinctMonthSize = maxMonthLast;
            }
            vo.setDays(distinctMonths);
            List<ProvinceDayData> datas = new ArrayList<>();
            Map<String, List<StsMonth>> groupByProvince = stsMonthList.stream().collect(Collectors.groupingBy(StsMonth::getProvinceName));
            List<String> provinces = new ArrayList<>(groupByProvince.keySet());
            vo.setProvinces(provinces);
            //
            for(String province : provinces){
                List<StsMonth> daySize = groupByProvince.get(province);
                Map<String, Integer> day_size = new HashMap<>();
                for(StsMonth tmp : daySize){
                    day_size.put(tmp.getMonth(), tmp.getSize());
                }
                ProvinceDayData data = new ProvinceDayData();
                data.setName(province);
                List<Integer> sizes = new ArrayList<>();
                for(int i=0;i<distinctMonthSize;i++){
                    if(day_size.containsKey(distinctMonths.get(i))){
                        sizes.add(day_size.get(distinctMonths.get(i)));
                    }else{
                        sizes.add(0);
                    }
                }
                data.setSizes(sizes);
                datas.add(data);
            }
            vo.setData(datas);
            return vo;
        } catch (ParseException e) {
            throw new BusinessException(500, "系统异常，请联系管理员", e.getMessage());
        }
    }
    @Override
    public ProvinceDayVo groupByProvinceAndDay() throws BusinessException {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DAY);
        Integer daysToDisplay = 13;// 最近2周调用次数统计
        Date dateFrom = null;
        try {
            dateFrom = DateUtil.addDayAndFormatMorning(-1 * daysToDisplay);
        } catch (ParseException e) {
            throw new BusinessException(500, "获取开始时间异常。",e.getMessage());
        }
        CallResultReq req = new CallResultReq();
        req.setCreateTimeFrom(dateFrom);
        Date now = new Date();
        req.setCreateTimeTo(now);
        List<String> dayStrList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();//获取对日期操作的类对象
        dayStrList.add(sdf.format(now));
        for(int i=0;i<daysToDisplay;i++){
            calendar.add(Calendar.DATE, -1);
            dayStrList.add(sdf.format(calendar.getTime()));
        }
        Collections.reverse(dayStrList);

        List<CallResultRsp> callResultRsps = callResultDao.groupByProvinceAndDay(req);
        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        callResultRsps.stream().map(rsp ->{
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            return rsp;
        }).collect(Collectors.toList());

        ProvinceDayVo vo = new ProvinceDayVo();
        vo.setTitle("地区近2周趋势");
        vo.setDays(dayStrList);
        List<ProvinceDayData> datas = new ArrayList<>();
        Map<String, List<CallResultRsp>> groupByProvince = callResultRsps.stream().collect(Collectors.groupingBy(CallResultRsp::getProvinceName));
        List<String> provinces = new ArrayList<>(groupByProvince.keySet());
        vo.setProvinces(provinces);
        for(String province : provinces){
            List<CallResultRsp> daySize = groupByProvince.get(province);
            Map<String, Integer> day_size = new HashMap<>();
            for(CallResultRsp tmp : daySize){
                day_size.put(sdf.format(tmp.getCreateday()), tmp.getSize());
            }
            ProvinceDayData data = new ProvinceDayData();
            data.setName(province);
            List<Integer> sizes = new ArrayList<>();
            for(int i=0;i< daysToDisplay+1;i++){
                if(day_size.containsKey(dayStrList.get(i))){
                    sizes.add(day_size.get(dayStrList.get(i)));
                }else{
                    sizes.add(0);
                }
            }
            data.setSizes(sizes);
            datas.add(data);
        }
        vo.setData(datas);
        return vo;
    }

    @Override
    public PageVO<CallResultRsp> finCallResultPatchByCondition(CallResultPatchReq req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<CallResultRsp> callResultRsps = callResultDao.finCallResultPatchByCondition(req);

        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> categoryMap = getTypeEnumByType(Constant.Type.Insurance_category);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        List<Api> servers = getServer();
        Map<String, Api> serverMap =
                servers.stream().collect(Collectors.toMap(Api::getCodesn, a -> a,(k1, k2)->k1));
        callResultRsps.stream().map(rsp ->{
            if(serverMap.get(rsp.getApiCodesn()) != null){
                rsp.setApiName(serverMap.get(rsp.getApiCodesn()).getName());
                rsp.setApiUrl(serverMap.get(rsp.getApiCodesn()).getUrl());
            }
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            rsp.setBillsTypeName(billsTypeMap.get(rsp.getBillsType()));
            rsp.setCategoryName(categoryMap.get(rsp.getCategory()));
            return rsp;
        }).collect(Collectors.toList());
        PageInfo<CallResultRsp> info = new PageInfo<>(callResultRsps);
        return new PageVO<CallResultRsp>(info.getTotal(), callResultRsps);
    }



    public PageVO<CallResultRsp> finCallResultItemByCondition(CallResultReq req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getDetailPageNum(), req.getDetailPageSize(), orderBy);
        List<CallResultRsp> callResultRsps = callResultDao.finCallResultItemByCondition(req);

        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> categoryMap = getTypeEnumByType(Constant.Type.Insurance_category);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        List<Api> servers = getServer();
        Map<String, Api> serverMap =
                servers.stream().collect(Collectors.toMap(Api::getCodesn, a -> a,(k1, k2)->k1));
        StringBuffer buffer = new StringBuffer();
        callResultRsps.stream().map(rsp ->{
            if(serverMap.get(rsp.getApiCodesn()) != null){
                rsp.setApiName(serverMap.get(rsp.getApiCodesn()).getName());
                rsp.setApiUrl(serverMap.get(rsp.getApiCodesn()).getUrl());
            }
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            rsp.setBillsTypeName(billsTypeMap.get(rsp.getBillsType()));
            rsp.setCategoryName(categoryMap.get(rsp.getCategory()));
            return rsp;
        }).collect(Collectors.toList());


        PageInfo<CallResultRsp> info = new PageInfo<>(callResultRsps);
        return new PageVO<CallResultRsp>(info.getTotal(), callResultRsps);
    }
    @Override
    public PageVO<RouteRsp> findRouteByCondition(Route req) {
        String orderBy  = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<RouteRsp> rsps = routeDao.findRouteByCondition(req);
        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> categoryMap = getTypeEnumByType(Constant.Type.Insurance_category);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        List<Api> servers = getServer();
        Map<String, Api> serverMap =
                servers.stream().collect(Collectors.toMap(Api::getCodesn, a -> a,(k1, k2)->k1));
        rsps.stream().map(rsp ->{
            if(serverMap.get(rsp.getApiCodesn()) != null){
                rsp.setApiName(serverMap.get(rsp.getApiCodesn()).getName());
                rsp.setApiUrl(serverMap.get(rsp.getApiCodesn()).getUrl());
            }
            rsp.setProvinceName(provinceMap.get(rsp.getProvince()));
            rsp.setBillsTypeName(billsTypeMap.get(rsp.getBillsType()));
            rsp.setCategoryName(categoryMap.get(rsp.getCategory()));
            return rsp;
        }).collect(Collectors.toList());
        PageInfo<RouteRsp> info = new PageInfo<>(rsps);
        return new PageVO<RouteRsp>(info.getTotal(), rsps);
    }

    public Map<String, String> getTypeEnumByType(String type){
        List<TypeEnum> list = (List<TypeEnum>)redisUtil.hget(Constant.HashKey.TYPEENUM, type);
        if (list == null){
            System.out.println("redis缓存没有，从数据库取出并存入redis");
            TypeEnum req = new TypeEnum();
            req.setType(type);
            list = typeEnumDao.findTypeEnumByCondition(req);
            redisUtil.hset(Constant.HashKey.TYPEENUM,type,list);
        }
        Map<String, String> codesn_name =
                list.stream().collect(Collectors.toMap(TypeEnum::getCodesn, TypeEnum::getName,(k1, k2)->k1));
        return codesn_name;
    }

    @Override
    public List<KeyValueVO> findTypeMap() {
        return typeEnumDao.findTypeMap();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTypeEnum(TypeEnum req) throws BusinessException {
        TypeEnum reqParam = new TypeEnum();
        reqParam.setType(req.getType());
        reqParam.setCodesn(req.getCodesn());
        List<TypeEnum> types = typeEnumDao.findPreciseTypeEnumByCondition(reqParam);
        List<KeyValueVO> typeCodeNames = findTypeMap();
        Map<String, String> typeCode_Name = new HashMap<>();
        if(!CollectionUtils.isEmpty(typeCodeNames)){
            typeCode_Name = typeCodeNames.stream().collect(Collectors.toMap(KeyValueVO::getKey, KeyValueVO::getValue));
        }
        req.setTypeName(typeCode_Name.get(req.getType()));
        if(CollectionUtils.isEmpty(types) || (types.size() == 1 && types.get(0).getId().equals(req.getId()))){
        }else{
            throw new BusinessException(500, "已存在类型为"+(req.getTypeName() == null ? "null" : req.getTypeName())+"，编码为"+req.getCodesn()+"的记录，请重新选择类型或者填写编码");
        }
        //名称不能重复
        TypeEnum reqParamName = new TypeEnum();
        reqParamName.setType(req.getType());
        reqParamName.setName(req.getName());
        types = typeEnumDao.findPreciseTypeEnumByCondition(reqParamName);
        req.setTypeName(typeCode_Name.get(req.getType()));
        if(CollectionUtils.isEmpty(types) || (types.size() == 1 && types.get(0).getId().equals(req.getId()))){
            //更新
            typeEnumDao.updateTypeEnum(req);
            redisUtil.hdel(Constant.HashKey.TYPEENUM, req.getType());
        }else{
            throw new BusinessException(500, "已存在类型为"+(req.getTypeName() == null ? "null" : req.getTypeName())+"，名称为"+req.getName()+"的记录，请重新选择类型或者填写名称");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTypeEnum(Integer id) throws BusinessException {
        TypeEnum typeEnum = findTypeById(id);
        if(typeEnum == null){
            throw new BusinessException(500, "不存在id="+id+"的参数");
        }
        typeEnumDao.deleteTypeEnum(id);
        redisUtil.hdel(Constant.HashKey.TYPEENUM, typeEnum.getType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDeleteTypeEnum(Integer id) throws BusinessException {
        TypeEnum typeEnum = findTypeById(id);
        if(typeEnum == null){
            throw new BusinessException(500, "不存在id="+id+"的参数");
        }
        typeEnumDao.logicDeleteTypeEnum(id);
        redisUtil.hdel(Constant.HashKey.TYPEENUM, typeEnum.getType());
    }

    /**
     * 更新路由，不能造成重复路由。
     * @param req
     * @throws BusinessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoute(Route req) throws BusinessException {
        Route reqParam = new Route();
        reqParam.setProvince(req.getProvince());
        reqParam.setCategory(req.getCategory());
        reqParam.setBillsType(req.getBillsType());
        String redisField = makeRedisFieldFromRoute(req);
        //（不含时间条件）查询路由
        List<RouteRsp> routes = routeDao.findRouteByCondition(reqParam);
        //没有路由
        if(CollectionUtils.isEmpty(routes)){
            routeDao.updateRoute(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //有一条自身的路由
        if(routes.size() == 1 && routes.get(0).getId().equals(req.getId())){
            routeDao.updateRoute(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //有一条其他的路由
        if(routes.size() == 1 && !routes.get(0).getId().equals(req.getId())){
            RouteRsp routeRsp = routes.get(0);
            //判断时间区间是否重叠
            if(!dateTimeRangeOverlap(req, routeRsp.getEffectivetimeFrom(), routeRsp.getEffectivetimeTo())){
                routeDao.updateRoute(req);
                saveRedisRoute(req, redisField);
                return;
            }else{
                throw new BusinessException(500, "已存在相同条件的路由，id="+routeRsp.getId()+"，请重新配置");
            }
        }
        // 除去自身的那条路由集合
        List<RouteRsp> routesRemoveSelf = new ArrayList<>();
        for(RouteRsp routeRsp : routes){
            if(!routeRsp.getId().equals(req.getId())){
                routesRemoveSelf.add(routeRsp);
            }
        }
        int size = routesRemoveSelf.size();
        //只有一个其他路由
        if(size == 1){
            if(!dateTimeRangeOverlap(req, routesRemoveSelf.get(0).getEffectivetimeFrom(), routesRemoveSelf.get(0).getEffectivetimeTo())){
                routeDao.updateRoute(req);
                saveRedisRoute(req, redisField);
                return;
            }
            throw new BusinessException(500, "已存在相同条件的路由，id="+routesRemoveSelf.get(0).getId()+"，请重新配置");
        }
        //2个以上（含）路由
        //在最小区间的左侧
        if(req.getEffectivetimeTo().before(routesRemoveSelf.get(0).getEffectivetimeFrom())){
            routeDao.updateRoute(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //在最大区间的右侧
        if(req.getEffectivetimeFrom().after(routesRemoveSelf.get(size - 1).getEffectivetimeTo())){
            routeDao.updateRoute(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //只要完全在相邻对象的空闲区间里面，就表明是一条不重复的路由。有n-1个空闲区间
        boolean canUpdate = false;
        for(int i=0; i<size-1; i++){
            RouteRsp routeRsp = routesRemoveSelf.get(i);
            RouteRsp routeRspNext = routesRemoveSelf.get(i+1);
            if(dateTimeRangeAllContais(req, routeRsp.getEffectivetimeTo(), routeRspNext.getEffectivetimeFrom())){
                canUpdate = true;
            }
        }
        if(canUpdate){
            routeDao.updateRoute(req);
            saveRedisRoute(req, redisField);
            return;
        }
        throw new BusinessException(500, "已存在相同条件的路由，id="+routesRemoveSelf.get(0).getId()+"，请重新配置");
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRoute(Route req) throws BusinessException {
        Route reqParam = new Route();
        reqParam.setProvince(req.getProvince());
        reqParam.setCategory(req.getCategory());
        reqParam.setBillsType(req.getBillsType());
        String redisField = makeRedisFieldFromRoute(req);
        //（不含时间条件）查询路由
        List<RouteRsp> routes = routeDao.findRouteByCondition(reqParam);
        //没有路由
        if(CollectionUtils.isEmpty(routes)){
            routeDao.insert(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //有一条其他的路由
        int size = routes.size();
        //只有一个其他路由
        if(size == 1){
            if(!dateTimeRangeOverlap(req, routes.get(0).getEffectivetimeFrom(), routes.get(0).getEffectivetimeTo())){
                routeDao.insert(req);
                saveRedisRoute(req, redisField);
                return;
            }
            throw new BusinessException(500, "已存在相同条件的路由，id="+routes.get(0).getId()+"，请重新配置");
        }
        //2个以上（含）路由
        //在最小区间的左侧
        if(req.getEffectivetimeTo().before(routes.get(0).getEffectivetimeFrom())){
            routeDao.insert(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //在最大区间的右侧
        if(req.getEffectivetimeFrom().after(routes.get(size - 1).getEffectivetimeTo())){
            routeDao.insert(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //只要完全在相邻对象的空闲区间里面，就表明是一条不重复的路由。有n-1个空闲区间
        boolean canSave = false;
        for(int i=0; i<size-1; i++){
            RouteRsp routeRsp = routes.get(i);
            RouteRsp routeRspNext = routes.get(i+1);
            if(dateTimeRangeAllContais(req, routeRsp.getEffectivetimeTo(), routeRspNext.getEffectivetimeFrom())){
                canSave = true;
            }
        }
        if(canSave){
            routeDao.insert(req);
            saveRedisRoute(req, redisField);
            return;
        }
        //日志
        Map<String, String> provinceMap = getTypeEnumByType(Constant.Type.Province);
        Map<String, String> categoryMap = getTypeEnumByType(Constant.Type.Insurance_category);
        Map<String, String> billsTypeMap = getTypeEnumByType(Constant.Type.Bills_type);
        StringBuffer buffer = new StringBuffer();
        buffer.append("省份=").append(provinceMap.get(req.getProvince())).append("，险种=")
                .append(categoryMap.get(req.getCategory())).append("，票据类型=").append(billsTypeMap.get(req.getBillsType()));
        throw new BusinessException(500, "已存在相同条件的路由，"+buffer.toString()+"，请重新配置");
    }

    /**
     *
     * @param req
     * @param redisField 身份_险种_票据类型
     */
    private void saveRedisRoute(Route req, String redisField){
        redisUtil.hdel(Constant.HashKey.ROUTE, redisField);
        //查询该key的数据，缓存
        Route reqParam = new Route();
        reqParam.setProvince(req.getProvince());
        reqParam.setCategory(req.getCategory());
        reqParam.setBillsType(req.getBillsType());
        List<RouteRsp> routes = routeDao.findRouteApiInfoByCondition(reqParam);
        List<Api> servers = getServer();
        Map<String, Api> serverMap =
                servers.stream().collect(Collectors.toMap(Api::getCodesn, a -> a,(k1, k2)->k1));
        routes.stream().map(rsp ->{
            if(serverMap.get(rsp.getApiCodesn()) != null){
                rsp.setApiName(serverMap.get(rsp.getApiCodesn()).getName());
                rsp.setApiUrl(serverMap.get(rsp.getApiCodesn()).getUrl());
            }
            return rsp;
        }).collect(Collectors.toList());
        redisUtil.hset(Constant.HashKey.ROUTE, redisField, routes);
    }
    //req的时间区间和from-to 有交叠
    private boolean dateTimeRangeOverlap(Route req, Date from, Date to){
        if(req.getEffectivetimeTo().before(from) ||
                req.getEffectivetimeFrom().after(to)){
            return false;
        }
        return true;
    }
    //req的时间区间完全在from-to 之间，即是他的子集
    private boolean dateTimeRangeAllContais(Route req, Date from, Date to){
        if(req.getEffectivetimeTo().before(to) && req.getEffectivetimeFrom().after(from)){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApi(Api req) throws BusinessException {
        List<Api> api = apiDao.getApiByCodeOrNameOrUrl(req);
        if(CollectionUtils.isEmpty(api)  || (api.size() == 1 && api.get(0).getId().equals(req.getId()))){
            apiDao.updateApi(req);
            redisUtil.hdel(Constant.HashKey.SERVER, Constant.Field.API);
            return;
        }
        throw new BusinessException(500, "已存在相同的编码、名称或url的记录，请重新填写");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDeleteRoute(Integer id) throws BusinessException {
        Route route = findRouteById(id);
        if(route == null){
            throw new BusinessException(500, "不存在id="+id+"的路由");
        }
        routeDao.logicDeleteRoute(id);
        String redisField = makeRedisFieldFromRoute(route);
        saveRedisRoute(route, redisField);
    }

    private String makeRedisFieldFromRoute(Route req){
        StringBuffer field = new StringBuffer();
        field.append(req.getProvince()).append(Constant.UNDERSCORE).append(req.getCategory()).append(Constant.UNDERSCORE)
                .append(req.getBillsType());
        return field.toString();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicDeleteApi(Integer id) throws BusinessException {
        Api api = findApiById(id);
        if(api == null){
            throw new BusinessException(500, "不存在id="+id+"的Api");
        }
        Route req = new Route();
        req.setApiCodesn(api.getCodesn());
        List<RouteRsp> routeRsps = routeDao.findRouteApiInfoByCondition(req);
        if(CollectionUtils.isEmpty(routeRsps)){
            apiDao.logicDeleteApi(id);
            redisUtil.hdel(Constant.HashKey.SERVER, Constant.Field.API);
        }else{
            throw new BusinessException(500, "已被路由引用，请先删除路由再来删除"+api.getName());
        }

    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertServer(Api req) throws BusinessException {
        Api reqParam = new Api();
        reqParam.setCodesn(req.getCodesn());
        List<Api> typesSameCodesn = apiDao.queryApiByCondition(reqParam);
        reqParam = new Api();
        reqParam.setUrl(req.getUrl());
        List<Api> typesSameUrl = apiDao.queryApiByCondition(reqParam);
        reqParam = new Api();
        reqParam.setName(req.getName());
        List<Api> typesSameName = apiDao.queryApiByCondition(reqParam);
        if(!CollectionUtils.isEmpty(typesSameCodesn)){
            throw new BusinessException(500, "已存在编码为"+req.getCodesn()+"的记录，请重新填写");
        }
        if(!CollectionUtils.isEmpty(typesSameUrl)){
            throw new BusinessException(500, "已存在url为"+req.getUrl()+"的记录，请重新填写");
        }
        if(!CollectionUtils.isEmpty(typesSameName)){
            throw new BusinessException(500, "已存在名称为"+req.getName()+"的记录，请重新填写");
        }
        apiDao.insert(req);
        redisUtil.hdel(Constant.HashKey.SERVER,Constant.Field.API);
    }
    public List<Api> getServer(){
        List<Api> list = (List<Api>)redisUtil.hget(Constant.HashKey.SERVER, Constant.Field.API);
        if (list == null){
            System.out.println("redis缓存没有，从数据库取出并存入redis");
            list = apiDao.findApiByCondition(new Api());
            redisUtil.hset(Constant.HashKey.SERVER,Constant.Field.API, list);
        }
        return list;
    }



    @Override
    public PageVO<Api> findApiByCondition(Api req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<Api> servers = apiDao.findApiByCondition(req);
        PageInfo<Api> info = new PageInfo<>(servers);
        return new PageVO<Api>(info.getTotal(), servers);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCallResult(CallResult req) {
        callResultDao.insert(req);
    }



    @Override
    public TypeEnum findTypeById(Integer id) {
        if(id == null){
            return null;
        }
        TypeEnum req = new TypeEnum();
        req.setId(id);
        List<TypeEnum> list = typeEnumDao.findTypeEnumByCondition(req);
        if(CollectionUtils.isEmpty(list)){
            log.error("id="+id+",查询枚举表为空");
            return null;
        }
        return list.get(0);
    }

    @Override
    public Route findRouteById(Integer id) {
        if(id == null){
            return null;
        }
        Route req = new Route();
        req.setId(id);
        List<RouteRsp> list = routeDao.findRouteByCondition(req);
        if(CollectionUtils.isEmpty(list)){
            log.error("id="+id+",查询路由为空");
            return null;
        }
        return list.get(0);
    }

    @Override
    public Api findApiById(Integer id) {
        if(id == null){
            return null;
        }
        Api req = new Api();
        req.setId(id);
        List<Api> list = apiDao.queryApiByCondition(req);
        if(CollectionUtils.isEmpty(list)){
            log.error("id="+id+",查询Api对象为空");
            return null;
        }
        return list.get(0);
    }

    @Override
    public PageVO<TypeEnum> findTypeEnumByCondition(TypeEnum req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<TypeEnum> typeEnums = typeEnumDao.findTypeEnumByCondition(req);
        PageInfo<TypeEnum> info = new PageInfo<>(typeEnums);
        return new PageVO<TypeEnum>(info.getTotal(), typeEnums);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertEnum(TypeEnum req) throws BusinessException {
        TypeEnum reqParam = new TypeEnum();
        reqParam.setType(req.getType());
        reqParam.setCodesn(req.getCodesn());
        List<TypeEnum> types = typeEnumDao.findPreciseTypeEnumByCondition(reqParam);
        List<KeyValueVO> typeCodeNames = findTypeMap();
        Map<String, String> typeCode_Name = new HashMap<>();
        if(!CollectionUtils.isEmpty(typeCodeNames)){
            typeCode_Name = typeCodeNames.stream().collect(Collectors.toMap(KeyValueVO::getKey, KeyValueVO::getValue));
        }
        req.setTypeName(typeCode_Name.get(req.getType()));
        if(!CollectionUtils.isEmpty(types)){
            throw new BusinessException(500, "已存在类型为"+(req.getTypeName() == null ? "null" : req.getTypeName())+"，编码为"+req.getCodesn()+"的记录，请重新选择类型或者填写编码");
        }
        //名称不能重复
        TypeEnum reqParamName = new TypeEnum();
        reqParamName.setType(req.getType());
        reqParamName.setName(req.getName());
        types = typeEnumDao.findPreciseTypeEnumByCondition(reqParamName);
        if(CollectionUtils.isEmpty(types)){
            typeEnumDao.insert(req);
            redisUtil.hdel(Constant.HashKey.TYPEENUM, req.getType());
        }else{
            throw new BusinessException(500, "已存在类型为"+(req.getTypeName() == null ? "null" : req.getTypeName())+"，名称为"+req.getName()+"的记录，请重新选择类型或者填写名称");
        }
    }
}
