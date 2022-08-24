package xyz.taoqz.server;

import com.alibaba.nacos.common.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.taoqz.config.ConfigValue;
import xyz.taoqz.config.IpLocalUtil;
import xyz.taoqz.config.IpUtil;
import xyz.taoqz.config.SpringUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author taoqz
 */
@ServerEndpoint(value = "/wsSrv/websocket")
@Component
@Slf4j
public class WebSocketServer {

    @PostConstruct
    public void init() {
    }


    private static RedisTemplate<String, String> redisTemplate;

    private static HttpServletRequest request;

    public  HttpServletRequest getRequest() {
        return request;
    }
    @Autowired
    public  void setRequest(HttpServletRequest request) {
        WebSocketServer.request = request;
    }

    public RedisTemplate<String, String> getTemplate() {
        return redisTemplate;
    }

    private static ConfigValue configValue;

    public ConfigValue getConfigValue() {
        return configValue;
    }

    @Autowired
    public void setConfigValue(ConfigValue configValue) {
        WebSocketServer.configValue = configValue;
    }

    @Autowired
    public void setTemplate(StringRedisTemplate template) {
        WebSocketServer.redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
     */
    private static final CopyOnWriteArraySet<Session> SESSION_SET = new CopyOnWriteArraySet<>();
    private static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws SocketException {
        String host = session.getRequestURI().getHost();
        //        SESSION_MAP.put("192.168.1.142", session);
        SESSION_MAP.put(host, session);
        SESSION_SET.add(session);
        // 在线数加1

        int cnt = ONLINE_COUNT.incrementAndGet();
        redisTemplate.opsForValue().set(host , IpLocalUtil.getLocalIp4Address().get().getHostAddress() + ":" + configValue.getPort());
        log.info("有连接加入，host：{}，当前连接数为：{}", host, cnt);
        sendMessage(session, "连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        SESSION_SET.remove(session);
        SESSION_MAP.remove(session.getRequestURI().getHost());
        redisTemplate.delete(session.getRequestURI().getHost());
        int cnt = ONLINE_COUNT.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：{}", message);
        sendMessage(session, "收到消息，消息内容：" + message);
    }


    /**
     * 出现错误
     *
     * @param session session 对象
     * @param error   错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session session 对象
     * @param message 消息
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessageByHost(String host, String message) {
        try {
            Session session = SESSION_MAP.get(host.replace("http://", ""));
            if (session.isOpen() ) {
                session.getBasicRemote().sendText(message);
            } else {
                // 转发
//                ip
                SESSION_MAP.remove(host);
                log.error("该session已关闭！");
            }
        } catch (Exception e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message 消息
     */
    public void broadCastInfo(String message) {
        for (Session session : SESSION_SET) {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }

    /**
     * 指定Session发送消息
     *
     * @param sessionId sessionId
     * @param message   message
     */
    public void sendMessage(String sessionId, String message) throws IOException {
        Session session = null;
        for (Session s : SESSION_SET) {
            if (s.getId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session != null) {
            sendMessage(session, message);
        } else {
            log.warn("没有找到你指定ID的会话：{}", sessionId);
        }
    }
}