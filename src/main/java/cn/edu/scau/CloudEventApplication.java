package cn.edu.scau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudEventApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(CloudEventApplication.class, args);
    }
}
