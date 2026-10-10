package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.model.dto.*;
import cn.ggbond.ttvideo.model.vo.UserVO;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IUserService;
import cn.ggbond.ttvideo.util.BindingResultUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @return 结果
     */
    @GetMapping("/sendSecurityCode")
    public Result sendSecurityCode(@RequestParam @Email String email){
        return userService.sendSecurityCode(email);
    }

    /**
     * 注册
     *
     * @param userRegisterDTO 用户注册信息
     * @param bindingResult   绑定结果
     * @return 结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid UserRegisterDTO userRegisterDTO, BindingResult bindingResult){
        //校验输入信息是否正确,返回错误信息
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        //校验验证码是否正确
        boolean isSecurityCode=userService.verifySecurityCode(userRegisterDTO.getEmail(),userRegisterDTO.getSecurityCode());
        if(!isSecurityCode){
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }
        return userService.register(userRegisterDTO);
    }
    /**
     * 登录
     *
     * @param userLoginDTO  用户登录信息
     * @param bindingResult 绑定结果
     * @return 结果
     */
    @PostMapping("/login")
    public  Result login(@RequestBody @Valid UserLoginDTO userLoginDTO, BindingResult bindingResult){
        //校验输入信息
        String errorMessage =BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return userService.login(userLoginDTO);
    }

    /**
     * 获取用户信息
     *
     * @return 结果
     */
    @GetMapping("/getUserInfo")
    public Result<UserVO> getUserInfo() {
        return userService.userInfo();

    }
    /**
     * 更新用户信息
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody @Valid UserDTO userDTO , BindingResult bindingResult){
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if(errorMessage!=null){
            return Result.error(errorMessage);
        }
        return userService.updateUserInfo(userDTO);
    }
    /**
     * 更新用户密码
     *
     * @param userPasswordDTO 用户密码信息
     * @param token           认证token
     * @return 结果
     */
    @PatchMapping("/updateUserPassword")
    public Result updateUserPassword(@RequestBody @Valid UserPasswordDTO userPasswordDTO,
                                     @RequestHeader("Authorization") String token, BindingResult bindingResult) {
        // 校验失败时，返回错误信息
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        return userService.updateUserPassword(userPasswordDTO, token);
    }
    /**
     * 重置用户密码
     *
     * @param userResetPasswordDTO 用户密码信息
     * @return 结果
     */
    @PatchMapping("/resetUserPassword")
    public Result resetUserPassword(@RequestBody @Valid UserResetPasswordDTO userResetPasswordDTO, BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResult(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        //核实验证码是否正确
        if(!userService.verifySecurityCode(userResetPasswordDTO.getEmail(), userResetPasswordDTO.getSecurityCode())){
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }
        return userService.resetUserPassword(userResetPasswordDTO);
    }
    /**
     * 登出
     *
     * @param token 认证token
     * @return 结果
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        return userService.logout(token);
    }
}
