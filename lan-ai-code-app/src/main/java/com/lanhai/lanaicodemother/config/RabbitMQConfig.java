package com.lanhai.lanaicodemother.config;

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
 * RabbitMQ 配置类
 *
 * @author hhzalh
 * @since 2026-01-26
 */
@Configuration
public class RabbitMQConfig {

    // ==================== 交换机 ====================

    /**
     * 截图生成交换机（Direct 类型）
     */
    @Bean
    public DirectExchange screenshotExchange() {
        return new DirectExchange("screenshot.exchange", true, false);
    }

    // ==================== 队列 ====================

    /**
     * 截图生成队列（任务队列）
     */
    @Bean
    public Queue screenshotQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "screenshot.dlq.exchange");
        args.put("x-dead-letter-routing-key", "dlq");
        args.put("x-message-ttl", 3600000); // 1 小时过期
        return new Queue("screenshot.queue", true, false, false, args);
    }

    /**
     * 截图结果队列（新增）
     * 用于 screenshot 服务向 app 服务返回截图结果
     */
    @Bean
    public Queue screenshotResultQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "screenshot.dlq.exchange");
        args.put("x-dead-letter-routing-key", "dlq");
        args.put("x-message-ttl", 7200000); // 2 小时过期
        return new Queue("screenshot.result.queue", true, false, false, args);
    }


    // ==================== 死信队列 ====================

    /**
     * 截图生成死信队列
     */
    @Bean
    public Queue screenshotDLQ() {
        return new Queue("screenshot.dlq", true, false, false);
    }


    // ==================== 绑定关系 ====================

    /**
     * 截图任务队列绑定
     */
    @Bean
    public Binding screenshotBinding() {
        return BindingBuilder
                .bind(screenshotQueue())
                .to(screenshotExchange())
                .with("screenshot.key");
    }

    /**
     * 截图结果队列绑定（新增）
     */
    @Bean
    public Binding screenshotResultBinding() {
        return BindingBuilder
                .bind(screenshotResultQueue())
                .to(screenshotExchange())
                .with("screenshot.result.key");
    }


    // ==================== 死信队列绑定 ====================

    @Bean
    public DirectExchange screenshotDLQExchange() {
        return new DirectExchange("screenshot.dlq.exchange");
    }

    @Bean
    public Binding screenshotDLQBinding() {
        return BindingBuilder
                .bind(screenshotDLQ())
                .to(screenshotDLQExchange())
                .with("dlq");
    }

    // ==================== 消息转换器 ====================

    /**
     * JSON 消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        // 开启发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("消息发送失败：" + cause);
            }
        });

        // 开启返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("消息无法路由：" + returned.getMessage());
        });

        return rabbitTemplate;
    }
}