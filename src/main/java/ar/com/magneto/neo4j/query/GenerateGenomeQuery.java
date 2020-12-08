package ar.com.magneto.neo4j.query;

import ar.com.magneto.dto.DnaDto;

import java.util.HashMap;
import java.util.Map;

public class GenerateGenomeQuery implements NoReturnCypherQuery {

    private static final String GENOME_ID = "genomeId";
    private static final String GEN_SIZE = "genSize";

    private Map<String, Object> parametros = new HashMap<>();

    public GenerateGenomeQuery(DnaDto dnaDto) {
        parametros.put(GENOME_ID,dnaDto.getIdGenome());
        parametros.put(GEN_SIZE,dnaDto.getGenSize());
    }

    @Override
    public String query() {
        return "WITH $genomeId as genomeId " +
                "UNWIND RANGE(0, $genSize -1) as j " +
                "UNWIND RANGE(0, $genSize -1) as i " +
                "CREATE (a:Gen) SET a.indexV=j, a.indexH=i, a.base=split(split(genomeId,',')[j],'')[i], a.genomeId=genomeId " +
                "WITH genomeId " +
                "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV, indexH:c.indexH+1}) WHERE NOT (c)-[:HORIZONTAL]->(b) CREATE (c)-[r:HORIZONTAL]->(b) " +
                "WITH genomeId " +
                "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV+1, indexH:c.indexH}) WHERE NOT (c)-[:VERTICAL]->(b) CREATE (c)-[r:VERTICAL]->(b) " +
                "WITH genomeId " +
                "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV+1, indexH:c.indexH+1}) WHERE NOT (c)-[:DIAGONAL]->(b) CREATE (c)-[r:DIAGONAL]->(b) ";
    }

    @Override
    public Map<String, Object> getParametros() {
        return parametros;
    }
}
