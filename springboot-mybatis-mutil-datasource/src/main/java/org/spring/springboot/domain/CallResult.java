package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResult extends PaperBase {
    private BigInteger id;
    private String category;
    private String province;
    private String apiCodesn;
    private Integer duration;
    private String billsType;
    private Integer result;
    private Date createtime;
    private Boolean isdel;
    private String client;


}
