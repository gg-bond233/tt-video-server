package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.UserAddDTO;
import cn.ggbond.ttvideo.model.dto.UserDTO;
import cn.ggbond.ttvideo.model.dto.UserSearchDTO;
import cn.ggbond.ttvideo.model.vo.UserManagementVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminUserService;
import cn.ggbond.ttvideo.util.BindingResultUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminUserController {
    private final IAdminUserService adminUserService;

    /**
     * 获取所有用户数量
     *
     * @return 用户数量
     */
    @GetMapping("/admin/getAllUsersCount")
    public Result<Long> getAllUsersCount() {
        return adminUserService.getAllUsersCount();
    }

    /**
     * 分页查询用户列表（管理端，支持按用户名 / 手机号 / 状态搜索）
     *
     * @param userSearchDTO 用户查询条件
     * @return 用户分页信息
     */
    @PostMapping("/admin/getAllUsers")
    public Result<PageResult<UserManagementVO>> getAllUsers(@RequestBody @Valid UserSearchDTO userSearchDTO, BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return adminUserService.getAllUsers(userSearchDTO);
    }

    /**
     * 新增用户
     *
     * @param userAddDTO    用户信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PostMapping("/admin/addUser")
    public Result addUser(@RequestBody @Valid UserAddDTO userAddDTO, BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return adminUserService.addUser(userAddDTO);
    }

    /**
     * 修改用户信息
     *
     * @param userDTO       用户信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PutMapping("/admin/updateUser")
    public Result updateUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return adminUserService.updateUser(userDTO);
    }

    /**
     * 更新用户状态（0-启用，1-禁用）
     *
     * @param userId     用户 id
     * @param userStatus 用户状态
     * @return 结果
     */
    @PatchMapping("/admin/updateUserStatus/{id}/{status}")
    public Result updateUserStatus(@PathVariable("id") Long userId, @PathVariable("status") Integer userStatus){
        return  adminUserService.updateUserStatus(userId,userStatus);
    }

    /**
     * 删除用户
     *
     * @param userId 用户 id
     * @return 结果
     */
    @DeleteMapping("/admin/deleteUser/{id}")
    public Result deleteUser(@PathVariable("id") Long userId) {
        return adminUserService.deleteUser(userId);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户 id 列表
     * @return 结果
     */
    @DeleteMapping("/admin/deleteUsers")
    public Result deleteUsers(@RequestBody List<Long> userIds) {
        return adminUserService.deleteUsers(userIds);
    }
}
