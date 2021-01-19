package org.spring.springboot.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.*;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.CallResultService;
import org.spring.springboot.service.UserService;
import org.spring.springboot.util.Response;
import org.spring.springboot.vo.CallResultReq;
import org.spring.springboot.vo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户控制层
 *
 * Created by bysocket on 07/02/2017.
 */
@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserService userService;
    @Autowired
    private CallResultService callResultService;
    /**
     * 根据用户名获取用户信息，包括从库的地址信息
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> findByName(@RequestParam(value = "userName", required = true) String userName) {
        User user = new User();
        user.setUserName(userName);
        return userService.findUserByCondition(user);
    }
    @GetMapping(value = "/menus")
    public Response getMenus() {

        List<Menu> menus = new ArrayList<>();
        List<Menu> menuChildren = new ArrayList<>();
        menuChildren.add(new Menu(11, "调用统计", "monitor"));
        menus.add(new Menu(1, "监控管理", null, menuChildren));

        menuChildren = new ArrayList<>();
        menuChildren.add(new Menu(21, "路由配置", "route"));
        menuChildren.add(new Menu(22, "Api配置", "api"));
        menuChildren.add(new Menu(23, "参数配置", "type"));
        menus.add(new Menu(2, "系统管理", null, menuChildren));


        return Response.SUCCESSDATA(menus);

    }

    @PostMapping("/login")
    public Response login(@RequestBody User user) {
        System.out.println("user="+user);
        List<User> users = userService.findUserByCondition(user);
        if(CollectionUtils.isEmpty(users)){
            return Response.FAIL;
        }
        Map<String, String> token = new HashMap<>();
        token.put("token", UUID.randomUUID().toString());
        return Response.SUCCESSDATA(token);
    }
    @PostMapping("/querymonitor")
    public Response queryMonitor(@RequestBody CallResultReq req)   {
        if(req.getCreateTimeFrom() == null || req.getCreateTimeTo() == null){
//            req.setCreateTimeFrom(DateUtil.strToDate("2021-01-11"));
//            req.setCreateTimeTo(DateUtil.strToDate("2021-01-20"));
            return Response.FAIL;
        }
        return Response.SUCCESSDATA(callResultService.findByCondition(req));
    }
    @PostMapping("/querydetail")
    public Response queryDetail(@RequestBody CallResultReq req)   {
        if(req.getCreateTimeFrom() == null || req.getCreateTimeTo() == null){
            return Response.FAIL;
        }
        return Response.SUCCESSDATA(callResultService.finCallResultdItemByCondition(req));
    }
    @PostMapping("/monitor")
    public Response saveMonitor(@RequestBody CallResult req)   {
        callResultService.insertCallResult(req);
        return Response.SUCCESS;
    }
    @PostMapping("/queryroute")
    public Response queryRoute(@RequestBody Route req)   {
        return Response.SUCCESSDATA(callResultService.findRouteByCondition(req));
    }
    @GetMapping("/queryroute")
    public Response queryRouteById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findRouteById(id));
    }
    @PostMapping("/route")
    public Response saveRoute(@RequestBody Route req) throws BusinessException {
        callResultService.insertRoute(req);
        return Response.SUCCESS;
    }
    @PutMapping("/route")
    public Response updatetApi(@RequestBody Route req) throws BusinessException {
        if(req.getId() != null && StringUtils.isNotBlank(req.getProvince())
                && StringUtils.isNotBlank(req.getCategory())
                && StringUtils.isNotBlank(req.getBillsType())
                && StringUtils.isNotBlank(req.getApiCodesn())){
            callResultService.updateRoute(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id、省份、险种、票据类型、Api都有值");
        }
    }
    @DeleteMapping("/route/{id}")
    public Response deleteRoute(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteRoute(id);
        return Response.SUCCESS;
    }
    @PostMapping("/queryapi")
    public Response queryServer(@RequestBody Api req)   {
        return Response.SUCCESSDATA(callResultService.findApiByCondition(req));
    }
    @GetMapping("/queryapi")
    public Response queryServerById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findApiById(id));
    }
    @PostMapping("/server")
    public Response insertServer(@RequestBody @Validated Api req) throws BusinessException {
        callResultService.insertServer(req);
        return Response.SUCCESS;
    }
    @PutMapping("/server")
    public Response updatetApi(@RequestBody @Validated Api req) throws BusinessException {
        if(req.getId() != null && StringUtils.isNotBlank(req.getCodesn())
                && StringUtils.isNotBlank(req.getUrl())
                && StringUtils.isNotBlank(req.getName())){
            callResultService.updateApi(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id、编码、名称、url都有值");
        }
    }
    @DeleteMapping("/server/{id}")
    public Response deleteApi(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteApi(id);
        return Response.SUCCESS;
    }
    @PostMapping("/querytype")
    public Response queryType(@RequestBody TypeEnum req)   {
        return Response.SUCCESSDATA(callResultService.findTypeEnumByCondition(req));
    }
    @GetMapping("/querytype")
    public Response queryTypeById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findTypeById(id));
    }
    @PostMapping("/type")
    public Response insertParam(@RequestBody TypeEnum req) throws BusinessException {
        if(StringUtils.isNotBlank(req.getCodesn()) && StringUtils.isNotBlank(req.getType()) && StringUtils.isNotBlank(req.getName())){
            callResultService.insertEnum(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保类型、编码、名称都有值");
        }

    }
    @PutMapping("/type")
    public Response updatetParam(@RequestBody TypeEnum req) throws BusinessException {
        if(req.getId() != null && StringUtils.isNotBlank(req.getCodesn()) && StringUtils.isNotBlank(req.getType()) && StringUtils.isNotBlank(req.getName())){
            callResultService.updateTypeEnum(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id、类型、编码、名称都有值");
        }
    }
    @DeleteMapping("/type/{id}")
    public Response deleteTypeEnum(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteTypeEnum(id);
        return Response.SUCCESS;
    }

    @GetMapping("/typemap")
    public Response queryTypeMap()   {
        return Response.SUCCESSDATA(callResultService.findTypeMap());
    }
}
