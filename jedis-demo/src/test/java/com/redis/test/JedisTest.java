package com.redis.test;

import com.redis.JedisConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class JedisTest {


    private Jedis jedis;

    @BeforeEach
    void setUp() {
        // jedis = new Jedis("192.168.10.17", 6379);
        // 使用连接池
        jedis = JedisConnectionFactory.getJedis();
        jedis.auth("thinker");
        jedis.select(0);
    }


    @Test
    void testJedis() {

        String result = jedis.set("name", "胡歌");
        System.out.println("result = " + result);
        String name = jedis.get("name");
        System.out.println("name = " + name);

    }

    @Test
    void testHash() {

        jedis.hset("user:1", "name", "Jack");
        jedis.hset("user:1", "age", "21");
    }

    @AfterEach
    void testDown() {
        if (jedis != null) {
            jedis.close();
        }
    }

}
