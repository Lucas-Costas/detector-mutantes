package ar.com.magneto.neo4j.query

import ar.com.magneto.dto.DnaDto
import ar.com.magneto.neo4j.operation.DefaultCypherOperation
import spock.lang.Specification

class GenerateGenomeQueryTest extends Specification{

    private static final DnaDto DNA_DTO = new DnaDto(["abc", "def", "ghi"] as String[])

    def "Retorna la query para contar las secuencias mutantes"(){
        given: "Una cQuery que crea un genoma"
            GenerateGenomeQuery cQuery = aGenerateGenomeQuery()
        when: "Obtengo la query"
            String query = cQuery.query()
        then: "Es la esperada"
            query == "WITH \$genomeId as genomeId " +
                    "MATCH (a:Gen {genomeId:genomeId}) DETACH DELETE a " +
                    "WITH genomeId " +
                    "UNWIND RANGE(0, \$genSize -1) as j " +
                    "UNWIND RANGE(0, \$genSize -1) as i " +
                    "CREATE (a:Gen) SET a.indexV=j, a.indexH=i, a.base=split(split(genomeId,',')[j],'')[i], a.genomeId=genomeId " +
                    "WITH genomeId " +
                    "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV, indexH:c.indexH+1}) WHERE NOT (c)-[:HORIZONTAL]->(b) CREATE (c)-[r:HORIZONTAL]->(b) " +
                    "WITH genomeId " +
                    "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV+1, indexH:c.indexH}) WHERE NOT (c)-[:VERTICAL]->(b) CREATE (c)-[r:VERTICAL]->(b) " +
                    "WITH genomeId " +
                    "MATCH (c:Gen {genomeId:genomeId}),(b:Gen {genomeId:c.genomeId, indexV:c.indexV+1, indexH:c.indexH+1}) WHERE NOT (c)-[:DIAGONAL]->(b) CREATE (c)-[r:DIAGONAL]->(b) "
    }

    def "Retorna los parametros de la query"(){
        given: "Una cQuery que crea un genoma"
            GenerateGenomeQuery cQuery = aGenerateGenomeQuery()
        when: "Obtengo los parametros de la query"
            def parametros = cQuery.getParametros()
        then: "Estan vacios"
            parametros == [genomeId: "abc,def,ghi", genSize: 3]
    }

    def "Puede transformarse a CypherOperation"(){
        given: "Una cQuery que crea un genoma"
            GenerateGenomeQuery cQuery = aGenerateGenomeQuery()
        when: "Obtengo los parametros de la query"
            DefaultCypherOperation operation = cQuery.asOperation()
        then: "Estan vacios"
            operation.cypherQuery == cQuery
    }

    def aGenerateGenomeQuery() {
        new GenerateGenomeQuery(DNA_DTO)
    }

}
