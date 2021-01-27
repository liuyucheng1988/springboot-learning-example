package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.User;

import java.util.List;

/**
 * 用户 DAO 接口类
 *
 * Created by bysocket on 07/02/2017.
 */
@Mapper
public interface UserDao {

    List<User> findUserByCondition(@Param("vo") User req);
    void insertUser(@Param("vo") User req);
    void updateUser(@Param("vo") User req);
}
