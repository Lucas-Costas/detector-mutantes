package ar.com.magneto.dto

import spock.lang.Specification
import spock.lang.Unroll

class DnaDtoTest extends Specification {

    public static final String[] ADN = ["abc", "def", "ghi"]

    def "Puede crear un DnaDto a partir de un ADN"(){
        when: "Creo un DTO de ADN"
            DnaDto dnaDto = new DnaDto(ADN)
        then: "Contiene el ADN especificado"
            dnaDto.dna == ADN
    }

    def "Puede calcular el ID del genoma"(){
        given: "Un DTO de ADN"
            DnaDto dnaDto = new DnaDto(ADN)
        when: "Obtengo su ID"
            String genomeId = dnaDto.getIdGenome()
        then: "Retorna todos los genes separados por coma"
            genomeId == "abc,def,ghi"
    }

    @Unroll
    def "Puede calcular el tamaño (N) de un gen con #tamanio bases"(){
        given: "Un DTO de ADN"
            DnaDto dnaDto = new DnaDto(adn as String[])
        when: "Obtengo el tamaño del gen"
            Integer genSize = dnaDto.genSize
        then: "Es #tamanio"
            genSize == tamanio
        where: ""
            adn                   | tamanio
            ["a"]                 | 1
            ["ab", "cd"]          | 2
            ["abc", "def", "ghi"] | 3
    }

}
