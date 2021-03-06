package com.huawei.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResult {
    private BigInteger id;
    private String category;
    private String server;
    private Integer duration;
    private String billsType;
    private Integer result;
    private Date createtime;
    private Boolean isdel;
    private String client;


}
