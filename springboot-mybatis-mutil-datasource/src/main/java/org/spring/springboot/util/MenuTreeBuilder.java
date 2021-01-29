package org.spring.springboot.util;


import org.spring.springboot.domain.Menu;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuTreeBuilder {
    public static List<Menu> build(List<Menu> nodes) {
        //根节点
        List<Menu> rootMenu = new ArrayList<>();
        for (Menu nav : nodes) {
            if (nav.getParentId() == 0) {
                rootMenu.add(nav);
            }
        }
        Collections.sort(rootMenu, Menu.order());
        for (Menu nav : rootMenu) {
            List<Menu> childList = getChild(nav.getId(), nodes);
            nav.setChildren(childList);//给根节点设置子节点
        }
        return rootMenu;
    }

    private static List<Menu> getChild(Integer id, List<Menu> nodes) {
        //子菜单
        List<Menu> childList = new ArrayList<Menu>();
        for (Menu nav : nodes) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            //相等说明：为该根节点的子节点。
            if (nav.getParentId().equals(id)) {
                childList.add(nav);
            }
        }
        //递归
        for (Menu nav : childList) {
            nav.setChildren(getChild(nav.getId(), nodes));
        }
        Collections.sort(childList, Menu.order());//排序
        //如果节点下没有子节点，返回一个空List（递归退出）
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

  /*  public static List<Menu> build(List<Menu> nodes){
        //根节点
        List<Menu> rootMenu = new ArrayList<>();
        for (Menu nav : nodes) {
            if(nav.getParentId().equals(0)){
                rootMenu.add(nav);
            }
        }
        *//* 根据Menu类的order排序 *//*
        Collections.sort(rootMenu,Menu.order());
        for (Menu nav : rootMenu) {
            setChildren(nav, nodes);
            for(Menu child : nav.getChildren()){
                setChildren(child, nodes);
                for(Menu childSub : child.getChildren()){
                    setChildren(childSub, nodes);
                    for(Menu childSub2 : childSub.getChildren()){
                        setChildren(childSub2, nodes);
                    }
                }
            }
        }
        return rootMenu;
    }

    private static void setChildren(Menu nav, List<Menu> nodes){
        List<Menu> children = new ArrayList<>();
        for(Menu tmp : nodes){
            if(tmp.getParentId().equals(nav.getId())){
                children.add(tmp);
            }
        }
        nav.setChildren(children);
    }*/


}
