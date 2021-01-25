package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeEnum extends PaperBase {
    private Integer id;
    @NotNull(message = "类型不能为空")
    @Size(min=1, max=45)
    private String type;
    private String typeName;
    @NotNull(message = "编码不能为空")
    @Size(min=1, max=45)
    private String codesn;
    @NotNull(message = "名称不能为空")
    @Size(min=1, max=45)
    private String name;
    private Boolean isdel;
    private Date updatetime;
}
