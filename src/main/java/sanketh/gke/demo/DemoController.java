package sanketh.gke.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RestController
public class DemoController {
    private String name = null;

    private JedisPool jedisPool;

    @Autowired
    public DemoController(@Value("${redis.host}") String redisHost, @Value("${redis.port}") int redisPort) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, "localhost", 6379);
        System.out.println("JedisPool initialized with host: " + redisHost + " and port: " + redisPort);
    }

    @GetMapping("/")
    public String defaultWelcome() {
        return "Welcome to Sanketh Iyer Test GCP Project";
    }

    @GetMapping("/hello")
    public String getHello() {
        return name == null ? "Hello World" : "Hello " + name;
    }

    @PostMapping("/hello")
    public void postHello(@RequestBody Map<String, String> payload) {
        this.name = payload.get("name");
    }

    @GetMapping("/hello2")
    public String getHello2() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("myredis");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch value from Redis due to " + e.getMessage(), e);
        }
    }

    @PostMapping("/hello2")
    public void postHello2(@RequestBody Map<String, String> payload) {
        String value = payload.get("value");
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("myredis", value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set value in Redis due to " + e.getMessage(), e);
        }
    }
}