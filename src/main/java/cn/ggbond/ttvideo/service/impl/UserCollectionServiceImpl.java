package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.mapper.UserCollectionMapper;
import cn.ggbond.ttvideo.model.entity.UserCollection;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IUserCollectionService;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "userCollectionCache")
@RequiredArgsConstructor
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements IUserCollectionService {
    private final UserCollectionMapper userCollectionMapper;
    /**
     * 收藏视频
     *
     * @param videoId 视频 ID
     * @return 结果
     */
    @Override
    public Result collect(Long videoId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId).eq("user_id", userId);
        if (userCollectionMapper.selectCount(wrapper)>0){
            return Result.error(MessageConstant.ADD+MessageConstant.FAILED);
        }

        UserCollection userCollection = new UserCollection();
        userCollection.setVideoId(videoId);
        userCollection.setUserId(userId);
        userCollection.setCreateTime(LocalDateTime.now());
        userCollectionMapper.insert(userCollection);

        return Result.success(MessageConstant.ADD+MessageConstant.SUCCESS);
    }


    /**
     * 取消收藏
     *
     * @param videoId 视频 ID
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = {"userCollectionCache", "videoCache"}, allEntries = true)
    public Result cancelCollect(Long videoId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId).eq("user_id", userId);
        if (userCollectionMapper.delete(wrapper)==0){
            return Result.error(MessageConstant.DELETE+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE+MessageConstant.SUCCESS);
    }


}
