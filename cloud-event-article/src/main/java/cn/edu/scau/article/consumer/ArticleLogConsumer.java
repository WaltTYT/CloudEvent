package cn.edu.scau.article.consumer;

import cn.edu.scau.config.RabbitMqConfig;
import cn.edu.scau.pojo.Article;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 文章日志消费者
 * 该类作为 RabbitMQ 消息消费者，负责监听文章操作日志队列，
 * 当有新的文章操作（如创建、更新、删除等）消息时，
 * 会自动接收并处理这些消息，用于记录文章操作日志。
 */
@Component
public class ArticleLogConsumer {

    /**
     * 处理文章日志消息
     * 使用 @RabbitListener 注解监听指定的 RabbitMQ 队列，
     * 当队列中有消息时，Spring AMQP 会自动调用此方法进行处理。
     *
     * @param article 文章对象，包含文章的标题和ID等信息
     */
    @RabbitListener(queues = RabbitMqConfig.ARTICLE_LOG_QUEUE)
    public void handleArticleLog(Article article) {
        // 打印文章操作日志到控制台
        System.out.println("【日志】文章操作: " + article.getTitle() + ", ID: " + article.getId());
    }
}
