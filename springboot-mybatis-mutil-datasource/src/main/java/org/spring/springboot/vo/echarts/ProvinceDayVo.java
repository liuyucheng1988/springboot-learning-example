package org.spring.springboot.vo.echarts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvinceDayVo {
    private String title;
    private List<ProvinceDayData> data;
    private List<String> days;
    private List<String> provinces;

}
