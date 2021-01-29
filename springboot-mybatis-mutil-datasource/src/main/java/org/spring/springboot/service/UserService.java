package org.spring.springboot.service;

import org.spring.springboot.domain.Menu;
import org.spring.springboot.domain.Role;
import org.spring.springboot.domain.User;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.vo.*;

import java.util.List;

/**
 * 用户业务接口层
 *
 * Created by bysocket on 07/02/2017.
 */
public interface UserService {

    List<User> findUserByCondition(User req);
    String login(String username, String password) throws BusinessException;
    void addUser(User user) throws BusinessException;
    void changePwd(UserPw user) throws BusinessException;
    void changePwdBackend(UserPwBackend user) throws BusinessException;
    List<Menu> findMenuTree();
    List<Role> findRolesByUserId(Integer userId);
    List<Menu> findMenuTreeByRoleId(Integer roleId);
    List<Menu> findMenuRightsTree();
    List<Menu> findMenuByRoles(List<Role> roles);

    UserInfoVO info();

    PageVO<Menu> queryMenusByNameLike(Menu name);

    void deleteRoleRight(Integer roleId, Integer rightId) throws BusinessException;
    PageVO<User> queryUserLikeByCondition(User req);
    void insertRole(RoleVo req);

    List<RoleVo> listRole();

    void addRoleRights(Integer roleId, RoleRightsReq req) throws BusinessException;

    RoleVo queryRoleById(Integer roleId) throws BusinessException;

    void updateRole(RoleVo req);

    void deleteRole(Integer roleId);

    void changeUserState(Integer id, boolean state);

    User queryUserById(Integer id) throws BusinessException;
    List<KeyValue> findRoleMap();
    void updateUser(User user) throws BusinessException;

    void deleteUser(Integer id);
}
