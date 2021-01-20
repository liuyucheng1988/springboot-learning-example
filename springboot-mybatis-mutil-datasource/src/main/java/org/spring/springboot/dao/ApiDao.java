package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Api;
import org.spring.springboot.domain.TypeEnum;

import java.util.List;

@Mapper
public interface ApiDao {
    List<Api> findApiByCondition(@Param("vo") Api req);
    List<Api> queryApiByCondition(@Param("vo") Api req);
    List<Api> getApiByCodeOrNameOrUrl(@Param("vo") Api req);

    void insert(@Param("vo") Api req);
    void updateApi(@Param("vo") Api req);
    void logicDeleteApi(@Param("id") Integer id);
//    Integer countServerByCondition(@Param("vo") Server req);
}
