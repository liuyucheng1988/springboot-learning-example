package org.spring.springboot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPwBackend {
    @NotNull(message = "用户名不能为空")
    @Size(min=1, max=45)
    private String userName;
    @NotNull(message = "新密码不能为空")
    @Size(min=1, max=45)
    private String password;
}
