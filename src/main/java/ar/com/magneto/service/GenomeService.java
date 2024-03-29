package ar.com.magneto.service;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.exception.GenomeException;
import ar.com.magneto.exception.InvalidDnaException;
import ar.com.magneto.exception.Neo4jAdapterException;
import ar.com.magneto.exception.RedisException;
import ar.com.magneto.exception.StatsException;
import ar.com.magneto.neo4j.Neo4jAdapter;
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery;
import ar.com.magneto.neo4j.query.DeleteGenomeQuery;
import ar.com.magneto.neo4j.query.GenerateGenomeQuery;
import ar.com.magneto.redis.RedisAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GenomeService {

    private static final String RECORD_GENOME_WARN = "No se pudo registrar el genoma {}";
    private static final String FIND_GENOME_WARN = "No se pudo obtener la caché del genoma {}";
    private static final String DELETE_GENOME_WARN = "Ha ocurrido un error al eliminar el genoma {}.";

    private static final String STATS_ERROR = "Ha ocurrido un error al registrar las estadísticas del ADN.";
    private static final String GENOME_EVALUATION_ERROR = "Ha ocurrido un error al evaluar el genoma";
    private static final String DNA_NOT_SQUARE_ERROR = "El ADN debe ser una matriz de N x N (cuadrada)";
    private static final String DNA_MALFORMED_ERROR = "El ADN debe estar compuesto por las bases A,T,C o G";
    private static final String DNA_NOT_EMPTY_ERROR = "El ADN no puede estar vacío";

    @Autowired
    private Neo4jAdapter neo4jAdapter;

    @Autowired
    private RedisAdapter redisAdapter;

    @Autowired
    private StatsService statsService;

    public Boolean isMutant(DnaDto dnaDto) {
        this.validateGenome(dnaDto);
        return this.findGenome(dnaDto)
                .orElseGet(()->this.evaluateGenome(dnaDto));
    }

    private void validateGenome(DnaDto dnaDto) {
        if(!dnaDto.hasContent()) throw new InvalidDnaException(DNA_NOT_EMPTY_ERROR);
        if(!dnaDto.hasRightBases()) throw new InvalidDnaException(DNA_MALFORMED_ERROR);
        if(!dnaDto.isSquare()) throw new InvalidDnaException(DNA_NOT_SQUARE_ERROR);
    }

    private Boolean evaluateGenome(DnaDto dnaDto) {
        generateGenome(dnaDto);
        Boolean isMutant = countMutantSequences(dnaDto.getIdGenome()) > 1;
        saveResult(dnaDto, isMutant);
        deleteGenomeAsynchronously(dnaDto.getIdGenome());
        return isMutant;
    }

    private void generateGenome(DnaDto dnaDto) {
        try {
            neo4jAdapter.execute(new GenerateGenomeQuery(dnaDto));
        } catch (Neo4jAdapterException ex){
            throw new GenomeException(GENOME_EVALUATION_ERROR,ex);
        }
    }

    private Integer countMutantSequences(String genomeId) {
        try {
            return neo4jAdapter.executeWithIntegerResult(new CountMutantSecuencesQuery(genomeId));
        } catch (Neo4jAdapterException ex){
            throw new GenomeException(GENOME_EVALUATION_ERROR,ex);
        }
    }

    private void deleteGenomeAsynchronously(String genomeId) {
        new Thread(()->this.deleteGenome(genomeId)).start();
    }

    private void deleteGenome(String genomeId){
        try {
            neo4jAdapter.execute(new DeleteGenomeQuery(genomeId));
        } catch (Neo4jAdapterException ex){
            log.warn(DELETE_GENOME_WARN,genomeId,ex);
        }
    }

    private void saveResult(DnaDto dnaDto, Boolean isMutant) {
        try {
            this.recordGenome(dnaDto,isMutant);
            statsService.registerStats(isMutant);
        } catch (StatsException ex){
            throw new GenomeException(STATS_ERROR,isMutant,ex);
        }
    }

    private void recordGenome(DnaDto dnaDto, Boolean isMutant) {
        String idGenome = dnaDto.getIdGenome();
        try {
            redisAdapter.setBoolean(idGenome, isMutant);
        } catch (RedisException ex){
            log.warn(RECORD_GENOME_WARN,idGenome,ex);
        }
    }

    private Optional<Boolean> findGenome(DnaDto dnaDto) {
        String idGenome = dnaDto.getIdGenome();
        try {
            return redisAdapter.getBooleanIfExists(idGenome);
        } catch (RedisException ex){
            log.warn(FIND_GENOME_WARN, idGenome,ex);
            return Optional.empty();
        }
    }

}