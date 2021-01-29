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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role extends PaperBase {
    private Integer id;
    @NotNull(message = "角色名称不能为空")
    @Size(min=1, max=45)
    private String name;
    private String description;

    public Role(@NotNull(message = "角色名称不能为空") @Size(min = 1, max = 45) String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role(Integer id, @NotNull(message = "角色名称不能为空") @Size(min = 1, max = 45) String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    private Date createtime;


}
