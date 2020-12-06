package ar.com.magneto.neo4j

import ar.com.magneto.exception.Neo4jAdapterException
import ar.com.magneto.neo4j.operation.DefaultCypherOperation
import ar.com.magneto.neo4j.query.CountMutantSecuencesQuery
import ar.com.magneto.neo4j.query.DeleteGenomeQuery
import org.neo4j.driver.Driver
import org.neo4j.driver.Session
import spock.lang.Specification

class Neo4jAdapterTest extends Specification {

    private Neo4jAdapter adapter = new Neo4jAdapter()

    private Driver driver = Mock(Driver)

    private Session session = Mock(Session)

    def setup(){
        adapter.driver = driver
    }

    def "Al cerrarse cierra la conexión cierro el driver"(){
        when: "Cierro el adapter"
            adapter.close()
        then: "Se cierra el driver con Neo4J"
            1 * driver.close()
    }

    def "Si ocurre un error al cerrar la conexión lanza una excepcion"(){
        when: "Cierro el adapter"
            adapter.close()
        then: "Se cierra el driver con Neo4J"
            1 * driver.close() >> {throw new Exception("Oops")}
        and: "Lanza una Neo4JException"
            Neo4jAdapterException ex = thrown()
            ex.message == "Ocurrio un error al cerrar a conexión con Neo4J"
    }

    def "Al ejecutar una transaccion invoca a la sesión"(){
        when: "Ejecuto una transaccion"
            adapter.execute(new DeleteGenomeQuery("genomeId"))
        then: "Se solicita una sesión"
            1 * driver.session() >> session
        and: "Se ejecuta la transacción"
            1 * session.writeTransaction(_)
    }

    def "Al ejecutar una transaccion con retorno devuelve un entero"(){
        when: "Ejecuto una transaccion"
            Integer result = adapter.executeWithIntegerResult(new CountMutantSecuencesQuery())
        then: "Se solicita una sesión"
            1 * driver.session() >> session
        and: "Se ejecuta la transacción"
            1 * session.writeTransaction(_) >> 1
        and: "El resultado es 1"
            result == 1
    }

    def "Si ocurre una excepcion al ejecutar la transaccion lanza una Neo4JAdapterException"(){
        when: "Ejecuto una transaccion"
            adapter.execWriteTransaction(aCypherOperation())
        then: "Se solicita una sesión"
            1 * driver.session() >> session
        and: "Ocurre un error al ejecutar la transaccion"
            1 * session.writeTransaction(_) >> {throw new RuntimeException("Oops")}
        and: "Lanza una excepcion"
            Neo4jAdapterException ex = thrown()
            ex.message == "Ocurrio un error al ejecutar la transacción en Neo4J"
            ex.cause.message == "Oops"
    }

    def "Si ocurre una excepcion al obtener la sesion lanza una Neo4JAdapterException"(){
        when: "Ejecuto una transaccion"
            adapter.execWriteTransaction(aCypherOperation())
        then: "Se solicita una sesión"
            1 * driver.session() >> {throw new RuntimeException("Oops")}
        and: "Ocurre un error al ejecutar la transaccion"
            0 * session.writeTransaction(_)
        and: "Lanza una excepcion"
            Neo4jAdapterException ex = thrown()
            ex.message == "Ocurrio un error al ejecutar la transacción en Neo4J"
            ex.cause.message == "Oops"
    }

    def aCypherOperation() {
        new DefaultCypherOperation(new DeleteGenomeQuery("genomeId"))
    }

}
