package org.spring.springboot.util;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.UserService;
import org.spring.springboot.vo.ActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }


    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @SneakyThrows
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
//        String token = new String((char[]) auth.getCredentials()) ;
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtils.getUsername(token);

        if (username == null) {
            throw new AuthenticationException(" 身份认证失败，请重新登入！");
        }

        User req = new User();
        req.setUserName(username);
        List<User> users = userService.findUserByCondition(req);
        if (CollectionUtils.isEmpty(users)) {
            throw new AccountException("账号不存在!");
        }
        User user = users.get(0);
        if (JWTUtils.isExpire(token)) {
            throw new AuthenticationException(" 身份凭证过期，请退出重新登录！");
        }

        try {
            JWTUtils.verify(token, username, user.getPassWord());
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException(" 身份凭证失效，请退出重新登录！");
        }

        // shiro url权限
        //如果验证通过，获取用户的角色
        List<Role> roles = userService.findRolesByUserId(user.getId());
        //查询用户的所有菜单(包括了菜单和按钮)
        List<Menu> menus = userService.findMenuByRoles(roles);

        Set<String> urls = new HashSet<>();
        Set<String> perms = new HashSet<>();
        if (!CollectionUtils.isEmpty(menus)) {
            for (Menu menu : menus) {
                String url = menu.getUrl();
                String per = menu.getPermissions();
                if (menu.getType() == 0 && StringUtils.isNotBlank(url)) {
                    urls.add(menu.getUrl());
                }
                if (menu.getType() == 1 && StringUtils.isNotBlank(per)) {
                    perms.add(per);
                }
            }
        }
        //过滤出url,和用户的权限
        ActiveUser activeUser = new ActiveUser();
        activeUser.setRoles(roles);
        activeUser.setUser(user);
        activeUser.setMenus(menus);
        activeUser.setUrls(urls);
        activeUser.setPermissions(perms);
        return new SimpleAuthenticationInfo(activeUser, token, getName());
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //permission 角色
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        if (activeUser.getUser().getUserName().equals("admin")) {
            //超级管理员
            authorizationInfo.addStringPermission("*:*");
        } else {
            List<String> permissions = new ArrayList<>(activeUser.getPermissions());
            List<Role> roleList = activeUser.getRoles();
            //授权角色
            if (!CollectionUtils.isEmpty(roleList)) {
                for (Role role : roleList) {
                    //注册用户关联的角色名称
                    authorizationInfo.addRole(role.getName());
                }
            }
            //授权权限
            if (!CollectionUtils.isEmpty(permissions)) {
                for (String permission : permissions) {
                    if (StringUtils.isNotBlank(permission)) {
                        authorizationInfo.addStringPermission(permission);
                    }
                }
            }
        }
        return authorizationInfo;
    }

}
