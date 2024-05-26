package com.yupi.yuojbackendjudgeservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 题目提交消息队列配置类
 * @author tangx
 */
@Configuration
@Slf4j
public class QuestionSubmitRabbitMQConfig {

    // 定义队列名称
    private static final String CODE_QUEUE = "code_queue";
    private static final String DLX_CODE_QUEUE = "dlx_code_queue";

    // 定义交换机名称
    private static final String CODE_EXCHANGE = "code_exchange";
    private static final String DLX_CODE_EXCHANGE = "dlx_code_exchange";

    // 定义 routing key
    private static final String CODE_ROUTING_KEY = "code_routing_key";
    private static final String DLX_CODE_ROUTING_KEY = "dlx_code_routing_key";

    // 定义 codeQueue
    @Bean
    public Queue codeQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_CODE_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLX_CODE_ROUTING_KEY);
        return new Queue(CODE_QUEUE, true, false, false, args);
    }

    // 定义 dlxCodeQueue
    @Bean
    public Queue dlxCodeQueue() {
        return new Queue(DLX_CODE_QUEUE, true);
    }

    // 定义 codeExchange
    @Bean
    public DirectExchange codeExchange() {
        return new DirectExchange(CODE_EXCHANGE, true, false);
    }

    // 定义 dlxCodeExchange
    @Bean
    public DirectExchange dlxCodeExchange() {
        return new DirectExchange(DLX_CODE_EXCHANGE, true, false);
    }

    // 绑定 codeQueue 和 codeExchange
    @Bean
    public Binding codeQueueBinding() {
        return BindingBuilder.bind(codeQueue()).to(codeExchange()).with(CODE_ROUTING_KEY);
    }

    // 绑定 dlxCodeQueue 和 dlxCodeExchange
    @Bean
    public Binding dlxCodeQueueBinding() {
        return BindingBuilder.bind(dlxCodeQueue()).to(dlxCodeExchange()).with(DLX_CODE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);

        // ConfirmCallback 回调接口实现
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // 消息成功发送到 RabbitMQ
                log.info("Message sent successfully: {}", correlationData);
            } else {
                // 消息发送失败
                log.error("Message send failed: {}", cause);
            }
        });

        // 使用 setReturnsCallback 注册 ReturnsCallback
        rabbitTemplate.setReturnsCallback(returned -> {
            // 消息未能成功路由到任何队列
            log.error("Message returned: {}", new String(returned.getMessage().getBody()));
            log.error("Reply Code: {}", returned.getReplyCode());
            log.error("Reply Text: {}", returned.getReplyText());
            log.error("Exchange: {}", returned.getExchange());
            log.error("Routing Key: {}", returned.getRoutingKey());
        });

        return rabbitTemplate;
    }
}
