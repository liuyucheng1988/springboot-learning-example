package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.Role;

import java.util.List;
import java.util.Set;

@Mapper
public interface MenuDao {
    List<Menu> findMenuByCondition(@Param("vo") Menu req);
    List<Menu> findMenusByIds(@Param("menuids") Set<Integer> menuIds);
//    List<Api> queryApiByCondition(@Param("vo") Api req);
//    List<Api> getApiByCodeOrNameOrUrl(@Param("vo") Api req);

//    void insert(@Param("vo") Role req);
//    void updateRole(@Param("vo") Role req);
//    void logicDeleteApi(@Param("id") Integer id);
//    Integer countServerByCondition(@Param("vo") Server req);
}
