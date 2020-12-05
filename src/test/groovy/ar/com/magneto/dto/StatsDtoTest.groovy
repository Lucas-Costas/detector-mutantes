package ar.com.magneto.dto

import spock.lang.Specification
import spock.lang.Unroll

class StatsDtoTest extends Specification {

    def "Puede construir un StatsDto a partir del número de humanos y mutantes"(){
        given: "Se registro 1 humano"
            Long humanCount = 1L
        and: "Se registraron dos mutantes"
            Long mutantCount = 2L
        when: "Creo las estadísticas a apartir de las cantidades de humanos y mutantes"
            StatsDto statsDto = new StatsDto(humanCount,mutantCount)
        then: "La cantidad de humanos es 1"
            statsDto.countHumanDna == 1L
        and: "La cantidad de mutantes  es 2"
            statsDto.countMutantDna == 2L
    }

    @Unroll
    def """Puede calcular la cantidad total de casos.
           Cuando la cantidad de humanos es #humanCount
           Y la cantidad de mutantes es #mutantCount"""(){
        given: "Estadísticas a partir de las cantidades de humanos y mutantes"
            StatsDto statsDto = new StatsDto(humanCount,mutantCount)
        when: "Calculo el total de casos"
            Long total = statsDto.total()
        then: "El total es #totalEsperado"
            total == totalEsperado
        where:
            humanCount | mutantCount | totalEsperado
            0L         | 0L          | 0L
            1L         | 0L          | 1L
            0L         | 1L          | 1L
            2L         | 3L          | 5L
    }

    @Unroll
    def """Puede calcular el ratio de mutantes
           Cuando la cantidad de humanos es #humanCount
           Y la cantidad de mutantes es #mutantCount"""(){
        given: "Estadísticas a partir de las cantidades de humanos y mutantes"
            StatsDto statsDto = new StatsDto(humanCount,mutantCount)
        when: "Calculo el ratio de mutantes"
            Double ratio = statsDto.ratio()
        then: ""
            ratio == ratioEsperado
        where:
            humanCount | mutantCount | ratioEsperado
            0L         | 0L          | 0.0
            1L         | 0L          | 0.0
            0L         | 1L          | 1.0
            2L         | 2L          | 0.5
    }

}
