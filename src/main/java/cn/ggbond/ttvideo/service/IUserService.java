package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.*;
import cn.ggbond.ttvideo.model.entity.User;
import cn.ggbond.ttvideo.model.vo.UserVO;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {
    // 发送验证码
    Result sendSecurityCode(String email);

    // 验证验证码
    boolean verifySecurityCode(String email, String securityCode);

    // 用户注册
    Result register(UserRegisterDTO userRegisterDTO);

    //用户登录
    Result login(UserLoginDTO userLoginDTO);

    //获取用户信息
    Result<UserVO> userInfo();

    //更新用户信息
    Result updateUserInfo(UserDTO userDTO);

    //更新用户密码
    Result updateUserPassword(UserPasswordDTO userPasswordDTO, String token);

    //重置密码
    Result resetUserPassword(UserResetPasswordDTO userResetPasswordDTO);

    //退出登录
    Result logout(String token);
}
