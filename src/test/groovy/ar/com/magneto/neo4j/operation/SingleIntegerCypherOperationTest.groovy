package ar.com.magneto.neo4j.operation

import ar.com.magneto.exception.CypherOperationException
import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery
import ar.com.magneto.neo4j.query.CypherQuery
import org.neo4j.driver.Record
import org.neo4j.driver.Result
import org.neo4j.driver.Transaction
import org.neo4j.driver.Value
import org.neo4j.driver.exceptions.NoSuchRecordException
import spock.lang.Specification

class SingleIntegerCypherOperationTest extends Specification {

    private Transaction tx = Mock(Transaction)

    private Result result = Mock(Result)

    private Record record = Mock(Record)

    private Value value = Mock(Value)

    def "Crea una DefaultCypherOperation a partir de una CypherQuery"(){
        given: "Una query que retonra un entero"
            CypherQuery<Integer> cQuery = new CountMutantSecuencesQuery()
        when: "Creo una Operación de Cypher"
            SingleIntegerChyperOperation operation = new SingleIntegerChyperOperation(cQuery)
        then: "Contiene a la query"
            operation.cypherQuery == cQuery
    }

    def "Puede ejecutar una query de cypher y retornar el resultado"(){
        given: "Una Query de Cypher que retorna un entero"
            CypherQuery<Integer> cQuery = new CountMutantSecuencesQuery()
        and: "Una operación que retorna un entero"
            SingleIntegerChyperOperation operation = new SingleIntegerChyperOperation(cQuery)
        when: "Ejecuto la operación"
            Integer opResult = operation.execute(tx)
        then: "Se llama al método run de la transaccion con los datos de la query"
            1 * tx.run(cQuery.query(),cQuery.parametros) >> result
        and: "Se llama al resultado buscando el valor"
            1 * result.single() >> record
        and: "Se llama al resultado buscando el valor"
            1 * record.get(0) >> value
        and: "Se consulta el entero que contiene"
            1 * value.asInt() >> 1
        and: "El resultado es 'OK'"
            opResult == 1
    }

    def "Falla si la consulta no retorna exáctamente un registro"(){
        given: "Una Query de Cypher que retorna un entero"
            CypherQuery<Integer> cQuery = new CountMutantSecuencesQuery()
        and: "Una operación que retorna un entero"
            SingleIntegerChyperOperation operation = new SingleIntegerChyperOperation(cQuery)
        when: "Ejecuto la operación"
            operation.execute(this.tx)
        then: "Se llama al método run de la transaccion con los datos de la query"
            1 * this.tx.run(cQuery.query(),cQuery.parametros) >> result
        and: "Se llama al resultado buscando el valor"
            1 * result.single() >> {throw new  NoSuchRecordException("Oops")}
        and: "Se lanza una excepción"
            CypherOperationException ex = thrown()
            ex.message == "La query no retornó exáctamente un registro como se esperaba"
    }


}
