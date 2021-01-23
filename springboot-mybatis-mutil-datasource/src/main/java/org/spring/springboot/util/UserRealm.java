package org.spring.springboot.util;

import lombok.SneakyThrows;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class UserRealm extends AuthorizingRealm {

//    @Autowired
//    private UserService userService;
@Autowired
private UserService userService;


    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
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
        String token = (String) auth.getCredentials() ;
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtils.getUsername(token);

        if (username == null) {
            throw new AuthenticationException(" 身份认证失败，请重新登入！");
        }

        User req = new User();
        req.setUserName(username);
        List<User> users = userService.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new AccountException("账号不存在!");
        }
        User user = users.get(0);
        if(JWTUtils.isExpire(token)){
            throw new AuthenticationException(" 身份凭证过期，请退出重新登录！");
        }

        try {
            JWTUtils.verify(token, username, user.getPassWord());
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException(" 身份凭证失效，请退出重新登录！");
        }

        //过滤出url,和用户的权限
//        ActiveUser activeUser = new ActiveUser();
//        activeUser.setRoles(roles);
//        activeUser.setUser(userBean);
//        activeUser.setMenus(menus);
//        activeUser.setUrls(urls);
//        activeUser.setPermissions(perms);
//        new SimpleAuthorizationInfo()
        return new SimpleAuthenticationInfo(user, token, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}
