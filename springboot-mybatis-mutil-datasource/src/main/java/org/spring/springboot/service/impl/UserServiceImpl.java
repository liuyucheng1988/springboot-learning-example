package org.spring.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.spring.springboot.dao.*;
import org.spring.springboot.domain.*;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.UserService;
import org.spring.springboot.util.*;
import org.spring.springboot.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RoleMenuDao roleMenuDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private MenuDao menuDao;

    private static String salt = "a1b2c3d4e5f";
    @Override
    public List<User> findUserByCondition(User req) {
        return userDao.findUserByCondition(req);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) throws BusinessException {
        if(user.getRoleId() != null && user.getRoleId() > 0){
            Role roleReq = new Role();
            roleReq.setId(user.getRoleId());
            List<Role> roles = roleDao.findRoleByCondition(roleReq);
            if(CollectionUtils.isEmpty(roles)){
                throw new BusinessException(500, "角色不存在，id="+user.getRoleId());
            }
            userRoleDao.deleteUserRole(user.getId());

            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(user.getRoleId());
            userRoleDao.insertUserRole(userRole);
        }else{
            throw new BusinessException(500, "修改用户时，请选择角色");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        userDao.delUser(id);
        userRoleDao.deleteUserRole(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) throws BusinessException {
        User req = new User();
        req.setUserName(user.getUserName());
        List<User> users = userDao.findUserByCondition(req);
        if(!CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "已存在相同用户名的用户");
        }
        //密码转换
        String encrptPwd = MD5Utils.md5Encryption(user.getPassWord(), salt);
        user.setPassWord(encrptPwd);
        Integer id = userDao.insertUser(user);
        //
        if(user.getRoleId() != null && user.getRoleId() > 0){
            Role roleReq = new Role();
            roleReq.setId(user.getRoleId());
            List<Role> roles = roleDao.findRoleByCondition(roleReq);
            if(CollectionUtils.isEmpty(roles)){
                throw new BusinessException(500, "角色不存在，id="+user.getRoleId());
            }
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(user.getRoleId());
            userRoleDao.insertUserRole(userRole);
        }else{
            throw new BusinessException(500, "添加用户时，请选择角色");
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePwd(UserPw user) throws BusinessException {
        if(!user.getPassword().equals(user.getQrpassword())){
            throw new BusinessException(500, "新密码和确认新密码不一致");
        }
        User req = new User();
        req.setUserName(user.getUserName());
        List<User> users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "不存在该用户");
        }
        String lastEncrptPwd = MD5Utils.md5Encryption(user.getLastpassword(), salt);
        req.setPassWord(lastEncrptPwd);
        users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "原密码不对");
        }
        String encrptPwd = MD5Utils.md5Encryption(user.getPassword(), salt);
        User updateUser = new User();
        updateUser.setUserName(user.getUserName());
        updateUser.setPassWord(encrptPwd);
        userDao.updateUser(updateUser);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePwdBackend(UserPwBackend user) throws BusinessException {
        User req = new User();
        req.setUserName(user.getUserName());
        List<User> users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "不存在该用户");
        }
        String encrptPwd = MD5Utils.md5Encryption(user.getPassword(), salt);
        User updateUser = new User();
        updateUser.setUserName(user.getUserName());
        updateUser.setPassWord(encrptPwd);
        userDao.updateUser(updateUser);
    }
    @Override
    public List<Role> findRolesByUserId(Integer userId) {
        return roleDao.findRolesByUserId(userId);
    }
    @Override
    public List<Menu> findMenuTree() {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer userId = activeUser.getUser().getId();
        List<Integer> menuIds = menuDao.findMenuIdsByUserId(userId);
        if(CollectionUtils.isEmpty(menuIds)){
            return new ArrayList<>();
        }
        List<Menu> menus = menuDao.findMenusByIds(menuIds);
        return MenuTreeBuilder.build(menus);
    }
    @Override
    public List<Menu> findMenuTreeByRoleId(Integer roleId){
        RoleMenu req = new RoleMenu();
        req.setRoleId(roleId);
        List<RoleMenu> roleMenus = roleMenuDao.findRoleMenuByCondition(req);
        if(CollectionUtils.isEmpty(roleMenus)){
            return null;
        }
        List<Integer> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        List<Menu> menus = menuDao.findMenusByIds(menuIds);
        return MenuTreeBuilder.build(menus);
    }
    @Override
    public List<Menu> findMenuRightsTree(){
        List<Menu> menus = menuDao.findMenusByIds(null);
        return MenuTreeBuilder.build(menus);
    }
    /**
     * 查询权限
     * @param roles 用户的角色
     * @return
     */
    @Override
    public List<Menu> findMenuByRoles(List<Role> roles) {
        List<Menu> menus=new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            Set<Integer> menuIds=new HashSet<>();//存放用户的菜单id
            List<RoleMenu> roleMenus;
            for (Role role : roles) {
                //根据角色ID查询权限ID
                RoleMenu req = new RoleMenu();
                req.setRoleId(role.getId());
                roleMenus= roleMenuDao.findRoleMenuByCondition(req);
                if(!CollectionUtils.isEmpty(roleMenus)){
                    for (RoleMenu roleMenu : roleMenus) {
                        menuIds.add(roleMenu.getMenuId());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(menuIds)){
                menus = menuDao.findMenusByIds(menuIds);
            }
        }
        return menus;
    }

    @Override
    public UserInfoVO info() {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUsername(activeUser.getUser().getUserName());
        userInfoVO.setUrl(activeUser.getUrls());
        List<String> roleNames = activeUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        userInfoVO.setRoles(roleNames);
        userInfoVO.setPerms(activeUser.getPermissions());
        userInfoVO.setIsAdmin(activeUser.getUser().getType()== UserTypeEnum.SYSTEM_ADMIN.getTypeCode());
        return userInfoVO;
    }

    @Override
    public PageVO<Menu> queryMenusByNameLike(Menu req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<Menu> menus = menuDao.queryMenusByNameLike(req);
        PageInfo<Menu> info = new PageInfo<>(menus);
        return new PageVO<Menu>(info.getTotal(), menus);
    }
    @Override
    public PageVO<User> queryUserLikeByCondition(User req) {
        String orderBy = PageUtils.getOrderBy(req);
        PageHelper.startPage(req.getPageNum(), req.getPageSize(), orderBy);
        List<User> users = new ArrayList<>();
        List<KeyValue> roleIdName = roleDao.findRoleMap();
        Map<Integer, String> roleIdNameMap = roleIdName.stream().collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
        if(req.getRoleId() != null ){
            users = userDao.queryByRoleId(req);
        }else{
            users = userDao.queryUserWithRoleIdByUserName(req);
        }
        users.forEach(i->i.setRoleName(roleIdNameMap.get(i.getRoleId())));
        PageInfo<User> info = new PageInfo<>(users);
        return new PageVO<User>(info.getTotal(), users);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleRight(Integer roleId, Integer rightId) throws BusinessException {
        //根据rightId 查询孩子
        if(roleId == null || rightId == null){
            return;
        }
        List<Integer> idsToDel = new ArrayList<>();
        idsToDel.add(rightId);
        List<Menu> menus = menuDao.findMenusByIds(Arrays.asList(rightId));
        if(CollectionUtils.isEmpty(menus)){
            throw new BusinessException(500, "删除权限失败，没有查询到该权限。id="+rightId);
        }
        Menu menu = menus.get(0);
        if(menu.getLevel() == 1){
            List<Integer> ids = menuDao.queryIdsByParentId(rightId);
            if(!CollectionUtils.isEmpty(ids)){
                idsToDel.addAll(ids);
                List<Integer> idsChildren = menuDao.queryIdsByParentIds(ids);
                idsToDel.addAll(idsChildren);
            }
        }else if(menu.getLevel() == 2){
            List<Integer> ids = menuDao.queryIdsByParentId(rightId);
            if(!CollectionUtils.isEmpty(ids)){
                idsToDel.addAll(ids);
            }
        }
        roleMenuDao.deleteByRoleidAndMenuIds(roleId, idsToDel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRole(RoleVo req) {
        roleDao.insertRole(new Role(req.getRoleName(), req.getRoleDesc()));
    }

    @Override
    public List<RoleVo> listRole() {
       List<Role> roles = roleDao.findRoleByCondition(new Role());
        if(CollectionUtils.isEmpty(roles)){
            return new ArrayList<>();
        }

        List<RoleVo> roleVos =  roles.stream().map(role -> new RoleVo(role.getId(), role.getName(), role.getDescription())).collect(Collectors.toList());
        List<RoleMenu> roleMenus = roleMenuDao.findRoleMenuByCondition(new RoleMenu());
        Map<Integer, List<RoleMenu>> groupByRoleId = roleMenus.stream().collect(Collectors.groupingBy(RoleMenu::getRoleId));
        List<Menu> menuList = menuDao.findMenuByCondition(new Menu());
        Map<Integer, Menu> menuMap = menuList.stream().collect(Collectors.toMap(Menu::getId, a -> a,(k1,k2)->k1));
        for(RoleVo role : roleVos){
            List<RoleMenu> roleMenuList = groupByRoleId.get(role.getId());
            if(!CollectionUtils.isEmpty(roleMenuList)){
                List<Integer> menuIds = roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
                List<Menu> menus = new ArrayList<>();
                for(Integer menuId : menuIds){
                    menus.add(Menu.copyFromMenu(menuMap.get(menuId)));
                }
                List<Menu> children = MenuTreeBuilder.build(menus);
                role.setChildren(children);
            }
        }
        return roleVos;
    }
    @Override
    public List<KeyValue> findRoleMap() {
        return roleDao.findRoleMap();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleRights(Integer roleId, RoleRightsReq req) throws BusinessException {
        if(roleId == null || roleId == 0){
            throw new BusinessException(500, "请传入有效的roleId");
        }
        String ridStr = req.getRids();
        roleMenuDao.deleteByRoleidAndMenuIds(roleId, null);
        if(StringUtils.isBlank(ridStr)){
            return;
        }
        //插入新的权限
        String[] arrayRid = ridStr.split(",");
        int size = arrayRid.length;
        List<Integer> menuIds = new ArrayList<>();
        Integer rId = 0;
        List<RoleMenu> roleMenus = new ArrayList<>();
        for(int i=0; i<size; i++){
            rId = Integer.valueOf(arrayRid[i]);
            if(!menuIds.contains(rId)){
                menuIds.add(rId);
                roleMenus.add(new RoleMenu(roleId, rId));
            }
        }
        if(CollectionUtils.isEmpty(menuIds)){
            return;
        }
        Integer idMax = roleMenuDao.insertBatchRoleMenu(roleMenus);
        log.info("分配权限修改，idMax="+idMax);
    }

    @Override
    public RoleVo queryRoleById(Integer roleId) throws BusinessException {
        Role req = new Role();
        req.setId(roleId);
        List<Role> roles = roleDao.findRoleByCondition(req);
        if(CollectionUtils.isEmpty(roles)){
            throw new BusinessException(500, "不存在该角色，角色id="+roleId);
        }
        Role role = roles.get(0);
        return new RoleVo(role.getId(), role.getName(), role.getDescription());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleVo req) {
        roleDao.updateRole(new Role(req.getId(), req.getRoleName(), req.getRoleDesc()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer roleId) {
        roleDao.deleteRoleById(roleId);
        roleDao.deleteRolemenuByRoleId(roleId);
        roleDao.deleteUserroleByRoleId(roleId);
    }

    @Override
    public void changeUserState(Integer id, boolean state) {
        User user = new User();
        user.setId(id);
        user.setState(state);
        userDao.updateUser(user);
    }

    @Override
    public User queryUserById(Integer id) throws BusinessException {
        User req = new User();
        req.setId(id);
        List<User> users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "根据id查询不到用户。id="+id);
        }
        User user = users.get(0);
        UserRole userRoleReq = new UserRole();
        userRoleReq.setUserId(id);
        List<UserRole> userRoles = userRoleDao.findUserRoleByCondition(userRoleReq);
        if(!CollectionUtils.isEmpty(userRoles)){
            user.setRoleId(userRoles.get(0).getRoleId());
        }
        return user;
    }



    @Override
    public String login(String username, String password) throws BusinessException {
        User req = new User();
        req.setUserName(username);
        req.setState(true);
//        req.setPassWord(password);
        List<User> users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "不存在该用户");
        }
        User user = users.get(0);
        String token;
        String target = MD5Utils.md5Encryption(password, salt);

        //生成Token
        try {
            token = JWTUtils.sign(username, target);
            JWTToken jwtToken = new JWTToken(token);
            SecurityUtils.getSubject().login(jwtToken);
        } catch (Exception e) {
            throw new BusinessException(500,"身份认证失败，请联系管理员", e.getMessage());
        }
        return token;
    }
}
