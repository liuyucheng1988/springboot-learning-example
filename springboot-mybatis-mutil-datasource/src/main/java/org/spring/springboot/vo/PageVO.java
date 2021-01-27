package org.spring.springboot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageVO<T> {
    private long total;

    private List<T> rows=new ArrayList<>();

    public PageVO(long total, List<T> data) {
        this.total = total;
        this.rows = data;
    }
}
