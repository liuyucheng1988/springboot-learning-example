package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Api;
import org.spring.springboot.domain.Role;
import org.spring.springboot.vo.KeyValue;
import org.spring.springboot.vo.KeyValueVO;

import java.util.List;

@Mapper
public interface RoleDao {
    List<Role> findRoleByCondition(@Param("vo") Role req);
    List<Role> findRolesByUserId(@Param("userid") Integer userId);
    List<KeyValue> findRoleMap();
    void insertRole(@Param("vo") Role req);

    void updateRole(@Param("vo") Role role);
    void deleteRoleById(@Param("roleId")  Integer roleId);
    void deleteUserroleByRoleId(@Param("roleId")  Integer roleId);
    void deleteRolemenuByRoleId(@Param("roleId")  Integer roleId);
//    List<Api> queryApiByCondition(@Param("vo") Api req);
//    List<Api> getApiByCodeOrNameOrUrl(@Param("vo") Api req);

//    void insert(@Param("vo") Role req);
//    void updateRole(@Param("vo") Role req);
//    void logicDeleteApi(@Param("id") Integer id);
//    Integer countServerByCondition(@Param("vo") Server req);
}
