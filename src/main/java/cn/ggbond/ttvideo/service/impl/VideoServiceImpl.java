package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.enumeration.LikeStatusEnum;
import cn.ggbond.ttvideo.enumeration.RoleEnum;
import cn.ggbond.ttvideo.mapper.UserCollectionMapper;
import cn.ggbond.ttvideo.mapper.UserLikeMapper;
import cn.ggbond.ttvideo.mapper.VideoMapper;
import cn.ggbond.ttvideo.model.dto.VideoDTO;
import cn.ggbond.ttvideo.model.entity.UserCollection;
import cn.ggbond.ttvideo.model.entity.UserLike;
import cn.ggbond.ttvideo.model.entity.Video;
import cn.ggbond.ttvideo.model.vo.VideoDetailVO;
import cn.ggbond.ttvideo.model.vo.VideoVO;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IVideoService;
import cn.ggbond.ttvideo.util.JwtUtil;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "videoCache")
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    private final VideoMapper videoMapper;

    private final UserLikeMapper userLikeMapper;

    private final UserCollectionMapper userCollectionMapper;

    /**
     * 从请求头解析当前登录用户 ID（未登录或非用户角色返回 null）
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        //获取token
        String token = request.getHeader("Authorization");
        if(token!=null && token.startsWith("Bearer")){
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()){
            return null;
        }
        try {
            Map<String, Object> map = JwtUtil.parseToken(token);
            String role =(String) map.get(JwtClaimsConstant.ROLE);
            if (RoleEnum.USER.getRole().equals(role)){
                return TypeConversionUtil.toLong(map.get(JwtClaimsConstant.USER_ID));
            }
        }catch (Exception e){
            //token无效，视为未登录
        }
        return null;
    }
    /**
     * 为视频列表设置当前用户的喜欢/收藏状态
     */
    public void fillStatus(List<VideoVO> videoVOList, Long userId){
        if (userId == null){
            videoVOList.forEach(video ->{
                video.setLikeStatus(LikeStatusEnum.DEFAULT.getId());
                video.setCollectStatus(LikeStatusEnum.DEFAULT.getId());
            } );
            return;
        }
         Set<Long> likeIds = userLikeMapper.selectList(new QueryWrapper<UserLike>().eq("user_id", userId))
                 .stream().map(UserLike::getVideoId).collect(Collectors.toSet());
         Set<Long> collectionIds =userCollectionMapper.selectList(new QueryWrapper<UserCollection>().eq("user_id", userId))
                 .stream().map(UserCollection::getVideoId).collect(Collectors.toSet());
         videoVOList.forEach(video -> {
             video.setLikeStatus(likeIds.contains(video.getVideoId()) ? LikeStatusEnum.LIKE.getId() : LikeStatusEnum.DEFAULT.getId());
             video.setCollectStatus(collectionIds.contains(video.getVideoId()) ? LikeStatusEnum.LIKE.getId() : LikeStatusEnum.DEFAULT.getId());
         });
    }


    /**
     * 获取所有视频（支持按分类/关键词分页查询）
     */
    @Override
    @Cacheable(key = "#videoDTO.pageNum + '-' + #videoDTO.pageSize + '-' + #videoDTO.videoName + '-' + #videoDTO.category")
    public Result<PageResult<VideoVO>> getAllVideos(VideoDTO videoDTO, HttpServletRequest request) {
        //分页查询
        Page<VideoVO> page = new Page<>(videoDTO.getPageNum(), videoDTO.getPageSize());
        IPage<VideoVO> videoPage = videoMapper.getVideos(page, videoDTO.getVideoName(), videoDTO.getCategory());
        if(videoPage.getRecords().isEmpty()){
            return Result.success(MessageConstant.DATA_NOT_FOUND ,new PageResult<>(0L,null));
        }
        fillStatus(videoPage.getRecords(), getCurrentUserId(request));

        return Result.success(new PageResult<>(videoPage.getTotal(),videoPage.getRecords()));
    }

    /*
    * 获取推荐视频
    * */
    @Override
    public Result<List<VideoVO>> getRecommendedVideos(HttpServletRequest request) {
        List<VideoVO> videoVOList = videoMapper.getRandomVideos(20);
        fillStatus(videoVOList, getCurrentUserId(request));
        return Result.success(videoVOList);
    }

    /**
     * 按分类获取视频
     */
    @Override
    @Cacheable(key = "#videoDTO.pageNum + '-' + #videoDTO.pageSize + '-' + #videoDTO.videoName + '-' + #videoDTO.category")
    public Result<PageResult<VideoVO>> getVideoByCategory(VideoDTO videoDTO, HttpServletRequest request) {
        Page<VideoVO> page = new Page<>(videoDTO.getPageNum(),videoDTO.getPageSize());
        IPage<VideoVO> videoPage =videoMapper.getVideos(page,videoDTO.getVideoName(),videoDTO.getCategory());
        if(videoPage.getRecords().isEmpty()){
            return Result.success(MessageConstant.DATA_NOT_FOUND ,new PageResult<>(0L,null));
        }
        fillStatus(videoPage.getRecords(), getCurrentUserId(request));

        return Result.success(new PageResult<>(videoPage.getTotal(),videoPage.getRecords()));
    }

    /*
    * 获取视频详情（含评论列表及当前用户的喜欢/收藏状态）
    * */
    @Override
    public Result<VideoDetailVO> getVideoDetail(Long videoId, HttpServletRequest request) {
        VideoDetailVO videoDetailVO=videoMapper.getVideoDetailById(videoId);
        if (videoDetailVO == null){
            return Result.error(MessageConstant.DATA_NOT_FOUND);
        }
        Long userId = getCurrentUserId(request);
        if (userId != null){
            videoDetailVO.setLikeStatus(isLiked(userId,videoId)?LikeStatusEnum.LIKE.getId():LikeStatusEnum.DEFAULT.getId());
            videoDetailVO.setCollectStatus(isCollected(userId,videoId)?LikeStatusEnum.LIKE.getId():LikeStatusEnum.DEFAULT.getId());
        }else {
            videoDetailVO.setLikeStatus(LikeStatusEnum.DEFAULT.getId());
            videoDetailVO.setCollectStatus(LikeStatusEnum.DEFAULT.getId());
        }
        return Result.success(videoDetailVO);
    }

    /**
     * 获取当前用户喜欢的视频列表
     */
    @Override
    public Result<PageResult<VideoVO>> getLikedVideos(VideoDTO videoDTO) {
        Map<String,Object>map=ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        List<Long> likedVideoIds = userLikeMapper.selectList(new QueryWrapper<UserLike>()
                        .eq("user_id", userId).orderByDesc("create_time"))
                .stream().map(UserLike::getVideoId).toList();
        if (likedVideoIds.isEmpty()){
            return Result.success(MessageConstant.DATA_NOT_FOUND ,new PageResult<>(0L,null));
        }

        Page<VideoVO> page=new Page<>(videoDTO.getPageNum(), videoDTO.getPageSize());
        IPage<VideoVO> videoPage=videoMapper.getVideosByIds(page,likedVideoIds);

        Set<Long> collectedIds = userCollectionMapper.selectList(new QueryWrapper<UserCollection>().eq("user_id", userId))
                .stream().map(UserCollection::getVideoId).collect(Collectors.toSet());
        videoPage.getRecords().forEach(v -> {
            v.setLikeStatus(LikeStatusEnum.LIKE.getId());
            v.setCollectStatus(collectedIds.contains(v.getVideoId()) ? LikeStatusEnum.LIKE.getId() : LikeStatusEnum.DEFAULT.getId());
        });
        return Result.success(new PageResult<>(videoPage.getTotal(), videoPage.getRecords()));
    }

    /**
     * 获取当前用户收藏的视频列表
     */
    @Override
    public Result<PageResult<VideoVO>> getCollectedVideos(VideoDTO videoDTO) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        List<Long> collectedVideoIds = userCollectionMapper.selectList(new QueryWrapper<UserCollection>()
                        .eq("user_id", userId).orderByDesc("create_time"))
                .stream().map(UserCollection::getVideoId).toList();
        if (collectedVideoIds.isEmpty()) {
            return Result.success(new PageResult<>(0L, Collections.emptyList()));
        }

        Page<VideoVO> page = new Page<>(videoDTO.getPageNum(), videoDTO.getPageSize());
        IPage<VideoVO> videoPage = videoMapper.getVideosByIds(page, collectedVideoIds);

        // 收藏列表中 collectStatus 必为 1，likeStatus 根据喜欢表计算
        Set<Long> likedIds = userLikeMapper.selectList(new QueryWrapper<UserLike>().eq("user_id", userId))
                .stream().map(UserLike::getVideoId).collect(Collectors.toSet());
        videoPage.getRecords().forEach(v -> {
            v.setCollectStatus(LikeStatusEnum.LIKE.getId());
            v.setLikeStatus(likedIds.contains(v.getVideoId()) ? LikeStatusEnum.LIKE.getId() : LikeStatusEnum.DEFAULT.getId());
        });

        return Result.success(new PageResult<>(videoPage.getTotal(), videoPage.getRecords()));
    }


    //查询喜欢视频
    private boolean isLiked(Long userId, Long videoId) {
        return userLikeMapper.selectCount(new QueryWrapper<>(UserLike.class)
                .eq("user_id", userId).eq("video_id", videoId)) > 0;
    }

    //查询收藏视频
    private boolean isCollected(Long userId, Long videoId) {
        return userCollectionMapper.selectCount(new QueryWrapper<UserCollection>()
                .eq("user_id", userId).eq("video_id", videoId)) > 0;
    }

}
