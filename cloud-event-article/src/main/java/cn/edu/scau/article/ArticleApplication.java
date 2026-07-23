package cn.edu.scau.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文章服务启动类
 * 扫描 cn.edu.scau 包下的组件，启用服务注册发现和 Feign 远程调用
 */
@SpringBootApplication(scanBasePackages = "cn.edu.scau")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.scau.article.feign")
public class ArticleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleApplication.class, args);
    }
}
