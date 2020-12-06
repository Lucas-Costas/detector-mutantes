package ar.com.magneto.resource

import ar.com.magneto.exception.Neo4jAdapterException
import ar.com.magneto.exception.RedisException
import ar.com.magneto.exception.StatsException
import ar.com.magneto.neo4j.Neo4jAdapter
import ar.com.magneto.redis.RedisAdapter
import ar.com.magneto.service.GenomeService
import ar.com.magneto.service.StatsService
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.StandardCharsets

import static ar.com.magneto.resource.AcceptanceTestUtils.GET
import static ar.com.magneto.resource.AcceptanceTestUtils.POST
import static ar.com.magneto.resource.AcceptanceTestUtils.getBody

@SpringBootTest
@AutoConfigureMockMvc
class StatsGetSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private StatsService statsService

    private RedisAdapter redisAdapter = Mock(RedisAdapter)

    def setup(){
        statsService.redisAdapter = redisAdapter
    }

    def "Devuelve HTTP OK - 200 si el ADN es mutante"(){
        when: "Realizo un POST para examinar un ADN"
            def getStatsResponse = GET(mockMvc,"/stats")
        then: "Consulto la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> 6
        and: "Consulto la cantidad de mutantes"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> 2
        and: "La respuesta es 200 - OK"
            getStatsResponse.status == HttpStatus.OK.value()
        and: "La respuesta indica la cantidad de humanos"
            getBody(getStatsResponse)["count_human_dna"] == 6
        and: "La respuesta indica la cantidad de mutantes"
            getBody(getStatsResponse)["count_mutant_dna"] == 2
        and: "La respuesta indica el ratio de mutantes"
            getBody(getStatsResponse)["ratio"] == 0.25
    }

    def "Devuelve HTTP INTERNAL SERVER ERROR - 500 si falla la consulta de la cantidad de humanos"(){
        when: "Realizo un POST para examinar un ADN"
            def getStatsResponse = GET(mockMvc,"/stats")
        then: "Falla al intentar consultar la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> {throw new RedisException("Oops")}
        and: "No consulto la cantidad de mutantes"
            0 * redisAdapter.getCounter("stats:count:dna:mutant")
        and: "La respuesta es 500 - INTERNAL SERVER ERROR"
            getStatsResponse.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "La respuesta indica los motivos del error"
            getBody(getStatsResponse)["message"] == "Ocurrio un error al obtener las estadisticas"
            getBody(getStatsResponse)["cause"] == "Fallo la consulta de la cantidad de humanos"
    }

    def "Devuelve HTTP INTERNAL SERVER ERROR - 500 si falla la consulta de la cantidad de mutantes"(){
        when: "Realizo un POST para examinar un ADN"
            def getStatsResponse = GET(mockMvc,"/stats")
        then: "Consulto la cantidad de humanos"
            1 * redisAdapter.getCounter("stats:count:dna:human") >> 6
        and: "Falla al intentar Consultar la cantidad de mutantes"
            1 * redisAdapter.getCounter("stats:count:dna:mutant") >> {throw new RedisException("Oops")}
        and: "La respuesta es 500 - INTERNAL SERVER ERROR"
            getStatsResponse.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "La respuesta indica los motivos del error"
            getBody(getStatsResponse)["message"] == "Ocurrio un error al obtener las estadisticas"
            getBody(getStatsResponse)["cause"] == "Fallo la consulta de la cantidad de mutantes"
    }

}
