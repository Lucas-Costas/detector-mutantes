package ar.com.magneto.redis

import ar.com.magneto.exception.RedisException
import redis.clients.jedis.Jedis
import spock.lang.Specification
import spock.lang.Unroll

class RedisAdapterTest extends Specification {

    private Jedis jedis = Mock(Jedis)

    private RedisAdapter adapter = new RedisAdapter()

    def setup(){
        adapter.jedis = jedis
    }

    def "Establece un boolean en una row"(){
        when: "Establecer true en 'key'"
            adapter.setBoolean("key",true)
        then: "Impacta 'true' en la clave 'key'"
            1 * jedis.set("key","true")
    }

    def "Si falla la conexion cuando setea un booleano lanza una excepcion"(){
        when: "Establecer true en 'key'"
            adapter.setBoolean("key",true)
        then: "Ocurre un error"
            1 * jedis.set("key","true") >> {throw new Exception("Oops")}
        and: "Lanza una excepcion"
            RedisException ex = thrown()
            ex.message == "Ocurrió un error al setear el booleano con clave 'key'"
            ex.cause.message == "Oops"
    }

    def "Incrementa un counter en una row"(){
        when: "Incremetar el counter en 'key'"
            adapter.incrementCounter("key")
        then: "Impacta 'true' en la clave 'key'"
            1 * jedis.incr("key")
    }

    def "Si falla la comunicación cuando incrementa un contador lanza una excepcion"(){
        when: "Incremetar el counter en 'key'"
            adapter.incrementCounter("key")
        then: "Ocurre un error"
            1 * jedis.incr("key") >> {throw new Exception("Oops")}
        and: "Lanza una excepcion"
            RedisException ex = thrown()
            ex.message == "Ocurrió un error al incrementar el contador con clave 'key'"
            ex.cause.message == "Oops"
    }

    @Unroll
    def "Consulta un counter en una row y retorna el #valor"(){
        when: "Consultar el counter en 'key'"
            Long result = adapter.getCounter("key")
        then: "Retorna '#counterContent'"
            1 * jedis.get("key") >> counterContent
        and: "El resultado es #value"
            result == value
        where:
            counterContent | value
            "2"            | 2L
            "0"            | 0L
            null           | 0L
    }

    def "Si ocurre un error de comunicacion cuando obtiene el valor del contador lanza una excepcion"(){
        when: "Consultar el counter en 'key'"
            Long result = adapter.getCounter("key")
        then: "Ocurre un error"
            1 * jedis.get("key") >> {throw new Exception("Oops")}
        and: "Lanza una excepcion"
            RedisException ex = thrown()
            ex.message == "Ocurrió un error al obtener el valor del contador con clave 'key'"
            ex.cause.message == "Oops"
    }

    @Unroll
    def "Consulta un boolean  en una row y si existe lo retora"(){
        when: "Consultar el boolean en 'key'"
            Optional<Boolean> result = adapter.getBooleanIfExists("key")
        then: "Retorna '#counterContent'"
            1 * jedis.get("key") >> booleanContent
        and: "El resultado es #value"
            result.get() == value
        where:
            booleanContent | value
            "true"         | true
            "false"        | false
    }

    def "Consulta un boolean  en una row y no existe. Retorna un optional vacio"() {
        when: "Consultar el boolean en 'key'"
            Optional<Boolean> result = adapter.getBooleanIfExists("key")
        then: "Retorna null porque la clave no existe"
            1 * jedis.get("key") >> null
        and: "El resultado es un Optional vaciio"
            result.isEmpty()
    }

    def "Si ocurre un error de comunicación cuando consulta un registro booleano lanza una excepcion"(){
        when: "Consultar el boolean en 'key'"
        Optional<Boolean> result = adapter.getBooleanIfExists("key")
        then: "Retorna '#counterContent'"
        1 * jedis.get("key")  >> {throw new Exception("Oops")}
        and: "Lanza una excepcion"
        RedisException ex = thrown()
        ex.message == "Ocurrió un error al consultar el booleano con clave 'key'"
        ex.cause.message == "Oops"
    }

}
