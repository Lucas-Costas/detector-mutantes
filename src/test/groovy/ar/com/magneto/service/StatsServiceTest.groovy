    package ar.com.magneto.service

import ar.com.magneto.dto.StatsDto
import ar.com.magneto.exception.RedisException
import ar.com.magneto.exception.StatsException
import ar.com.magneto.exception.StatsUpdateException
import ar.com.magneto.redis.RedisAdapter
import spock.lang.Specification
import spock.lang.Unroll

class StatsServiceTest extends Specification {

    private StatsService service = new StatsService()

    private RedisAdapter redisAdapter = Mock(RedisAdapter)

    def setup(){
        service.redisAdapter = redisAdapter
    }

    def "Obtengo las estadisticas"(){
        when: "Obtengo las estadisticas"
            StatsDto statsDto = service.stats()
        then: "Consulto la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> 8L
        and: "Consulto la cantidad de mutantes"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> 2L
        and: "La respuesta es correcta"
            statsDto.countHumanDna == 8L
            statsDto.countMutantDna == 2L
            statsDto.ratio == 0.2
    }

    def "Lanzo una excepcion si falla la busqueda de la cantidad de humanos"(){
        when: "Obtengo las estadisticas"
            service.stats()
        then: "Falla la consulta la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> {throw new RedisException("Oops")}
        and: "No consulto la cantidad de mutantes"
            0 * redisAdapter.getCounter("stats:count:dna:mutant")
        and: "Se lanza una excepcion"
            StatsException ex = thrown()
            ex.message == "Fallo la consulta de la cantidad de humanos"
    }

    def "Lanzo una excepcion si falla la busqueda de la cantidad de mutantes"(){
        when: "Obtengo las estadisticas"
            StatsDto statsDto = service.stats()
        then: "Consulto la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> 4L
        and: "Falla la consulta la cantidad de mutantes"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> {throw new RedisException("Oops")}
        and: "Se lanza una excepcion"
            StatsException ex = thrown()
            ex.message == "Fallo la consulta de la cantidad de mutantes"
    }

    @Unroll
    def "Registra un resultado incrementando la clave #key"(){
        when: "Registro un resultado"
            service.registerStats(isMutant)
        then: "Incremento el registro #key"
            1 * redisAdapter.incrementCounter(key)
        where:
            isMutant | key
            false    | "stats:count:dna:human"
            true     | "stats:count:dna:mutant"
    }

    def "Lanza una excepcion si falla al registrar la estadistica"(){
        when: "Registro un resultado"
            service.registerStats(true)
        then: "Ocurre un error en REDIS"
            1 * redisAdapter.incrementCounter(_) >> {throw new RedisException("Oops")}
        and: "Lanzo una excepcion"
            StatsUpdateException ex = thrown()
            ex.message == "Fallo la actualizacion de las estaditicas"
    }

    def "Obtiene la cantidad de humanos"(){
        when: "Busco la cantidad de humanos"
            Long result = service.getHumanDnaCount()
        then: "Consulto stats:count:dna:human"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> 4L
        and: "El resultado es 4"
            result == 4L
    }

    def "Lanza una excepcion si falla la busqueda de la cantidad de humanos"(){
        when: "Busco la cantidad de humanos"
            service.getHumanDnaCount()
        then: "Ocurre un error en REDIS"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> {throw new RedisException("Oops")}
        and: "Lanzo una excepcion"
            StatsException ex = thrown()
            ex.message == "Fallo la consulta de la cantidad de humanos"
    }

    def "Obtiene la cantidad de mutantes"(){
        when: "Busco la cantidad de mutantes"
            Long result = service.getMutantDnaCount()
        then: "Consulto stats:count:dna:mutant"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> 4L
        and: "El resultado es 4"
            result == 4L
    }

    def "Lanza una excepcion si falla la busqueda de la cantidad de mutantes"(){
        when: "Busco la cantidad de mutantes"
            service.getMutantDnaCount()
        then: "Ocurre un error en REDIS"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> {throw new RedisException("Oops")}
        and: "Lanzo una excepcion"
            StatsException ex = thrown()
            ex.message == "Fallo la consulta de la cantidad de mutantes"
    }

}
