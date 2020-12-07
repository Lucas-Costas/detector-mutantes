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

    @Unroll
    def "Evalua si un ADN tiene contenido cuando #caso"(){
        when: "Evaluo si un ADN es cuadrado"
            Boolean result = dnaDto.hasContent()
        then: "El ADN #caso"
            result == hasContent
        where:
            caso                 | dnaDto          | hasContent
            "tiene contenido"    | aValidDnaDto()  | true
            "no tiene contenido" | anEmptyDnaDto() | false
            "no tiene ADN"       | aNullDnaDto()   | false
    }

    @Unroll
    def "Evalua si un ADN es cuadrado cuando #caso"(){
        when: "Evaluo si un ADN es cuadrado"
            Boolean result = dnaDto.isSquare()
        then: "El ADN #caso"
            result == isSquare
        where:
            caso             | dnaDto             | isSquare
            "es cuadrado"    | aValidDnaDto()     | true
            "no es cuadrado" | aNotSquareDnaDto() | false
    }

    @Unroll
    def "Evalua si un ADN esta bien formado cuando #caso"(){
        when: "Evaluo si un ADN es cuadrado"
            Boolean result = dnaDto.hasRightBases()
        then: "El ADN #caso"
            result == hasRightBases
        where:
            caso                | dnaDto             | hasRightBases
            "esta bien formado" | aValidDnaDto()     | true
            "esta mal formado"  | aMalformedDnaDto() | false
    }

    def aValidDnaDto() {
        new DnaDto(["AAA", "CCC", "TTT"] as String[])
    }

    def anEmptyDnaDto() {
        new DnaDto([] as String[])
    }

    def aNullDnaDto() {
        new DnaDto()
    }

    def aMalformedDnaDto() {
        new DnaDto(["AAA", "BBB", "CCC"] as String[])
    }

    def aNotSquareDnaDto() {
        new DnaDto(["AA", "TT", "CC"] as String[])
    }

}
