package cn.ggbond.ttvideo.service;


import cn.ggbond.ttvideo.model.entity.UserCollection;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户收藏服务类
 * </p>
 *
 * @author ttvideo
 * @since 2025-07-01
 */
public interface IUserCollectionService extends IService<UserCollection> {

    //收藏视频
    Result collect(Long videoId);

    //取消收藏视频
    Result cancelCollect(Long videoId);
}
