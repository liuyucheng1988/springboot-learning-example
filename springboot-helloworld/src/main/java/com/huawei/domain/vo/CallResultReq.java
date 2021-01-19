package com.huawei.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huawei.entity.CallResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResultReq extends CallResult {
    private Date createTimeFrom;
    private Date createTimeTo;
    private Integer durationFrom;
    private Integer durationTo;
    
    
}
