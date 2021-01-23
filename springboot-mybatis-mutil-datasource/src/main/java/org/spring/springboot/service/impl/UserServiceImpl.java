package org.spring.springboot.service.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.spring.springboot.dao.UserDao;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.service.UserService;
import org.spring.springboot.util.JWTToken;
import org.spring.springboot.util.JWTUtils;
import org.spring.springboot.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 用户业务实现层
 *
 * Created by bysocket on 07/02/2017.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao; // 主数据源
    private static String salt = "a1b2c3d4e5f";
    @Override
    public List<User> findUserByCondition(User req) {
        return userDao.findUserByCondition(req);
    }
    @Override
    public String login(String username, String password) throws BusinessException {
        User req = new User();
        req.setUserName(username);
        req.setPassWord(password);
        List<User> users = userDao.findUserByCondition(req);
        if(CollectionUtils.isEmpty(users)){
            throw new BusinessException(500, "用户名或者密码错误");
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
            throw new BusinessException(500, e.getMessage());
        }
        return token;
    }
}
