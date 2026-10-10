package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.enumeration.RoleEnum;
import cn.ggbond.ttvideo.enumeration.UserStatusEnum;
import cn.ggbond.ttvideo.mapper.UserMapper;
import cn.ggbond.ttvideo.model.dto.*;
import cn.ggbond.ttvideo.model.entity.User;
import cn.ggbond.ttvideo.model.vo.UserVO;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.EmailService;
import cn.ggbond.ttvideo.service.IUserService;
import cn.ggbond.ttvideo.util.JwtUtil;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "userCache")
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final EmailService emailService;

    private final StringRedisTemplate stringRedisTemplate;

    private final UserMapper userMapper;

    /**
     * 发送验证码,判断验证码，存储验证码
     *验证码生成等其它逻辑业务在eamilService实现
     * @param email 用户邮箱
     * @return 结果
     */
    @Override
    public Result sendSecurityCode(String email) {
        String securityCode=emailService.sendSecurityCodeEmail(email);
        if (securityCode==null){
            return Result.error(MessageConstant.EMAIL_SEND_FAILED);
        }
        stringRedisTemplate.opsForValue().set("securityCode:" + email, securityCode, 5, TimeUnit.MINUTES);
        return Result.success(MessageConstant.EMAIL_SEND_SUCCESS);
    }

    /**
     * 验证验证码
     *
     * @param email            用户邮箱
     * @param securityCode 验证码
     * @return 验证结果
     */
    @Override
    public boolean verifySecurityCode(String email, String securityCode) {
        String storedSecurityCode = stringRedisTemplate.opsForValue().get("securityCode:" + email);
        if (storedSecurityCode == null ) {
            return false;
        }
        return  storedSecurityCode.equals(securityCode);
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result register(UserRegisterDTO userRegisterDTO) {
        //删除存储在redis中的验证
        stringRedisTemplate.delete("verificationCode:" + userRegisterDTO.getEmail());
        //验证用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userRegisterDTO.getEmail());
        User userName = userMapper.selectOne(queryWrapper);
        if (userName!=null) {
            return Result.error(MessageConstant.USER + MessageConstant.ALREADY_EXISTS);
        }
        //验证邮箱是否存在
        User email = userMapper.selectOne(new QueryWrapper<User>().eq("email", userRegisterDTO.getEmail()));
        if (email!=null) {
            return Result.error(MessageConstant.EMAIL+MessageConstant.ALREADY_EXISTS);
        }
        //密码加密,创建用户
        String passwordMD5 = DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes());
        User user = new User();
        user.setUsername(userRegisterDTO.getEmail()).setPassword(passwordMD5).setEmail(userRegisterDTO.getEmail()).
                setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now()).setUserStatus(UserStatusEnum.ENABLE);
        //将用户插入数据库，返回信息
        if (userMapper.insert(user) == 0) {
            return Result.error(MessageConstant.REGISTER + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.REGISTER + MessageConstant.SUCCESS);
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 结果
     */
    @Override
    public Result login(UserLoginDTO userLoginDTO) {
        //根据邮箱查找用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", userLoginDTO.getEmail()));
        if (user==null) {
            return Result.error(MessageConstant.EMAIL+MessageConstant.ERROR);
        }
        if(user.getUserStatus()!=UserStatusEnum.ENABLE){
            return Result.error(MessageConstant.ACCOUNT_LOCKED);
        }
        //比对密码
        if (DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes()).equals(user.getPassword())) {
            // 生成JWT载荷
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.ROLE, RoleEnum.USER.getRole());
            claims.put(JwtClaimsConstant.USER_ID, user.getUserId());
            claims.put(JwtClaimsConstant.USERNAME, user.getUsername());
            claims.put(JwtClaimsConstant.EMAIL, user.getEmail());
            //生成token
            String token = JwtUtil.generateToken(claims);
            //将token存入redis
            stringRedisTemplate.opsForValue().set(token,token,6,TimeUnit.HOURS);
            return Result.success(MessageConstant.LOGIN+MessageConstant.SUCCESS,token);
        }
        return Result.error(MessageConstant.PASSWORD + MessageConstant.ERROR);
    }


    /**
     * 用户信息
     *
     * @return 结果
     */
    @Override
    public Result<UserVO> userInfo() {
       //从 ThreadLocal 拿当前用户身份（claims Map）
        Map<String,Object> map = ThreadLocalUtil.get();
        //从 Map 里取出 userId，转成 Long
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId= TypeConversionUtil.toLong(userIdObj);
        //按 userId 查数据库，拿到完整 User 实体
        User user = userMapper.selectById(userId);
        //把 User 实体转成 UserVO 返回（过滤敏感字段）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return Result.success(userVO);
    }


    /**
     * 更新用户信息
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result updateUserInfo(UserDTO userDTO) {
        //获取userid
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId= TypeConversionUtil.toLong(userIdObj);
        //根据userid,查询数据库原始用户信息
        User userName = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        if (userName!=null&&!userName.getUserId().equals(userId)) {
            return Result.error(MessageConstant.USER + MessageConstant.ALREADY_EXISTS);
        }
        User email = userMapper.selectOne(new QueryWrapper<User>().eq("email", userDTO.getEmail()));
        if(email!=null&&!email.getUserId().equals(userId)){
            return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
        }
        User phone = userMapper.selectOne(new QueryWrapper<User>().eq("phone", userDTO.getPhone()));
        if(phone!=null&&!phone.getUserId().equals(userId)){
            return Result.error(MessageConstant.PHONE + MessageConstant.ALREADY_EXISTS);
        }
        //属性拷贝
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        user.setUpdateTime(LocalDateTime.now());
        user.setUserId(userId);
        if (userMapper.updateById(user)==0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 更新用户密码
     *
     * @param userPasswordDTO 用户密码信息
     * @param token           认证token
     * @return 结果
     */
    @Override
    public Result updateUserPassword(UserPasswordDTO userPasswordDTO, String token) {
        //获取userid
        Map<String,Object>map=ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId= TypeConversionUtil.toLong(userIdObj);
        //根据userid查数据库
        User user = userMapper.selectById(userId);
        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(userPasswordDTO.getOldPassword().getBytes()))){
            return Result.error(MessageConstant.OLD_PASSWORD_ERROR);
        }
        if(user.getPassword().equals(DigestUtils.md5DigestAsHex(userPasswordDTO.getNewPassword().getBytes()))){
            return Result.error(MessageConstant.NEW_PASSWORD_ERROR);
        }
        if (!userPasswordDTO.getRepeatPassword().equals(userPasswordDTO.getNewPassword())){
            return Result.error(MessageConstant.PASSWORD_NOT_MATCH);
        }
        //设置新密码
        User user1=new User();
        user1.setPassword(DigestUtils.md5DigestAsHex(userPasswordDTO.getNewPassword().getBytes()));
        user1.setUpdateTime(LocalDateTime.now());
        user1.setUserId(userId);
        if (userMapper.updateById(user1)==0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        //注销token
        stringRedisTemplate.delete(token);
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 重置用户密码
     *
     * @param userResetPasswordDTO 用户密码信息
     * @return 结果
     */
    @Override
    public Result resetUserPassword(UserResetPasswordDTO userResetPasswordDTO) {
        //删除redis中存储的验证码
        stringRedisTemplate.delete("securityCode"+userResetPasswordDTO.getEmail());
        //根据邮箱查数据库
        User user=userMapper.selectOne(new QueryWrapper<User>().eq("email", userResetPasswordDTO.getEmail()));
        if(user==null){
            return Result.error(MessageConstant.EMAIL+MessageConstant.NOT_EXIST);
        }
        if (!userResetPasswordDTO.getRepeatPassword().equals(userResetPasswordDTO.getNewPassword())){
            return Result.error(MessageConstant.PASSWORD_NOT_MATCH);
        }
        Long userId = user.getUserId();
        User user1=new User();
        user1.setPassword(DigestUtils.md5DigestAsHex(userResetPasswordDTO.getNewPassword().getBytes()));
        user1.setUpdateTime(LocalDateTime.now());
        user1.setUserId(userId);

        if(userMapper.updateById(user1)==0){
            return Result.error(MessageConstant.PASSWORD+MessageConstant.RESET+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.PASSWORD+MessageConstant.RESET+MessageConstant.SUCCESS);
    }

    /**
     * 登出
     *
     * @param token 认证token
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = {"userCache", "userFavoriteCache", "songCache", "artistCache", "playlistCache"}, allEntries = true)
    public Result logout(String token) {
        // 注销token
        Boolean result = stringRedisTemplate.delete(token);
        if (result != null && result) {
            return Result.success(MessageConstant.LOGOUT + MessageConstant.SUCCESS);
        } else {
            return Result.error(MessageConstant.LOGOUT + MessageConstant.FAILED);
        }
    }

}
