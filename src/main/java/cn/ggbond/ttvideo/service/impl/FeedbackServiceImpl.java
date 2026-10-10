package cn.ggbond.ttvideo.service.impl;

import cn.ggbond.ttvideo.constant.JwtClaimsConstant;
import cn.ggbond.ttvideo.constant.MessageConstant;
import cn.ggbond.ttvideo.mapper.FeedbackMapper;
import cn.ggbond.ttvideo.model.dto.FeedbackDTO;
import cn.ggbond.ttvideo.model.entity.Feedback;
import cn.ggbond.ttvideo.result.PageResult;
import cn.ggbond.ttvideo.result.Result;
import cn.ggbond.ttvideo.service.IFeedbackService;
import cn.ggbond.ttvideo.util.ThreadLocalUtil;
import cn.ggbond.ttvideo.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "feedbackCache")
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

    private final FeedbackMapper feedbackMapper;

    /**
     * 分页查询反馈信息
     *
     * @param feedbackDTO 反馈信息查询条件
     * @return 反馈信息分页结果
     */
    @Override
    @Cacheable(key = "'getFeedbackList:' + #feedbackDTO.pageNum + ':' + #feedbackDTO.pageSize + ':' + #feedbackDTO.keyword")
    public Result<PageResult<Feedback>> getAllFeedbaccks(FeedbackDTO feedbackDTO) {
        //构建分页条件
        Page<Feedback> page = new Page<>(feedbackDTO.getPageNum(), feedbackDTO.getPageSize());
        //构建查询条件
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        if(feedbackDTO.getKeyword()!=null){
            wrapper.like("feedback",feedbackDTO.getKeyword());
        }
        wrapper.orderByDesc("create_time");
        //执行分页查询
        IPage<Feedback> feedbackPage =feedbackMapper.selectPage(page, wrapper);
        if (feedbackPage.getRecords().isEmpty()){
            return Result.success(MessageConstant.DATA_NOT_FOUND,new PageResult<>(0L,null));
        }
        return Result.success(new PageResult<>(feedbackPage.getTotal(), feedbackPage.getRecords()));
    }


    /**
     * 添加反馈信息
     *
     * @param content 反馈信息内容
     * @return 反馈信息添加结果
     */
    @Override
    @CacheEvict(cacheNames = "feedbackCache", allEntries = true)
    public Result<FeedbackDTO> addFeedback(String content) {
        Map<String,Object> map =ThreadLocalUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Feedback feedback = new Feedback();
        feedback.setUserId(userId).setFeedback(content).setCreateTime(LocalDateTime.now());

        if(feedbackMapper.insert(feedback)==0){
            return Result.error(MessageConstant.ADD+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD+MessageConstant.SUCCESS);
    }

    /**
     * 删除反馈信息
     *
     * @param feedbackId 反馈信息id
     * @return 反馈信息删除结果
     */
    @Override
    @CacheEvict(cacheNames = "feedbackCache", allEntries = true)
    public Result deleteFeedback(Long feedbackId) {
        if(feedbackMapper.deleteById(feedbackId)==0){
            return Result.error(MessageConstant.DELETE+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE+MessageConstant.SUCCESS);
    }

    /**
     * 批量删除反馈信息
     *
     * @param feedbackIds 反馈信息id列表
     * @return 反馈信息批量删除结果
     */
    @Override
    public Result deleteFeedbacks(List<Long> feedbackIds) {
        if(feedbackMapper.deleteByIds(feedbackIds)==0){
            return Result.error(MessageConstant.DELETE+MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE+MessageConstant.SUCCESS);
    }
}
