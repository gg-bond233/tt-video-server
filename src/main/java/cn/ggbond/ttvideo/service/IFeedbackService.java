package cn.ggbond.ttvideo.service;

import cn.ggbond.ttvideo.model.dto.FeedbackDTO;
import cn.ggbond.ttvideo.model.entity.Feedback;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IFeedbackService extends IService<Feedback> {

    // 获取反馈列表
    Result<PageResult<Feedback>> getAllFeedbaccks(FeedbackDTO feedbackDTO);

    //添加反馈
    Result<FeedbackDTO> addFeedback(String content);

    //删除反馈
    Result deleteFeedback(Long feedbackId);

    //批量删除反馈
    Result deleteFeedbacks(List<Long> feedbackIds);
}
