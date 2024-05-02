package com.yupi.yuojbackendserviceclient.factory;

import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendserviceclient.service.QuestionFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteQuestionFallbackFactory implements FallbackFactory<QuestionFeignClient>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteQuestionFallbackFactory.class);

    @Override
    public QuestionFeignClient create(Throwable throwable)
    {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return new QuestionFeignClient()
        {
            @Override
            public Question getQuestionById(@RequestParam("questionId") long questionId) {
                return null;
            };

            @Override
            public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
                return null;
            };

            @Override
            public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
                return false;
            }
        };
    }
}
