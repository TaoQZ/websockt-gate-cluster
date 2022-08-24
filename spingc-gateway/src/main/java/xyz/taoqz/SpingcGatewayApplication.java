package xyz.taoqz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpingcGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpingcGatewayApplication.class, args);
    }

}
