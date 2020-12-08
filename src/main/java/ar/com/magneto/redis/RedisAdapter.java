package ar.com.magneto.redis;

import ar.com.magneto.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Slf4j
@Component
public class RedisAdapter {

    private static final Long ZERO = 0L;

    private static final String SET_BOOLEAN_ERROR = "Ocurrió un error al setear el booleano con clave ";
    private static final String GET_BOOLEAN_ERROR = "Ocurrió un error al consultar el booleano con clave ";
    private static final String COUNTER_INCR_ERROR = "Ocurrió un error al incrementar el contador con clave ";
    private static final String GET_COUNTER_ERROR = "Ocurrió un error al obtener el valor del contador con clave ";

    Jedis jedis = new Jedis("10.0.0.11");

    public void setBoolean(String key, Boolean booleanValue) {
        try {
            jedis.set(key,booleanValue.toString());
        } catch (Exception ex){
            throw new RedisException(SET_BOOLEAN_ERROR,key,ex);
        }
    }

    public void incrementCounter(String key){
        try {
            jedis.incr(key);
        } catch (Exception ex){
            throw new RedisException(COUNTER_INCR_ERROR,key,ex);
        }
    }

    public Long getCounter(String key){
        try {
            Optional<String> counterValue = Optional.ofNullable(jedis.get(key));
            return counterValue.map(Long::valueOf).orElse(ZERO);
        } catch (Exception ex){
            throw new RedisException(GET_COUNTER_ERROR,key,ex);
        }
    }

    public Optional<Boolean> getBooleanIfExists(String key){
        try {
            Optional<String> value = Optional.ofNullable(jedis.get(key));
            return value.map(Boolean::valueOf);
        } catch (Exception ex){
            throw new RedisException(GET_BOOLEAN_ERROR,key,ex);
        }
    }
}
