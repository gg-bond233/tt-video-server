package cn.ggbond.ttvideo.service;


import cn.ggbond.ttvideo.model.entity.UserLike;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户喜欢服务类
 * </p>
 *
 * @author ttvideo
 * @since 2025-07-01
 */
public interface IUserLikeService extends IService<UserLike> {

    //喜欢视频
    Result like(Long videoId);

    //取消喜欢视频
    Result cancelLike(Long videoId);
}
