package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.CommentDTO;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.Comment;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;
    /*
    * 新增评论
    * */
    @PostMapping("/addVideoComment")
    @CacheEvict(cacheNames = "videoCache", allEntries = true)
    public Result addVideoComment(@RequestBody @Valid CommentDTO commentDTO) {
        return commentService.addVideoComment(commentDTO);
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论id
     * @return 结果
     */
    @PatchMapping("/likeComment/{id}")
    public Result likeComment(@PathVariable("id") Long commentId) {
        return commentService.likeComment(commentId);
    }

    /**
     * 取消点赞评论
     *
     * @param commentId 评论id
     * @return 结果
     */
    @PatchMapping("/cancelLikeComment/{id}")
    public Result cancelLikeComment(@PathVariable("id") Long commentId) {
        return commentService.cancelLikeComment(commentId);
    }


    @DeleteMapping("/deleteComment/{id}")
    public Result deleteComment(@PathVariable("id") Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
