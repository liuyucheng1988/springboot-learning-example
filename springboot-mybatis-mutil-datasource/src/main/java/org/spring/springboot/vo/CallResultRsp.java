package org.spring.springboot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.domain.CallResult;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResultRsp extends CallResult {
    private Integer size;//调用次数
    private String categoryName;
    private String provinceName;
    private String apiUrl;
    private String apiName;
    private String billsTypeName;
    private String durationRange;
    private Integer durationMin;
    private Integer durationMax;
}
