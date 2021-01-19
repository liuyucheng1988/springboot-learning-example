package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Api extends PaperBase {
    private Integer id;
    @NotNull(message = "编码不能为空")
    private String codesn;
    @NotNull(message = "名称不能为空")
    private String name;
    @NotNull(message = "url不能为空")
    private String url;
    private String description;
    private Date updatetime;
    private Boolean isdel;


}
