package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.AdminDTO;
import cn.ggbond.ttvideo.model.entity.Admin;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAdminService extends IService<Admin> {
    //注册管理员
    Result register(AdminDTO adminDTO);

    //管理员登录
    Result login(AdminDTO adminDTO);

    //退出登录
    Result logout(String token);
}
