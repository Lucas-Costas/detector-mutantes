package ar.com.magneto.service;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.neo4j.Neo4jAdapter;
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery;
import ar.com.magneto.neo4j.query.DeleteGenomeQuery;
import ar.com.magneto.neo4j.query.GenerateGenomeQuery;

public class GenomeService {

    private Neo4jAdapter neo4jService = new Neo4jAdapter();

    public Boolean isMutant(DnaDto dnaDto) {
        generateGenome(dnaDto);
        Boolean isMutant = countMutantSequences() > 0;
        deleteGenome(dnaDto.getIdGenome());
        return isMutant;
    }

    private void generateGenome(DnaDto dnaDto) {
        neo4jService.execute(new GenerateGenomeQuery(dnaDto));
    }

    private void deleteGenome(String genomeId) {
        neo4jService.execute(new DeleteGenomeQuery(genomeId));
    }

    public Integer countMutantSequences() {
        return neo4jService.executeWithIntegerResult(new CountMutantSecuencesQuery());
    }

}