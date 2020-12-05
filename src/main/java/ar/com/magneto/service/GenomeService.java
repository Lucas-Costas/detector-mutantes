package ar.com.magneto.service;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.neo4j.Neo4jAdapter;
import ar.com.magneto.neo4j.operation.DefaultCypherOperation;
import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation;
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery;
import ar.com.magneto.neo4j.query.CypherQuery;
import ar.com.magneto.neo4j.query.DeleteGenomeQuery;
import ar.com.magneto.neo4j.query.GenerateGenomeQuery;
import ar.com.magneto.redis.RedisAdapter;
import org.neo4j.driver.TransactionWork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GenomeService {

    private Neo4jAdapter neo4jService = new Neo4jAdapter();

    private RedisAdapter redisAdapter = new RedisAdapter();

    private StatsService statsService = new StatsService();

    public Boolean isMutant(DnaDto dnaDto) {
        return this.findGenome(dnaDto)
                .orElseGet(()->this.evaluateGenome(dnaDto));
    }

    private Boolean evaluateGenome(DnaDto dnaDto) {
        generateGenome(dnaDto);
        Boolean isMutant = countMutantSequences() > 1;
        saveResult(dnaDto, isMutant);
        deleteGenome(dnaDto.getIdGenome());
        return isMutant;
    }

    private void generateGenome(DnaDto dnaDto) {
        neo4jService.execute(new GenerateGenomeQuery(dnaDto));
    }

    public Integer countMutantSequences() {
        return neo4jService.executeWithIntegerResult(new CountMutantSecuencesQuery());
    }

    private void deleteGenome(String genomeId) {
        new Thread(() -> neo4jService.execute(new DeleteGenomeQuery(genomeId))).start();
    }

    private void saveResult(DnaDto dnaDto, Boolean isMutant) {
        this.recordGenome(dnaDto,isMutant);
        statsService.registerStats(isMutant);
    }

    private void recordGenome(DnaDto dnaDto, Boolean isMutant) {
        redisAdapter.setBoolean(dnaDto.getIdGenome(), isMutant);
    }

    public Optional<Boolean> findGenome(DnaDto dnaDto) {
        return redisAdapter.getBooleanIfExists(dnaDto.getIdGenome());
    }

}