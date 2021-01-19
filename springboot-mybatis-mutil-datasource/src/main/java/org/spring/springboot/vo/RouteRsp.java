package org.spring.springboot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.domain.CallResult;
import org.spring.springboot.domain.Route;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteRsp extends Route {
    private String categoryName;
    private String provinceName;
    private String apiName;
    private String apiUrl;
    private String apiCodesn;
    private String billsTypeName;
    
}
