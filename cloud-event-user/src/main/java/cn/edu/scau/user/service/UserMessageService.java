package cn.edu.scau.user.service;

import cn.edu.scau.config.RabbitMqConfig;
import cn.edu.scau.pojo.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUserLog(User user, String operation) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.USER_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_USER_LOG,
            user
        );
    }
}
