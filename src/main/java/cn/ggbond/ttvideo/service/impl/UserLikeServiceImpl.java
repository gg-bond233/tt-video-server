package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.mapper.UserLikeMapper;
import cn.ggbond.ttvideo.model.entity.UserLike;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IUserLikeService;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "userLikeCache")
@RequiredArgsConstructor
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements IUserLikeService {
    private final UserLikeMapper userLikeMapper;
    /**
     * 喜欢视频
     *
     * @param videoId 视频 ID
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = {"userLikeCache", "videoCache"}, allEntries = true)
    public Result like(Long videoId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);
        //查数据库
        QueryWrapper<UserLike> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId).eq("user_id", userId);
        if (userLikeMapper.selectCount(wrapper) > 0) {
            return Result.error(MessageConstant.ADD+MessageConstant.FAILED);
        }
        UserLike userLike = new UserLike();
        userLike.setVideoId(videoId);
        userLike.setUserId(userId);
        userLike.setCreateTime(LocalDateTime.now());
        userLikeMapper.insert(userLike);

        return Result.success(MessageConstant.ADD+MessageConstant.SUCCESS);
    }
    /**
     * 取消喜欢
     *
     * @param videoId 视频 ID
     * @return 结果
     */
    @Override
    public Result cancelLike(Long videoId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserLike> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId).eq("user_id", userId);
        if (userLikeMapper.delete(wrapper) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }
}
