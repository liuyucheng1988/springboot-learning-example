package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu extends PaperBase {
    private Integer id;
    private Integer parentId;
    private String name;
    private String url;
    private Integer type;
    private Integer orderNum;
    private Date createtime;
    private String permissions;
}
