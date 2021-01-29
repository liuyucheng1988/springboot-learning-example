package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.RoleMenu;
import org.spring.springboot.domain.UserRole;

import java.util.List;

@Mapper
public interface UserRoleDao {
    List<UserRole> findUserRoleByCondition(@Param("vo") UserRole req);
    void insertUserRole(@Param("vo") UserRole req);

    void updateUserRole(@Param("vo") UserRole userRole);
    void deleteUserRole(@Param("userId") Integer userId);
//    List<Api> queryApiByCondition(@Param("vo") Api req);
//    List<Api> getApiByCodeOrNameOrUrl(@Param("vo") Api req);

//    void insert(@Param("vo") Role req);
//    void updateRole(@Param("vo") Role req);
//    void logicDeleteApi(@Param("id") Integer id);
//    Integer countServerByCondition(@Param("vo") Server req);
}
