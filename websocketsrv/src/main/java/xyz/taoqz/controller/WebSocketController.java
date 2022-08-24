package xyz.taoqz.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.taoqz.config.IpLocalUtil;
import xyz.taoqz.config.IpUtil;
import xyz.taoqz.server.WebSocketServer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author taoqingzhou
 * @version 1.0
 * @date 2022/8/23 10:53
 */
@RequestMapping("/wsSrv/hello")
@RestController
public class WebSocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Value("${server.port}")
    private String port;

    @Value("${taoqz.name}")
    private String name;

    @GetMapping("/name")
    public String name(){
        return name;
    }

    @GetMapping("/p")
    public String port(){
        return port;
    }

    @GetMapping
    public String test(HttpServletRequest request, @RequestParam("host") String host, @RequestParam("msg") String msg) throws IOException {
        String ip = redisTemplate.opsForValue().get(host);
        if (IpLocalUtil.getLocalIp4Address().get().getHostAddress().equals(ip.split(":")[0])){
            webSocketServer.sendMessageByHost(host, msg);
        } else {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet("http://"+ip+"/wsSrv/hello?host="+host+"&msg="+msg);
            httpClient.execute(httpGet);
        }
        return "ok";
    }

}
