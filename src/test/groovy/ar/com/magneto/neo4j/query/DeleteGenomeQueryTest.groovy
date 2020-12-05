package ar.com.magneto.neo4j.query

import ar.com.magneto.neo4j.operation.DefaultCypherOperation
import spock.lang.Specification

class DeleteGenomeQueryTest extends Specification{

    private static final String GENOME_ID = "abc,def,ghi"

    def "Retorna la query para contar las secuencias mutantes"(){
        given: "Una cQuery que elimina un genoma"
            DeleteGenomeQuery cQuery = aDeleteGenomeQuery()
        when: "Obtengo la query"
            String query = cQuery.query()
        then: "Es la esperada"
            query == "MATCH (b:Gen {genomeId: \$genomeId }) DETACH DELETE b"
    }

    def "Retorna los parametros de la query"(){
        given: "Una cQuery que elimina un genoma"
            DeleteGenomeQuery cQuery = aDeleteGenomeQuery()
        when: "Obtengo los parametros de la query"
            def parametros = cQuery.getParametros()
        then: "Estan vacios"
            parametros == [genomeId: GENOME_ID]
    }

    def "Puede transformarse a CypherOperation"(){
        given: "Una cQuery que elimina un genoma"
            DeleteGenomeQuery cQuery = aDeleteGenomeQuery()
        when: "Obtengo los parametros de la query"
            DefaultCypherOperation operation = cQuery.asOperation()
        then: "Estan vacios"
            operation.cypherQuery == cQuery
    }

    def aDeleteGenomeQuery() {
        new DeleteGenomeQuery(GENOME_ID)
    }

}
