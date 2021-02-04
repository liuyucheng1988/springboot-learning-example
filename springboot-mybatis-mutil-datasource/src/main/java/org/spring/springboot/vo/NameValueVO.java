package org.spring.springboot.vo;

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
public class NameValueVO {
    private String name;
    private Integer value;
    private List<NameValueVO> children;

    public NameValueVO(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
