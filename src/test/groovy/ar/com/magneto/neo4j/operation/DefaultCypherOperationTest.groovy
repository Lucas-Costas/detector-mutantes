package ar.com.magneto.neo4j.operation

import ar.com.magneto.dto.DnaDto
import ar.com.magneto.neo4j.operation.DefaultCypherOperation
import ar.com.magneto.neo4j.query.GenerateGenomeQuery
import ar.com.magneto.neo4j.query.NoReturnCypherQuery
import org.neo4j.driver.Transaction
import spock.lang.Specification

class DefaultCypherOperationTest extends Specification {

    def "Crea una DefaultCypherOperation a partir de una CypherQuery"(){
        given: ""
            NoReturnCypherQuery cQuery = aCypherQuery()
        when: ""
            DefaultCypherOperation operation = new DefaultCypherOperation(cQuery)
        then: ""
            operation.cypherQuery == cQuery
    }

    def "Puede ejecutar una query de cypher sin retorno"(){
        given: "Una Query de Cypher sin valor de retorno"
            NoReturnCypherQuery cQuery = aCypherQuery()
        and: "Una operación default de cypher"
            DefaultCypherOperation operation = new DefaultCypherOperation(cQuery)
        and: "Una transacción"
            Transaction tx = Mock(Transaction)
        when: "Ejecuto la operación"
            String result = operation.execute(tx)
        then: "Se llama al método run de la transaccion con los datos de la query"
            1 * tx.run(cQuery.query(),cQuery.parametros)
        and: "El resultado es 'OK'"
            result == "OK"
    }

    def aCypherQuery() {
        new GenerateGenomeQuery(new DnaDto(["ab", "cd"] as String[]))
    }

}
