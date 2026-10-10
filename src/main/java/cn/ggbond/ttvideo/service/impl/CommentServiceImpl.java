package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.mapper.CommentMapper;
import cn.ggbond.ttvideo.model.dto.CommentDTO;
import cn.ggbond.ttvideo.model.entity.Comment;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.ICommentService;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final CommentMapper commentMapper;

    /**
     * 添加视频评论
     *
     * @param commentDTO 视频评论DTO
     * @return Result
     */
    @Override
    @CacheEvict(cacheNames = "videoCache", allEntries = true)
    public Result addVideoComment(CommentDTO commentDTO) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Comment comment = new Comment();
        comment.setUserId(userId).setVideoId(commentDTO.getVideoId())
                .setParentId(commentDTO.getParentId())
                .setReplyToUserId(commentDTO.getReplyToUserId())
                .setContent(commentDTO.getContent())
                .setCreateTime(LocalDateTime.now()).setLikeCount(0L);

        if (commentMapper.insert(comment)==0){
            return Result.error(MessageConstant.ADD+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS);
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return Result
     */
    @Override
    @CacheEvict(cacheNames = "videoCache", allEntries = true)
    public Result likeComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment==null){
            Result.error(MessageConstant.NOT_FOUND);
        }
        if(comment.getLikeCount()==null){
            comment.setLikeCount(0L);
        }
        comment.setLikeCount(comment.getLikeCount()+1);

        if (commentMapper.updateById(comment)==0){
            return Result.error(MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.SUCCESS);
    }

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @return Result
     */
    @Override
    public Result cancelLikeComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment==null){
            Result.error(MessageConstant.NOT_FOUND);
        }
        if(comment.getLikeCount()==null){
            comment.setLikeCount(0L);
        }
        comment.setLikeCount(comment.getLikeCount()-1);
        if (commentMapper.updateById(comment)==0){
            return Result.error(MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.SUCCESS);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return Result
     */
    @Override
    @CacheEvict(cacheNames = "videoCache", allEntries = true)
    public Result deleteComment(Long commentId) {
       Map<String,Object> map = ThreadLocalUtil.get();
       Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
       Long userId = TypeConversionUtil.toLong(userIdObj);

        Comment comment = commentMapper.selectById(commentId);
        if (comment==null){
            Result.error(MessageConstant.NOT_FOUND);
        }

        //使用Objects.equals避免抛出NPE
        if (!Objects.equals(comment.getUserId(), userId)) {
            return Result.error(MessageConstant.NO_PERMISSION);
        }

        if(commentMapper.deleteById(commentId)==0){
            return Result.error(MessageConstant.FAILED);
        };
        return Result.success(MessageConstant.SUCCESS);
    }
}
