package service;

import domain.Gen;
import domain.GenCoordinate;
import persistence.neo4j.Neo4jAdapter;
import persistence.neo4j.query.AddDiagonalRelationshipsQuery;
import persistence.neo4j.query.AddHorizontalRelationshipsQuery;
import persistence.neo4j.query.AddVerticalRelationshipsQuery;
import persistence.neo4j.query.BindIdQuery;
import persistence.neo4j.query.CountMutantSecuencesQuery;
import persistence.neo4j.query.CreateGenQuery;
import persistence.neo4j.query.CreateIdQuery;

public class GenomeService {

    private static final int FIRST_COLUMN_POSITION = 0;

    private Neo4jAdapter neo4jService = new Neo4jAdapter();

    public Boolean isMutant(String[] dna) {
        genereteIdNode(dna);
        Long firstNodeId = generateHorizontalSequences(dna);
        bindGenomeWithId(dna,firstNodeId);
        generateVerticalSequences(firstNodeId);
        generateDiagonalSequences(firstNodeId);
        return countMutantSequences() > 0;
    }

    private void bindGenomeWithId(String[] dna, Long firstNodeId) {
        String id = String.join("", dna);
        neo4jService.execute(new BindIdQuery(id));
    }

    private void genereteIdNode(String[] dna) {
        String id = String.join("", dna);
        neo4jService.execute(new CreateIdQuery(id));
    }

    public Long generateHorizontalSequences(String[] sequences) {
        Long firstNodeId = null;
        for (int j = 1; j <= sequences.length; j++) {
            String[] row = getRow(sequences, j);
            Long rowFirstNodeId = createRowFirstNode(j,row);
            firstNodeId = firstNodeId(firstNodeId, rowFirstNodeId);
            for (int i = 1; i < row.length; i++) {
                addHorizontalRelationship(i,j,row);
            }
        }
        return firstNodeId;
    }

    private Long firstNodeId(Long firstNodeId, Long rowFirstNodeId) {
        return firstNodeId == null ? rowFirstNodeId : firstNodeId;
    }

    public void generateVerticalSequences() {
        neo4jService.execute(new AddVerticalRelationshipsQuery());
    }

    public void generateDiagonalSequences() {
        neo4jService.execute(new AddDiagonalRelationshipsQuery());
    }

    public Integer countMutantSequences() {
        return neo4jService.executeWithIntegerResult(new CountMutantSecuencesQuery());
    }

    private Long createRowFirstNode(Integer verticalPosition, String[] sequence){
        GenCoordinate currentCoordinate = new GenCoordinate(1, verticalPosition);
        String value = sequence[FIRST_COLUMN_POSITION];
        return neo4jService.executeWithLongResult(new CreateGenQuery(new Gen(currentCoordinate,value)));
    }

    private void addHorizontalRelationship(Integer currentXIndex, Integer currentYIndex, String[] sequence){
        GenCoordinate currentCoordinate = new GenCoordinate(currentXIndex, currentYIndex);
        GenCoordinate nextCoordinate = currentCoordinate.nextCoordinateRight();
        String value = sequence[currentXIndex];
        neo4jService.execute( new AddHorizontalRelationshipsQuery(  currentCoordinate,
                                                                    new Gen(nextCoordinate,value)
        ));
    }

    private String[] getRow(String[] secuencias, Integer currentXIndex) {
        return secuencias[ currentXIndex - 1 ].split("");
    }

}
