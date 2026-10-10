package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.AdminDTO;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminService;
import cn.ggbond.ttvideo.util.BindingResultUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    /**
     * 注册管理员
     *
     * @param adminDTO      管理员信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid AdminDTO adminDTO , BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return adminService.register(adminDTO);
    }

    /**
     * 登录管理员
     *
     * @param adminDTO      管理员信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid AdminDTO adminDTO , BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return adminService.login(adminDTO);
    }
    /**
     * 登出
     *
     * @param token 认证token
     * @return 结果
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        return adminService.logout(token);
    }
}
