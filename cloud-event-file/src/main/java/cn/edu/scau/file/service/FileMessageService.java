package cn.edu.scau.file.service;

import cn.edu.scau.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendFileUpload(String filename, String url) {
        Map<String, String> data = new HashMap<>();
        data.put("filename", filename);
        data.put("url", url);
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.FILE_EXCHANGE,
            RabbitMqConfig.ROUTING_KEY_UPLOAD,
            data
        );
    }
}
