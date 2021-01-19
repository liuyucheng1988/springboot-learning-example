package org.spring.springboot.service;

import org.spring.springboot.domain.User;

import java.util.List;

/**
 * 用户业务接口层
 *
 * Created by bysocket on 07/02/2017.
 */
public interface UserService {

    List<User> findUserByCondition(User req);
}
