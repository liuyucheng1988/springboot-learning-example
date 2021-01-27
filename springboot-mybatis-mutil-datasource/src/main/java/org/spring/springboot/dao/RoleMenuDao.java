package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.RoleMenu;

import java.util.List;

@Mapper
public interface RoleMenuDao {
    List<RoleMenu> findRoleMenuByCondition(@Param("vo") RoleMenu req);
//    List<Api> queryApiByCondition(@Param("vo") Api req);
//    List<Api> getApiByCodeOrNameOrUrl(@Param("vo") Api req);

//    void insert(@Param("vo") Role req);
//    void updateRole(@Param("vo") Role req);
//    void logicDeleteApi(@Param("id") Integer id);
//    Integer countServerByCondition(@Param("vo") Server req);
}
