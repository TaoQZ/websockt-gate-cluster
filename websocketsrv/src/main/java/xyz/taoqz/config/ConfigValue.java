package xyz.taoqz.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConfigValue {
 
    @Value("${server.port}")
    private String port;
}