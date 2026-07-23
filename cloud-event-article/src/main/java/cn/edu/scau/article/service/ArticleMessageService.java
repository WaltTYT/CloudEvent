package cn.edu.scau.article.service;

import cn.edu.scau.config.RabbitMqConfig;
import cn.edu.scau.pojo.Article;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文章消息服务
 * 通过 RabbitMQ 发送文章相关消息（发布通知、操作日志等）
 */
@Service
public class ArticleMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送文章发布消息
     * @param article 文章实体
     */
    public void sendArticlePublish(Article article) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.ARTICLE_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_PUBLISH,
            article
        );
    }

    /**
     * 发送文章操作日志消息
     * @param article 文章实体
     * @param operation 操作类型（如 delete, updateState）
     */
    public void sendArticleLog(Article article, String operation) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.ARTICLE_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_LOG,
            article
        );
    }
}
