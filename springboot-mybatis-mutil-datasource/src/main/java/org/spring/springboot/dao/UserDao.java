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
    List<User> queryLikeByCondition(@Param("vo") User req);

    Integer insertUser(@Param("vo") User req);
    void updateUser(@Param("vo") User req);
    void delUser(@Param("id") Integer id);

    List<User> queryByRoleId(@Param("vo") User req);

    List<User> queryUserWithRoleIdByUserName(@Param("vo") User req);
}
