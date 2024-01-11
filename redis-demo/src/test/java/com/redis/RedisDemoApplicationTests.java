package com.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@SpringBootTest
class RedisDemoApplicationTests {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    void contextLoads() {

        /**
         * 这里接收的参数是对象类型，不管传入什么参数，最后都当作对象处理。当存入redis中时，这里默认使用的是JDK的序列化，
         */
        redisTemplate.opsForValue().set("name", "Amina");

        Object name = redisTemplate.opsForValue().get("name");

        System.out.println("name=" + name);

    }


    @Test
    void testSaveUser() {

        redisTemplate.opsForValue().set("user:100", new User("Jack", 10));

        User o = (User) redisTemplate.opsForValue().get("user:100");

        System.out.println("o = " + o);

    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testStringTemplate() throws JsonProcessingException {

        // 准备对象
        User user = new User("Jack", 18);
        // 手动序列化对象
        String userJson = mapper.writeValueAsString(user);

        stringRedisTemplate.opsForValue().set("user:200", userJson);

        // 读取数据
        String value = stringRedisTemplate.opsForValue().get("user:200");
        // 反序列化
        User user1 = mapper.readValue(value, User.class);
        System.out.println("user = " + user1);

    }

    @Test
    void testHash() {

        stringRedisTemplate.opsForHash().put("heima:user:5", "name", "胡歌");
        stringRedisTemplate.opsForHash().put("heima:user:5", "age", "18");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("heima:user:5");
        System.out.println("entries = " + entries);

    }

}
