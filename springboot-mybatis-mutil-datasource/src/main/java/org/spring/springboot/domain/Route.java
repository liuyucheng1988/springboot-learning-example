package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Route extends PaperBase {
    private Integer id;
    @NotNull(message = "险种不能为空")
    @Size(min=1, max=45)
    private String category;
    @NotNull(message = "省份不能为空")
    @Size(min=1, max=45)
    private String province;
    @NotNull(message = "Api不能为空")
    @Size(min=1, max=45)
    private String apiCodesn;
    @NotNull(message = "票据类型不能为空")
    @Size(min=1, max=45)
    private String billsType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "生效起始时间不能为空")
    private Date effectivetimeFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "生效结束时间不能为空")
    private Date effectivetimeTo;
    private String description;
    private Date updatetime;
    private Boolean isdel;

}
