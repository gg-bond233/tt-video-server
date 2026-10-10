package cn.ggbond.ttvideo.controller;

import cn.ggbond.ttvideo.model.dto.FeedbackDTO;
import cn.ggbond.ttvideo.model.entity.Feedback;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedbackController {
    private final IFeedbackService feedbackService;

    /**
     * 获取反馈列表
     *
     * @return 反馈列表
     */
    @PostMapping("/admin/getAllFeedbacks")
    public Result<PageResult<Feedback>> getAllFeedbacks(@RequestBody FeedbackDTO feedbackDTO){
        return feedbackService.getAllFeedbaccks(feedbackDTO);
    }

    /**
     * 删除反馈
     *
     * @param feedbackId 反馈id
     * @return 结果
     */
    @DeleteMapping("/admin/deleteFeedback/{id}")
    public Result deleteFeedback(@PathVariable("id") Long feedbackId){
        return feedbackService.deleteFeedback(feedbackId);
    }

    /**
     * 批量删除反馈
     *
     * @param feedbackIds 反馈id列表
     * @return 结果
     */
    @DeleteMapping("/admin/deleteFeedbacks")
    public Result deleteFeedbacks(@RequestBody List<Long> feedbackIds){
        return feedbackService.deleteFeedbacks(feedbackIds);
    }

    /**
     * 添加反馈
     *
     * @param content 反馈内容
     * @return 结果
     */
    @PostMapping("/feedback/addFeedback")
    public Result<FeedbackDTO> addFeedback(@RequestParam(value = "content") String content){
        return feedbackService.addFeedback(content);
    }
}
