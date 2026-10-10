package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.CommentDTO;
import cn.ggbond.ttvideo.model.entity.Comment;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ICommentService extends IService<Comment> {

    //新增视频评论
    Result addVideoComment(CommentDTO commentDTO);

    //点赞视频评论
    Result likeComment(Long commentId);

    //取消点赞视频评论
    Result cancelLikeComment(Long commentId);

    //删除评论
    Result deleteComment(Long commentId);
}
