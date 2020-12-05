package ar.com.magneto.service;

import ar.com.magneto.domain.Gen;
import ar.com.magneto.domain.GenCoordinate;
import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.neo4j.Neo4jAdapter;
import ar.com.magneto.neo4j.query.AddDiagonalRelationshipsQuery;
import ar.com.magneto.neo4j.query.AddHorizontalRelationshipsQuery;
import ar.com.magneto.neo4j.query.AddVerticalRelationshipsQuery;
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery;
import ar.com.magneto.neo4j.query.CreateGenQuery;
import ar.com.magneto.neo4j.query.DeleteGenomeQuery;
import ar.com.magneto.neo4j.query.GenerateGenomeQuery;
import ar.com.magneto.performance.DurationLog;

import java.time.LocalDateTime;


public class GenomeService {

    private static final int FIRST_COLUMN_POSITION = 0;

    private Neo4jAdapter neo4jService = new Neo4jAdapter();

    DurationLog logger = new DurationLog();

    public Boolean isMutant(DnaDto dnaDto) {
        //generateHorizontalSequences(dnaDto);
        //generateVerticalSequences(dnaDto.getIdGenome());
        //generateDiagonalSequences(dnaDto.getIdGenome());
        generateGenome(dnaDto);
        Boolean isMutant = countMutantSequences() > 0;
        deleteGenome(dnaDto.getIdGenome());
        return isMutant;
    }

    private void generateGenome(DnaDto dnaDto) {
        LocalDateTime begin = LocalDateTime.now();
        neo4jService.execute(new GenerateGenomeQuery(dnaDto));
        logger.logDurationInMillisecs(begin,"generateGenome");
    }

    private void deleteGenome(String genomeId) {
        LocalDateTime begin = LocalDateTime.now();
        neo4jService.execute(new DeleteGenomeQuery(genomeId));
        logger.logDurationInMillisecs(begin,"deleteGenome");
    }

    public Long generateHorizontalSequences(DnaDto dnaDto) {
        LocalDateTime beginTime = LocalDateTime.now();
        Long firstNodeId = null;
        for (int j = 1; j <= dnaDto.getDna().length; j++) {
            String[] row = getRow(dnaDto.getDna(), j);
            createRowFirstNode(j,row, dnaDto.getIdGenome());
            for (int i = 1; i < row.length; i++) {
                addHorizontalRelationship(i,j,row, dnaDto.getIdGenome());
            }
        }
        logger.logDurationInMillisecs(beginTime,"horizontalsequences");
        return firstNodeId;
    }

    public void generateVerticalSequences(String genomeId) {
        LocalDateTime begin = LocalDateTime.now();
        neo4jService.execute(new AddVerticalRelationshipsQuery(genomeId));
        logger.logDurationInMillisecs(begin,"generateVerticalSequences");
    }

    public void generateDiagonalSequences(String genomeId) {
        LocalDateTime begin = LocalDateTime.now();
        neo4jService.execute(new AddDiagonalRelationshipsQuery(genomeId));
        logger.logDurationInMillisecs(begin,"generateDiagonalSequences");
    }

    public Integer countMutantSequences() {
        LocalDateTime begin = LocalDateTime.now();
        Integer result = neo4jService.executeWithIntegerResult(new CountMutantSecuencesQuery());
        logger.logDurationInMillisecs(begin,"countMutantSequences");
        return result;
    }

    private void createRowFirstNode(Integer verticalPosition, String[] sequence, String genomeId){
        LocalDateTime begin = LocalDateTime.now();
        GenCoordinate currentCoordinate = new GenCoordinate(1, verticalPosition);
        String value = sequence[FIRST_COLUMN_POSITION];
        neo4jService.execute(new CreateGenQuery(new Gen(currentCoordinate,value,genomeId)));
        logger.logDurationInMillisecs(begin,"createRowFirstNode");
    }

    private void addHorizontalRelationship(Integer currentXIndex, Integer currentYIndex, String[] sequence, String genomeId){
        LocalDateTime begin = LocalDateTime.now();
        GenCoordinate currentCoordinate = new GenCoordinate(currentXIndex, currentYIndex);
        GenCoordinate nextCoordinate = currentCoordinate.nextCoordinateRight();
        String value = sequence[currentXIndex];
        neo4jService.execute( new AddHorizontalRelationshipsQuery(  currentCoordinate,
                                                                    new Gen(nextCoordinate,value,genomeId)
        ));
        logger.logDurationInMillisecs(begin,"addHorizontalRel");
    }

    private String[] getRow(String[] secuencias, Integer currentXIndex) {
        return secuencias[ currentXIndex - 1 ].split("");
    }

}