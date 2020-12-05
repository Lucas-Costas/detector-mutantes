package ar.com.magneto.neo4j.query

import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation
import spock.lang.Specification

class CountMutantSecuencesQueryTest extends Specification{

    def "Retorna la query para contar las secuencias mutantes"(){
        given: "Una cQuery que cuenta secuencias mutantes"
            CountMutantSecuencesQuery cQuery = new CountMutantSecuencesQuery()
        when: "Obtengo la query"
            String query = cQuery.query()
        then: "Es la esperada"
            query == "MATCH path = (a)-[*3..]->(b {base:a.base}) " +
                     "WHERE id(a) <> id(b) " +
                     "AND none(n IN nodes(path) WHERE n.base<>a.base) " +
                     "AND ( " +
                     " all(r IN relationships(path) WHERE type(r)=\"VERTICAL\") OR " +
                     " all(r IN relationships(path) WHERE type(r)=\"HORIZONTAL\") OR " +
                     " all(r IN relationships(path) WHERE type(r)=\"DIAGONAL\") " +
                     ") " +
                     "RETURN COUNT(path);"
    }

    def "Retorna los parametros de la query"(){
        given: "Una cQuery que cuenta secuencias mutantes"
            CountMutantSecuencesQuery cQuery = new CountMutantSecuencesQuery()
        when: "Obtengo los parametros de la query"
            def parametros = cQuery.getParametros()
        then: "Estan vacios"
            parametros.isEmpty()
    }

    def "Puede transformarse a CypherOperation"(){
        given: "Una cQuery que cuenta secuencias mutantes"
            CountMutantSecuencesQuery cQuery = new CountMutantSecuencesQuery()
        when: "Obtengo los parametros de la query"
            SingleIntegerChyperOperation operation = cQuery.asOperation()
        then: "Estan vacios"
            operation.cypherQuery == cQuery
    }

}
