package org.spring.springboot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaperBase {
    private Integer pageNum;
    private Integer pageSize;
    //排序
    private String field; //排序字段
    private Integer desc;// 0 升序 1 倒序
    private Boolean isdel;
}
