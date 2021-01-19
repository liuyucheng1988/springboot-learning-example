package org.spring.springboot.domain;

import lombok.Data;

/**
 * 用户实体类
 *
 * Created by bysocket on 07/02/2017.
 */
@Data
public class User {

    /**
     * 城市编号
     */
    private Long id;

    /**
     * 城市名称
     */
    private String userName;
    /**
     * 密码
     */
    private String passWord;

    /**
     * 描述
     */
    private String description;

    private Boolean isdel;


}
