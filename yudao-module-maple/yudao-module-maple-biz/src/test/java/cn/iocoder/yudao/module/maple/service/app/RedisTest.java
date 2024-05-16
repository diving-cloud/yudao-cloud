package cn.iocoder.yudao.module.maple.service.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        ValueOperations<String, String> ops = this.stringRedisTemplate.opsForValue();
        String key = "spring.boot.redis.test";
        String value = "Hello Redis!";

        // 删除可能存在的同名key
        if (this.stringRedisTemplate.hasKey(key)) {
            this.stringRedisTemplate.delete(key);
        }

        // 存储数据
        ops.set(key, value);

        // 获取数据
        String valueFromRedis = ops.get(key);
        System.out.println(valueFromRedis);

        // 测试数据
        assertEquals(value, valueFromRedis);
    }
}
