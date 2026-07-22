package cn.edu.scau.article.service;

import cn.edu.scau.config.RabbitMqConfig;
import cn.edu.scau.pojo.Article;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendArticlePublish(Article article) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.ARTICLE_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_PUBLISH,
            article
        );
    }

    public void sendArticleLog(Article article, String operation) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.ARTICLE_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_LOG,
            article
        );
    }
}
