package org.spring.springboot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.spring.springboot.vo.PaperBase;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu extends PaperBase {
    private Integer id;
    private Integer parentId;
    private String name;
    private String url;
    private Integer type;
    private Integer orderNum;
    private Date createtime;
    private String permissions;

    private List<Menu> children;
    private Integer level;
    /*
     * 排序,根据order排序
     */
    public static Comparator<Menu> order(){
        Comparator<Menu> comparator = (o1, o2) -> {
            if(o1.getOrderNum() != o2.getOrderNum()){
                return (int) (o1.getOrderNum() - o2.getOrderNum());
            }
            return 0;
        };
        return comparator;
    }

    public static Menu copyFromMenu(Menu menu){
        Menu vo = new Menu();
        vo.setId(menu.getId());
        vo.setName(menu.getName());
        vo.setCreatetime(menu.getCreatetime());
        vo.setLevel(menu.getLevel());
        vo.setOrderNum(menu.getOrderNum());
        vo.setParentId(menu.getParentId());
        vo.setPermissions(menu.getPermissions());
        vo.setType(menu.getType());
        vo.setUrl(menu.getUrl());
        return vo;
    }


}
