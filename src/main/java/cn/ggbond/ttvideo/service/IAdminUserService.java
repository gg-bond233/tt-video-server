package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.UserAddDTO;
import cn.ggbond.ttvideo.model.dto.UserDTO;
import cn.ggbond.ttvideo.model.dto.UserSearchDTO;
import cn.ggbond.ttvideo.model.vo.UserManagementVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;

import java.util.List;

public interface IAdminUserService {

    //获取所有用户数量
    Result<Long> getAllUsersCount();

    //分页查询所有用户
    Result<PageResult<UserManagementVO>> getAllUsers(UserSearchDTO userSearchDTO);

    //新增用户
    Result addUser(UserAddDTO userAddDTO);

    //修改用户信息
    Result updateUser(UserDTO userDTO);

    //更新用户状态
    Result updateUserStatus(Long userId, Integer userStatus);

    //删除用户
    Result deleteUser(Long userId);

    //批量删除用户
    Result deleteUsers(List<Long> userIds);
}
