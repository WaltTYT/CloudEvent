package cn.edu.scau.article.consumer;

import cn.edu.scau.config.RabbitMqConfig;
import cn.edu.scau.pojo.Article;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleLogConsumer {

    @RabbitListener(queues = RabbitMqConfig.ARTICLE_LOG_QUEUE)
    public void handleArticleLog(Article article) {
        System.out.println("【日志】文章操作: " + article.getTitle() + ", ID: " + article.getId());
    }
}
