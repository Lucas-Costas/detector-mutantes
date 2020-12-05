package ar.com.magneto.neo4j

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

}
