package ar.com.magneto.service

import ar.com.magneto.dto.DnaDto
import ar.com.magneto.exception.GenomeException
import ar.com.magneto.exception.Neo4jAdapterException
import ar.com.magneto.exception.RedisException
import ar.com.magneto.exception.StatsException
import ar.com.magneto.neo4j.Neo4jAdapter
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery
import ar.com.magneto.neo4j.query.DeleteGenomeQuery
import ar.com.magneto.neo4j.query.GenerateGenomeQuery
import ar.com.magneto.redis.RedisAdapter
import spock.lang.Specification
import spock.lang.Unroll

class GenomeServiceTest extends Specification {

    private GenomeService service = new GenomeService()

    private RedisAdapter redisAdapter = Mock(RedisAdapter)

    private StatsService statsService = Mock(StatsService)

    private Neo4jAdapter neo4jAdapter = Mock(Neo4jAdapter)

    def setup(){
        service.redisAdapter = redisAdapter
        service.neo4jAdapter = neo4jAdapter
        service.statsService = statsService
    }

    def "Si encuentra el resultado en REDIS no evalua el genoma y retorna el resultado cacheado"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean isMutant = service.isMutant(dnaDto)
        then: "Encuentro el genoma en REDIS"
            1 * redisAdapter.getBooleanIfExists(dnaDto.getIdGenome()) >> Optional.of(true)
        and: "No genera el genoma"
            0 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "No cuenta las secuencias mutantes y encuentra #sequencesNumber"
            0 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "No se registra el resultado en la cache REDIS"
            0 * redisAdapter.setBoolean(_,_)
        and: "No se actualizan las estadisticas"
            0 * statsService.registerStats(_)
        and: "No Se elimina el genoma"
            0 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "El ADN es mutante"
            isMutant
    }

    def "Si no encuentra el genoma en REDIS lo evalua"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean isMutant = service.isMutant(dnaDto)
            Thread.sleep(300)
        then: "Encuentro el genoma en REDIS"
            1 * redisAdapter.getBooleanIfExists(dnaDto.getIdGenome()) >> Optional.empty()
        then: "Genera el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "Cuenta las secuencias mutantes y encuentra 5"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> 5
        and: "Se registra el resultado en la cache REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),true)
        and: "Se actualizan las estadisticas"
            1 * statsService.registerStats(true)
        and: "Se elimina el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "El ADN es #caso"
            isMutant
    }

    @Unroll
    def "Evalua un ADN como #caso si tiene #sequencesNumber secuencias mutantes"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
            Thread.sleep(300)
        then: "Genera el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "Cuenta las secuencias mutantes y encuentra #sequencesNumber"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> sequencesNumber
        and: "Se registra el resultado en la cache REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),isMutant)
        and: "Se actualizan las estadisticas"
            1 * statsService.registerStats(isMutant)
        and: "Se elimina el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "El ADN es #caso"
            result == isMutant
        where:
            caso      | sequencesNumber | isMutant
            "humano"  | 0               | false
            "humano"  | 1               | false
            "mutante" | 2               | true
    }

    def "Si falla la generacion el genoma se interrumpe el proceso"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
        then: "Falla la generacion del genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "No consulta la cantidad de secuencias mutantes"
            0 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "No se registra el resultado en la cache REDIS"
            0 * redisAdapter.setBoolean(_,_)
        and: "No se actualizan las estadisticas"
            0 * statsService.registerStats(_)
        and: "No se elimina el genoma"
            0 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "Lanza una excepcion"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al evaluar el genoma"
    }

    def "Si falla el conteo de secuencias mutantes se interrumpe el proceso"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
        then: "Falla la generacion del genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "No consulta la cantidad de secuencias mutantes"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "No se registra el resultado en la cache REDIS"
            0 * redisAdapter.setBoolean(_,_)
        and: "No se actualizan las estadisticas"
            0 * statsService.registerStats(_)
        and: "No se elimina el genoma"
            0 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "Lanza una excepcion"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al evaluar el genoma"
    }

    @Unroll
    def "Si ocurre un error al guardar el resultado en REDIS el proceso continua"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
            Thread.sleep(300)
        then: "Genera el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "Cuenta las secuencias mutantes y encuentra #sequencesNumber"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> sequencesNumber
        and: "Ocurre un error al guardar el resultado en REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),isMutant) >> {throw new RedisException("Oops")}
        and: "Se actualizan las estadisticas"
            1 * statsService.registerStats(isMutant)
        and: "Se elimina el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "El ADN es #caso"
            result == isMutant
        where:
            caso      | sequencesNumber | isMutant
            "humano"  | 0               | false
            "humano"  | 1               | false
            "mutante" | 2               | true
    }

    @Unroll
    def "Si ocurre un error al actualizar las estadísticas falla el proceso. Secuencias encontradas #sequencesNumber"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
        then: "Genera el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "Cuenta las secuencias mutantes y encuentra #sequencesNumber"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> sequencesNumber
        and: "Se guarda el resultad en redis"
            1 * redisAdapter.setBoolean(_,_)
        and: "Ocurre un error al actualizar las estadisticas"
            1 * statsService.registerStats(_) >> {throw new StatsException("Oops")}
        and: "No se elimina el genoma"
            0 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
        and: "Se lanza una excepcion con un mensaje acorde"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al registrar las estadísticas del ADN. " + messageSuffix
        where:
            caso      | sequencesNumber | messageSuffix
            "humano"  | 0               | "El ADN era humano"
            "humano"  | 1               | "El ADN era humano"
            "mutante" | 2               | "El ADN era mutante"
    }

    @Unroll
    def "Si ocurre un error al eliminar el resultado en REDIS el proceso continua"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Evaluo si es mutante"
            Boolean result = service.evaluateGenome(dnaDto)
            Thread.sleep(300)
        then: "Genera el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                        genomeId: dnaDto.getIdGenome(),
                        genSize: dnaDto.getGenSize()
                ]
            })})
        and: "Cuenta las secuencias mutantes y encuentra #sequencesNumber"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> sequencesNumber
        and: "Guarda el resultado en REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),isMutant)
        and: "Se actualizan las estadisticas"
            1 * statsService.registerStats(isMutant)
        and: "Ocurre un error al eliminar el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "El ADN es #caso"
            result == isMutant
        where:
            caso      | sequencesNumber | isMutant
            "humano"  | 0               | false
            "humano"  | 1               | false
            "mutante" | 2               | true
    }

    def "Genera el genoma"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Genero el genoma"
            service.generateGenome(dnaDto)
        then: "Ejecuta la query para generarlo"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                    genomeId: dnaDto.getIdGenome(),
                    genSize: dnaDto.getGenSize()
                ]
            })})
    }

    def "Si falla al generar el genoma lanza una excepción"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Genero el genoma"
            service.generateGenome(dnaDto)
        then: "Falla la comunicación con Neo4j"
            1 * neo4jAdapter.execute({verifyAll(it, GenerateGenomeQuery,{
                it.parametros == [
                    genomeId: dnaDto.getIdGenome(),
                    genSize: dnaDto.getGenSize()
                ]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "Lanza una excepcion"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al evaluar el genoma"
    }

    def "Cuenta las secuencias mutantes de un ADN"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Cuento las secuencia que son mutantes"
            Integer result = service.countMutantSequences(dnaDto.getIdGenome())
        then: "Se ejecuta la query y retorna la cantidad de secuencias mutantes"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> 5
        and: "El resultado es 5"
            result == 5
    }

    def "Si falla la comunicacion con Neo4J al contar las secuencias mutantes arroja una excepcion"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Cuento las secuencia que son mutantes"
            Integer result = service.countMutantSequences(dnaDto.getIdGenome())
        then: "Se ejecuta la query y retorna la cantidad de secuencias mutantes"
            1 * neo4jAdapter.executeWithIntegerResult({verifyAll(it, CountMutantSecuencesQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "Lanza una excepcion"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al evaluar el genoma"
    }

    def "Elimina asincrónicamente el genoma"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Elimino asincronicamente el genoma"
            service.deleteGenomeAsynchronously(dnaDto.getIdGenome())
            Thread.sleep(300)
        then: "Se elimina el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
    }

    def "Si falla la eliminacion asincronica del genoma no se cancela el proceso"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Elimino asincrónicamente"
            service.deleteGenomeAsynchronously(dnaDto.getIdGenome())
            Thread.sleep(300)
        then: "Falla al eliminar el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "No se cancela el proceso"
            notThrown()
    }

    def "Elimina el genoma"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Elimino el genoma"
            service.deleteGenome(dnaDto.getIdGenome())
        then: "Se elimina el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })})
    }

    def "Si falla la eliminacion del genoma no se cancela el proceso"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Elimino el genoma"
            service.deleteGenome(dnaDto.getIdGenome())
        then: "Falla al eliminar el genoma"
            1 * neo4jAdapter.execute({verifyAll(it, DeleteGenomeQuery,{
                it.parametros == [genomeId: dnaDto.getIdGenome()]
            })}) >> {throw new Neo4jAdapterException("Oops")}
        and: "No se cancela el proceso"
            notThrown()
    }

    def "Registra el resultado de una evaluacion de ADN"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Registro el resultado de su evaluación"
            service.saveResult(dnaDto,true)
        then: "Se registra el resultado en la cache REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),true)
        and: "Se refleja el resultado en las estadísticas"
            1 * statsService.registerStats(true)
    }

    def "Si falla el registro en la cache no se cancela el proceso"(){
        given: "Un ADN evaluado"
            DnaDto dnaDto = aDnaDto()
        when: "Registro el resultado de su evaluación"
            service.saveResult(dnaDto,true)
        then: "Se registra el resultado en la cache REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),true) >> {throw new RedisException("Oops")}
        and: "Se refleja el resultado en las estadísticas"
            1 * statsService.registerStats(true)
     }

    @Unroll
    def "Si falla el registro de estadísticas se cancela el proceso. Caso #isMutant"(){
        given: "Un ADN evaluado"
           DnaDto dnaDto = aDnaDto()
        when: "Registro el resultado de su evaluación"
            service.saveResult(dnaDto,isMutant)
        then: "Se registra el resultado en la cache REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),isMutant)
        and: "Hay un error al guardar las estadísticas"
            1 * statsService.registerStats(isMutant) >> {throw new StatsException("Oops")}
        and: "Se lanza una excepcion con un mensaje acorde"
            GenomeException ex = thrown()
            ex.message == "Ha ocurrido un error al registrar las estadísticas del ADN. " + messageSuffix
        where:
            isMutant | messageSuffix
            true     | "El ADN era mutante"
            false    | "El ADN era humano"
    }

    def "Registra si un ADN es mutante"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Registro que es mutante"
            service.recordGenome(dnaDto,true)
        then: "Lo registra en REDIS usando el id del genoma"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),true)
    }

    def "Si falla la comunicación con REDIS no lanza una excepcion"(){
        given: "Un ADN"
            DnaDto dnaDto = aDnaDto()
        when: "Registro que es mutante"
            service.recordGenome(dnaDto,true)
        then: "Falla la comunicacion con REDIS"
            1 * redisAdapter.setBoolean(dnaDto.getIdGenome(),true) >> {throw new RedisException("Oops")}
        and: "Ninguna excepcion es lanzada"
            notThrown()
    }

    @Unroll
    def "Puede retornar el registro cacheado cuando existe y #scenario"(){
        given: "Un ADN a buscar"
            DnaDto dnaDto = aDnaDto()
        when: "Lo busco en la cache redis"
            def result = service.findGenome(dnaDto)
        then: "Consulto con REDIS y retorna un resultado no vacio"
            1 * redisAdapter.getBooleanIfExists(dnaDto.getIdGenome()) >> Optional.of(isMutant)
        and: "El reultado tiene contenido"
            result.isPresent()
        and: "El ADN #scenario"
            result.get() == isMutant
        where:
            scenario        | isMutant
            "es mutante"    | true
            "NO es mutante" | false
    }

    def "Si el ADN no estaba en REDIS retorna un optional vacio"() {
        given: "Un ADN a buscar"
            DnaDto dnaDto = aDnaDto()
        when: "Lo busco en la cache redis"
            def result = service.findGenome(dnaDto)
        then: "Consulto con REDIS y retorna un resultado vacio"
            1 * redisAdapter.getBooleanIfExists(dnaDto.getIdGenome()) >> Optional.empty()
        and: "El reultado NO tiene contenido"
            result.isEmpty()
    }

    def """Si falla la comunicación con REDIS al intentar obtener el registro cacheado
           retorna un optional vacio"""() {
        given: "Un ADN a buscar"
            DnaDto dnaDto = aDnaDto()
        when: "Lo busco en la cache redis"
            def result = service.findGenome(dnaDto)
        then: "Consulto con REDIS y lanza una excepcion"
            1 * redisAdapter.getBooleanIfExists(dnaDto.getIdGenome()) >> {throw new RedisException("Oops")}
        and: "El reultado NO tiene contenido"
            result.isEmpty()
    }

    def aDnaDto() {
        new DnaDto(["a", "b", "c"] as String[])
    }

}
