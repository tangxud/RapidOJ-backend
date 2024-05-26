package com.yupi.yuojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.exception.BusinessException;
import com.yupi.yuojbackendcommon.service.RedisService;
import com.yupi.yuojbackendjudgeservice.judge.JudgeService;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendmodel.model.rabbitmq.QuestionSubmitMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.yupi.yuojbackendcommon.constant.CacheConstants.MESSAGE_ID_PREFIX;

/**
 * @author tangx
 */
@Component
@Slf4j
public class MyMessageConsumer {
    
    @Resource
    private RedisService redisService;

    @Resource
    private JudgeService judgeService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(QuestionSubmitMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message.toString());
        long messageId = message.getMessageId();
        // 消息幂等性
        // 检查消息是否已处理,已经处理则返回
        if (redisService.hasKey(MESSAGE_ID_PREFIX+ messageId)) {
            log.info("Message with ID {} has already been processed.", messageId);
            channel.basicAck(deliveryTag, false);
            return;
        }
        long questionSubmitId = message.getQuestionSubmitId();
        try {
            QuestionSubmit questionSubmit = judgeService.doJudge(questionSubmitId);
            if (questionSubmit == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            redisService.setCacheObject(MESSAGE_ID_PREFIX + messageId, "", 2L, TimeUnit.HOURS);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            // 消息可重试
            channel.basicNack(deliveryTag, false, true);
        }
    }

}
