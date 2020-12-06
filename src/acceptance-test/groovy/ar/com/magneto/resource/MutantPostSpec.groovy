package ar.com.magneto.resource

import ar.com.magneto.exception.Neo4jAdapterException
import ar.com.magneto.exception.StatsException
import ar.com.magneto.neo4j.Neo4jAdapter
import ar.com.magneto.redis.RedisAdapter
import ar.com.magneto.service.GenomeService
import ar.com.magneto.service.StatsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll

import static ar.com.magneto.resource.AcceptanceTestUtils.POST

@SpringBootTest
@AutoConfigureMockMvc
class MutantPostSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private GenomeService genomeService

    private RedisAdapter redisAdapter = Mock(RedisAdapter)

    private Neo4jAdapter neo4jAdapter = Mock(Neo4jAdapter)

    private StatsService statsService = Mock(StatsService)

    private String mutantDnaJson = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}"
    private String mutantGenomeId = "ATGCGA,CAGTGC,TTATGT,AGAAGG,CCCCTA,TCACTG"

    private String humanDnaJson = "{\"dna\":[\"ATGCAA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CTCCTA\",\"TCACTG\"]}"
    private String humanGenomeId = "ATGCAA,CAGTGC,TTATGT,AGAAGG,CTCCTA,TCACTG"

    private String notSquareDnaJson = "{\"dna\":[\"ATGCA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CTCCTA\",\"TCACTG\"]}"

    private String invalidBaseJson = "{\"dna\":[\"ATGCCA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CTCCTA\",\"TCACTD\"]}"

    private String emptyDnaJson = "{\"dna\":[]}"

    def setup(){
        genomeService.redisAdapter = redisAdapter
        genomeService.neo4jAdapter = neo4jAdapter
        genomeService.statsService = statsService
    }

    def "Devuelve HTTP OK - 200 si el ADN es mutante"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",mutantDnaJson)
        then: "En REDIS figura como mutante"
            1 * redisAdapter.getBooleanIfExists(mutantGenomeId) >> Optional.of(true)
        and: "La respuesta es 200 - OK"
            postMutantResponse.status == HttpStatus.OK.value()
    }

    def "Devuelve HTTP FORBIDDEN - 403 si el ADN es humano"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",humanDnaJson)
        then: "En REDIS figura como mutante"
            1 * redisAdapter.getBooleanIfExists(humanGenomeId) >> Optional.of(false)
        and: "La respuesta es 403 - FORBIDDEN"
            postMutantResponse.status == HttpStatus.FORBIDDEN.value()
    }

    def "Devuelve HTTP INTERNAL SERVER ERROR - 500 si ocurre una Neo4JException"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",humanDnaJson)
        then: "No está en REDIS"
            1 * redisAdapter.getBooleanIfExists(humanGenomeId) >> Optional.empty()
        and: "Ocurre un error al consultar a Neo4J"
            1 * neo4jAdapter.execute(_) >> {throw new Neo4jAdapterException("Oops")}
        and: "La respuesta es 500 - INTERNAL SERVER ERROR"
            postMutantResponse.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "El mensaje informa sobre el error"
        AcceptanceTestUtils.getBody(postMutantResponse)["message"] == "Ha ocurrido un error al evaluar el genoma"
        AcceptanceTestUtils.getBody(postMutantResponse)["cause"] == "Oops"
    }

    @Unroll
    def "Devuelve HTTP INTERNAL SERVER ERROR - 500 si ocurre una StatsException"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",humanDnaJson)
        then: "No se encuentra en REDIS"
            1 * redisAdapter.getBooleanIfExists(humanGenomeId) >> Optional.empty()
        and: "Genero el genoma"
            1 * neo4jAdapter.execute(_)
        and: "Cuento las secuencias mutantes"
            1 * neo4jAdapter.executeWithIntegerResult(_) >> sequencesNumber
        and: "Guardo el resultado en REDIS"
            1 * redisAdapter.setBoolean(_,_)
        and: "Guardo el resultado en REDIS"
            1 * statsService.registerStats(isMutant) >> {throw new StatsException("Oops")}
        and: "La respuesta es 500 - INTERNAL SERVER ERROR"
            postMutantResponse.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "El mensaje informa sobre el error"
        AcceptanceTestUtils.getBody(postMutantResponse)["message"] == message
        AcceptanceTestUtils.getBody(postMutantResponse)["cause"] == "Oops"
        where:
            sequencesNumber | isMutant | message
            5               | true     | "Ha ocurrido un error al registrar las estadísticas del ADN. El ADN era mutante"
            1               | false    | "Ha ocurrido un error al registrar las estadísticas del ADN. El ADN era humano"
    }

    def "Devuelve HTTP BAD_REQUESST - 400 si el ADN no es una matriz cuadrada"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",notSquareDnaJson)
        then: "La respuesta es 400 - BAD_REQUEST"
            postMutantResponse.status == HttpStatus.BAD_REQUEST.value()
        and: "El mensaje informa sobre el error"
        AcceptanceTestUtils.getBody(postMutantResponse)["message"] == "El ADN es inválido"
        AcceptanceTestUtils.getBody(postMutantResponse)["cause"] == "El ADN debe ser una matriz de N x N (cuadrada)"
    }

    def "Devuelve HTTP BAD_REQUESST - 400 si el ADN no tiene las bases correctas"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",invalidBaseJson)
        then: "La respuesta es 400 - BAD_REQUEST"
            postMutantResponse.status == HttpStatus.BAD_REQUEST.value()
        and: "El mensaje informa sobre el error"
        AcceptanceTestUtils.getBody(postMutantResponse)["message"] == "El ADN es inválido"
        AcceptanceTestUtils.getBody(postMutantResponse)["cause"] == "El ADN debe estar compuesto por las bases A,T,C o G"
    }

    def "Devuelve HTTP BAD_REQUESST - 400 si el ADN esta vacio"(){
        when: "Realizo un POST para examinar un ADN"
            def postMutantResponse = POST(mockMvc,"/mutant",emptyDnaJson)
        then: "La respuesta es 400 - BAD_REQUEST"
            postMutantResponse.status == HttpStatus.BAD_REQUEST.value()
        and: "El mensaje informa sobre el error"
        AcceptanceTestUtils.getBody(postMutantResponse)["message"] == "El ADN es inválido"
        AcceptanceTestUtils.getBody(postMutantResponse)["cause"] == "El ADN no puede estar vacío"
    }

}
