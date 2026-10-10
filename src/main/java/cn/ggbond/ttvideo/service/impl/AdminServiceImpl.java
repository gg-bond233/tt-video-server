package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.enumeration.RoleEnum;
import cn.ggbond.ttvideo.mapper.AdminMapper;
import cn.ggbond.ttvideo.model.dto.AdminDTO;
import cn.ggbond.ttvideo.model.entity.Admin;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IAdminService;
import cn.ggbond.ttvideo.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>  implements IAdminService {

    private final AdminMapper adminMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 管理员注册
     *
     * @param adminDTO 管理员信息
     * @return 结果
     */
    @Override
    public Result register(AdminDTO adminDTO) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", adminDTO.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin!=null){
            return Result.error(MessageConstant.USERNAME+MessageConstant.ALREADY_EXISTS);
        }
        //密码加密
        String password = DigestUtils.md5DigestAsHex(adminDTO.getPassword().getBytes());
        Admin adminRegister=new Admin();
        adminRegister.setUsername(adminDTO.getUsername());
        adminRegister.setPassword(password);
        //将管理员插入数据库
        if(adminMapper.insert(adminRegister)==0){
            return Result.error(MessageConstant.REGISTER+MessageConstant.FAILED);
        }
        return Result.error(MessageConstant.REGISTER+MessageConstant.SUCCESS);
    }

    /**
     * 管理员登录
     *
     * @param adminDTO 管理员信息
     * @return 结果
     */
    @Override
    public Result login(AdminDTO adminDTO) {
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", adminDTO.getUsername()));
        if(admin==null){
            return Result.error(MessageConstant.USERNAME+MessageConstant.ERROR);
        }
        //比对密码
        if(DigestUtils.md5DigestAsHex(adminDTO.getPassword().getBytes()).equals(admin.getPassword())){
            Map<String,Object> claims=new HashMap<>();
            claims.put(JwtClaimsConstant.ROLE, RoleEnum.ADMIN.getRole());
            claims.put(JwtClaimsConstant.ADMIN_ID,admin.getAdminId());
            claims.put(JwtClaimsConstant.USERNAME,admin.getUsername());
            //创建token
            String token= JwtUtil.generateToken(claims);
            //将token存入redis
            stringRedisTemplate.opsForValue().set(token, token, 6, TimeUnit.HOURS);

            return Result.success(MessageConstant.LOGIN + MessageConstant.SUCCESS, token);
        }
        return Result.error(MessageConstant.LOGIN+MessageConstant.FAILED);
    }

    /**
     * 登出
     *
     * @param token 认证token
     * @return 结果
     */
    @Override
    public Result logout(String token) {
        //注销token
        Boolean result = stringRedisTemplate.delete(token);
        if (result != null && result) {
            return Result.success(MessageConstant.LOGOUT + MessageConstant.SUCCESS);
        } else {
            return Result.error(MessageConstant.LOGOUT + MessageConstant.FAILED);
        }
    }
}
