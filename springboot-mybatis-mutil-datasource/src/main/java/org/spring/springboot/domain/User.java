package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 用户实体类
 *
 * Created by bysocket on 07/02/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends PaperBase {

    /**
     * 城市编号
     */
    private Integer id;

    /**
     * 城市名称
     */
    @NotNull(message = "用户名不能为空")
    @Size(min=1, max=45)
    private String userName;
    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    @Size(min=1, max=45)
    private String passWord;

    /**
     * 描述
     */
    private String description;

    private Boolean isdel;
    private Date createtime;
    private Integer type;// 0:管理员1：普通用户,


}
