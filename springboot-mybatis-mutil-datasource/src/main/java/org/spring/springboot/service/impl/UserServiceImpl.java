package org.spring.springboot.service.impl;

import org.apache.shiro.SecurityUtils;
import org.spring.springboot.dao.MenuDao;
import org.spring.springboot.dao.RoleDao;
import org.spring.springboot.dao.RoleMenuDao;
import org.spring.springboot.dao.UserDao;
import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.RoleMenu;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.UserService;
import org.spring.springboot.util.JWTToken;
import org.spring.springboot.util.JWTUtils;
import org.spring.springboot.util.MD5Utils;
import org.spring.springboot.util.UserTypeEnum;
import org.spring.springboot.vo.ActiveUser;
import org.spring.springboot.vo.UserInfoVO;
import org.spring.springboot.vo.UserPw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RoleMenuDao roleMenuDao;
    @Autowired
    private MenuDao menuDao;

    private static String salt = "a1b2c3d4e5f";
    @Override
    public List<User> findUserByCondition(User req) {
        return userDao.findUserByCondition(req);
    }
    @Override
    public void addUser(User user) throws BusinessException {
        User req = new User();
        req.setUserName(user.getUserName());
        List<User> users = userDao.findUserByCondition(req);
        if(!CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "已存在该用户");
        }
        //密码转换
        String encrptPwd = MD5Utils.md5Encryption(user.getPassWord(), salt);
        user.setPassWord(encrptPwd);
        userDao.insertUser(user);
    }
    @Override
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
    public List<Role> findRolesById(Integer id) {
        Role req = new Role();
        req.setId(id);
        return roleDao.findRoleByCondition(req);
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
    public String login(String username, String password) throws BusinessException {
        User req = new User();
        req.setUserName(username);
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
