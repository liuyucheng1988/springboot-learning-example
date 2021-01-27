package org.spring.springboot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Menu {
    private Integer id;
    private String authName;
    private String path;
    private List<Menu> children;
    public Menu(Integer id, String authName, String path) {
        this.id = id;
        this.authName = authName;
        this.path = path;
    }
}
