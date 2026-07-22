package cn.edu.scau.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String ARTICLE_EXCHANGE = "article-exchange";
    public static final String ARTICLE_QUEUE = "article-queue";
    public static final String ARTICLE_LOG_QUEUE = "article-log-queue";
    public static final String ROUTING_KEY_PUBLISH = "article.publish";
    public static final String ROUTING_KEY_LOG = "article.log";

    public static final String FILE_EXCHANGE = "file-exchange";
    public static final String FILE_UPLOAD_QUEUE = "file-upload-queue";
    public static final String ROUTING_KEY_UPLOAD = "file.upload";

    public static final String USER_EXCHANGE = "user-exchange";
    public static final String USER_LOG_QUEUE = "user-log-queue";
    public static final String ROUTING_KEY_USER_LOG = "user.log";

    @Bean
    public DirectExchange articleExchange() {
        return new DirectExchange(ARTICLE_EXCHANGE);
    }

    @Bean
    public Queue articleQueue() {
        return new Queue(ARTICLE_QUEUE);
    }

    @Bean
    public Queue articleLogQueue() {
        return new Queue(ARTICLE_LOG_QUEUE);
    }

    @Bean
    public Binding articleBinding() {
        return BindingBuilder.bind(articleQueue()).to(articleExchange()).with(ROUTING_KEY_PUBLISH);
    }

    @Bean
    public Binding articleLogBinding() {
        return BindingBuilder.bind(articleLogQueue()).to(articleExchange()).with(ROUTING_KEY_LOG);
    }

    @Bean
    public DirectExchange fileExchange() {
        return new DirectExchange(FILE_EXCHANGE);
    }

    @Bean
    public Queue fileUploadQueue() {
        return new Queue(FILE_UPLOAD_QUEUE);
    }

    @Bean
    public Binding fileUploadBinding() {
        return BindingBuilder.bind(fileUploadQueue()).to(fileExchange()).with(ROUTING_KEY_UPLOAD);
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userLogQueue() {
        return new Queue(USER_LOG_QUEUE);
    }

    @Bean
    public Binding userLogBinding() {
        return BindingBuilder.bind(userLogQueue()).to(userExchange()).with(ROUTING_KEY_USER_LOG);
    }
}
