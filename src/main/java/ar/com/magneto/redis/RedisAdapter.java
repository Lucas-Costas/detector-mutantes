package ar.com.magneto.redis;

import redis.clients.jedis.Jedis;

import java.util.Optional;

public class RedisAdapter {

    private static final Long ZERO = 0L;

    Jedis jedis = new Jedis();

    public void setBoolean(String key, Boolean booleanValue) {
        jedis.set(key,booleanValue.toString());
    }

    public void incrementCounter(String key){
        jedis.incr(key);
    }

    public Long getCounter(String key){
        Optional<String> counterValue = Optional.ofNullable(jedis.get(key));
        return counterValue.map(Long::valueOf).orElse(ZERO);
    }

    public Optional<Boolean> getBooleanIfExists(String key){
        Optional<String> value = Optional.ofNullable(jedis.get(key));
        return value.map(Boolean::valueOf);
    }
}
