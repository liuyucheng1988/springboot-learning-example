package org.spring.springboot.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.*;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.CallResultService;
import org.spring.springboot.service.UserService;
import org.spring.springboot.util.Constant;
import org.spring.springboot.util.Response;
import org.spring.springboot.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private CallResultService callResultService;
    @PostMapping(value = "/permission")
    @ApiOperation(value = "权限列表查询")
    @RequiresPermissions({"right:list"})
    public Response getMenusList(@RequestBody Menu req) {
        PageVO<Menu> menus = userService.queryMenusByNameLike(req);
        return Response.SUCCESSDATA(menus);
    }
    @DeleteMapping("/roles/{roleId}/rights/{rightId}")
    @ApiOperation(value = "删除角色绑定的权限")
//    @RequiresPermissions({"roleright:del"})
    public Response deleteRoleRight(@PathVariable Integer roleId, @PathVariable Integer rightId) throws BusinessException {
        userService.deleteRoleRight(roleId, rightId);
        return Response.SUCCESSDATA(userService.findMenuTreeByRoleId(roleId));
    }
    @PostMapping("/roles")
    @ApiOperation(value = "添加角色")
    @RequiresPermissions({"role:add"})
    public Response addRole(@RequestBody @Validated RoleVo req) throws BusinessException {
        userService.insertRole(req);
        return Response.SUCCESS;
    }
    @PutMapping("/roles/{roleId}")
    @ApiOperation(value = "编辑角色")
    @RequiresPermissions({"role:edit"})
    public Response editRole(@PathVariable Integer roleId, @RequestBody @Validated RoleVo req) throws BusinessException {
        if(roleId == null || roleId == 0){
            throw new BusinessException(500, "角色id必须大于0");
        }
        req.setId(roleId);
        userService.updateRole(req);
        return Response.SUCCESS;
    }
    @DeleteMapping("/roles/{roleId}")
    @ApiOperation(value = "删除角色")
    @RequiresPermissions({"role:del"})
    public Response editRole(@PathVariable Integer roleId) throws BusinessException {
        if(roleId == null || roleId == 0){
            throw new BusinessException(500, "角色id必须大于0");
        }
        userService.deleteRole(roleId);
        return Response.SUCCESS;
    }
    @GetMapping("/roles/{roleId}")
    @ApiOperation(value = "根据id查询角色")
    public Response queryRoleById(@PathVariable Integer roleId) throws BusinessException {
        RoleVo roleVo = userService.queryRoleById(roleId);
        return Response.SUCCESSDATA(roleVo);
    }
    @GetMapping("/queryrole")
    @ApiOperation(value = "角色下拉列表")
    public Response findRoleMap() throws BusinessException {
        return Response.SUCCESSDATA(userService.findRoleMap());
    }
    @GetMapping("/roles")
    @ApiOperation(value = "角色列表")
    @RequiresPermissions({"role:list"})
    public Response queryRole() throws BusinessException {
        List<RoleVo> roleVos = userService.listRole();
        return Response.SUCCESSDATA(roleVos);
    }
    @GetMapping("/rights/tree")
    @ApiOperation(value = "获取权限树")
    public Response queryRightsByRoleId() throws BusinessException {
        List<Menu> menuList = userService.findMenuRightsTree();
        return Response.SUCCESSDATA(menuList);
    }
    @PostMapping("/roles/{roleId}/rights")
    @ApiOperation(value = "给角色分配权限")
    @RequiresPermissions({"role:add"})
    public Response addRoleRights(@PathVariable Integer roleId, @RequestBody RoleRightsReq req) throws BusinessException {
        userService.addRoleRights(roleId, req);
        return Response.SUCCESS;
    }

    @GetMapping(value = "/menus")
    public Response getMenus() {
        List<Menu> menus = userService.findMenuTree();
        return Response.SUCCESSDATA(menus);
    }
    @PostMapping("/login")
    public Response login(@RequestBody @Validated  User user) throws BusinessException {
        log.info("login user="+user);
        String token = userService.login(user.getUserName(), user.getPassWord());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return Response.SUCCESSDATA(tokenMap);
    }
    @ApiOperation(value = "用户信息", notes = "用户登入信息")
    @GetMapping("/user")
    public Response info() {
        UserInfoVO userInfoVO = userService.info();
        return Response.SUCCESSDATA(userInfoVO);
    }
    @ApiOperation(value = "根据用户id查询用户信息")
    @GetMapping("/user/{id}")
    public Response queryUserById(@PathVariable Integer id) throws BusinessException {
        User user = userService.queryUserById(id);
        return Response.SUCCESSDATA(user);
    }
    @ApiOperation(value = "删除用户信息")
    @DeleteMapping("/user/{id}")
    public Response delUserById(@PathVariable Integer id) throws BusinessException {
        userService.deleteUser(id);
        return Response.SUCCESS;
    }
    @PutMapping("/user")
    public Response updateUser(@RequestBody @Validated  User user) throws BusinessException {
        log.info("updateUser user="+user);
        userService.updateUser(user);
        return Response.SUCCESS;
    }
    @PostMapping("/user")
    public Response addUser(@RequestBody @Validated  User user) throws BusinessException {
        log.info("add user="+user);
        userService.addUser(user);
        return Response.SUCCESS;
    }
    @PutMapping("/users/{id}/state/{state}")
    public Response changeUserState(@PathVariable Integer id, @PathVariable boolean state) throws BusinessException {
        userService.changeUserState(id, state);
        return Response.SUCCESS;
    }
    @PostMapping("/queryuser")
    public Response queryUser(@RequestBody User user) throws BusinessException {
        log.info("query user="+user);
        return Response.SUCCESSDATA(userService.queryUserLikeByCondition(user));
    }
    @PostMapping("/password")
    @ApiOperation(value = "修改密码")
    @RequiresPermissions({"password:edit"})
    public Response changePwd(@RequestBody @Validated  UserPw user) throws BusinessException {
        log.info("change pwd userPw="+user);
        userService.changePwd(user);
        return Response.SUCCESS;
    }
    @PostMapping("/pwdhand")
    @ApiOperation(value = "忘记密码并修改密码")
    @RequiresPermissions({"password:calledit"})
    public Response changePwdBackend(@RequestBody @Validated  UserPwBackend user) throws BusinessException {
        log.info("changePwdBackend userPw="+user);
        userService.changePwdBackend(user);
        return Response.SUCCESS;
    }
    @PostMapping("/querymonitor")
    @ApiOperation(value = "分组统计")
    @RequiresPermissions({"request:group"})
    public Response queryMonitor(@RequestBody CallResultReq req)   {
        if(req.getCreateTimeFrom() == null || req.getCreateTimeTo() == null){
            return Response.FAIL;
        }
        return Response.SUCCESSDATA(callResultService.findByCondition(req));
    }
    @PostMapping("/listmonitor")
    @ApiOperation(value = "请求列表查询")
    @RequiresPermissions({"request:list"})
    public Response listMonitor(@RequestBody CallResultPatchReq req)   {
        if(req.getCreateTimeFrom() == null || req.getCreateTimeTo() == null){
            return Response.FAIL;
        }
        return Response.SUCCESSDATA(callResultService.finCallResultPatchByCondition(req));
    }
    @PostMapping("/querydetail")
    @ApiOperation(value = "请求分组详情查看")
    @RequiresPermissions({"request:groupdetail"})
    public Response queryDetail(@RequestBody CallResultReq req)   {
        if(req.getCreateTimeFrom() == null || req.getCreateTimeTo() == null){
            return Response.FAIL;
        }
        return Response.SUCCESSDATA(callResultService.finCallResultItemByCondition(req));
    }
    @PostMapping("/monitor")
    public Response saveMonitor(@RequestBody CallResult req)   {
        callResultService.insertCallResult(req);
        return Response.SUCCESS;
    }
    @PostMapping("/queryroute")
    @ApiOperation(value = "路由列表查询")
    @RequiresPermissions({"route:list"})
    public Response queryRoute(@RequestBody Route req)   {
        return Response.SUCCESSDATA(callResultService.findRouteByCondition(req));
    }
    @GetMapping("/queryroute")
    public Response queryRouteById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findRouteById(id));
    }
    @PostMapping("/route")
    @ApiOperation(value = "添加路由")
    @RequiresPermissions({"route:add"})
    public Response saveRoute(@RequestBody @Validated Route req) throws BusinessException {
        callResultService.insertRoute(req);
        return Response.SUCCESS;
    }
    @PutMapping("/route")
    @ApiOperation(value = "修改路由")
    @RequiresPermissions({"route:edit"})
    public Response updatetRoute(@RequestBody @Validated Route req) throws BusinessException {
        if(req.getId() != null){
            callResultService.updateRoute(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id有值");
        }
    }
    @DeleteMapping("/route/{id}")
    @ApiOperation(value = "删除路由")
    @RequiresPermissions({"route:del"})
    public Response deleteRoute(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteRoute(id);
        return Response.SUCCESS;
    }
    @PostMapping("/queryapi")
    @ApiOperation(value = "Api列表查询")
    @RequiresPermissions({"api:list"})
    public Response queryServer(@RequestBody Api req)   {
        return Response.SUCCESSDATA(callResultService.findApiByCondition(req));
    }
    @GetMapping("/queryapi")
    public Response queryServerById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findApiById(id));
    }
    @PostMapping("/server")
    @ApiOperation(value = "添加Api")
    @RequiresPermissions({"api:add"})
    public Response insertServer(@RequestBody @Validated Api req) throws BusinessException {
        callResultService.insertServer(req);
        return Response.SUCCESS;
    }
    @PutMapping("/server")
    @ApiOperation(value = "修改Api")
    @RequiresPermissions({"api:edit"})
    public Response updatetApi(@RequestBody @Validated Api req) throws BusinessException {
        if(req.getId() != null){
            callResultService.updateApi(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id有值");
        }
    }
    @DeleteMapping("/server/{id}")
    @ApiOperation(value = "删除Api")
    @RequiresPermissions({"api:del"})
    public Response deleteApi(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteApi(id);
        return Response.SUCCESS;
    }
    @PostMapping("/querytype")
    @ApiOperation(value = "参数列表查询")
    @RequiresPermissions({"param:list"})
    public Response queryType(@RequestBody TypeEnum req)   {
        return Response.SUCCESSDATA(callResultService.findTypeEnumByCondition(req));
    }
    @GetMapping("/querytype")
    public Response queryTypeById(@RequestParam(value = "id", required = true) Integer id)   {
        return Response.SUCCESSDATA(callResultService.findTypeById(id));
    }
    @PostMapping("/type")
    @ApiOperation(value = "添加参数")
    @RequiresPermissions({"param:add"})
    public Response insertParam(@RequestBody @Validated TypeEnum req) throws BusinessException {
        if(req.getCodesn().contains(Constant.UNDERSCORE)){
            throw new BusinessException(500, "编码不能含有_");
        }
        callResultService.insertEnum(req);
        return Response.SUCCESS;
    }
    @PutMapping("/type")
    @ApiOperation(value = "修改参数")
    @RequiresPermissions({"param:edit"})
    public Response updatetParam(@RequestBody @Validated TypeEnum req) throws BusinessException {
        if(req.getId() != null){
            callResultService.updateTypeEnum(req);
            return Response.SUCCESS;
        }else{
            return Response.FAILMSG("参数不对，请确保id有值");
        }
    }
    @DeleteMapping("/type/{id}")
    @ApiOperation(value = "删除参数")
    @RequiresPermissions({"param:del"})
    public Response deleteTypeEnum(@PathVariable Integer id) throws BusinessException {
        callResultService.logicDeleteTypeEnum(id);
        return Response.SUCCESS;
    }

    @GetMapping("/typemap")
    public Response queryTypeMap()   {
        return Response.SUCCESSDATA(callResultService.findTypeMap());
    }
}
