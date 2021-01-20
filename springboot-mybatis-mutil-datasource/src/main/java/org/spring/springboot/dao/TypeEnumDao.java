package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.TypeEnum;
import org.spring.springboot.vo.KeyValueVO;

import java.util.List;
@Mapper
public interface TypeEnumDao {
    List<TypeEnum> findTypeEnumByCondition(@Param("vo") TypeEnum req);
    void insert(@Param("vo") TypeEnum req);

    List<KeyValueVO> findTypeMap();
    void deleteTypeEnum(@Param("id") Integer id);
    void updateTypeEnum(@Param("vo") TypeEnum req);
    void logicDeleteTypeEnum(@Param("id") Integer id);
//    Integer countTypeEnumByCondition(@Param("vo") TypeEnum req);
}
