package org.spring.springboot.service;

import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.vo.UserInfoVO;
import org.spring.springboot.vo.UserPw;

import java.util.List;

/**
 * 用户业务接口层
 *
 * Created by bysocket on 07/02/2017.
 */
public interface UserService {

    List<User> findUserByCondition(User req);
    String login(String username, String password) throws BusinessException;
    void addUser(User user) throws BusinessException;
    void changePwd(UserPw user) throws BusinessException;

    List<Role> findRolesById(Integer id);

    List<Menu> findMenuByRoles(List<Role> roles);

    UserInfoVO info();
}
