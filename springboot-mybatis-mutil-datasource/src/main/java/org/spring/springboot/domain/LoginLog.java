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
public class LoginLog extends PaperBase {
    private Integer id;

    private String username;

    private Date loginTime;

    private String location;

    private String ip;

    private String userSystem;

    private String userBrowser;

}
