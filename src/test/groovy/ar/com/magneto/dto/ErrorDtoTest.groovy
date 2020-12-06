package ar.com.magneto.dto

import ar.com.magneto.exception.GenomeException
import ar.com.magneto.exception.StatsException
import spock.lang.Specification

class ErrorDtoTest extends Specification {

    def "Puedo crear un ErrorDto a partir de un mensaje y una causa"(){
        when: "Creo un ErrorDto con mensaje y causa"
            ErrorDto errorDto = new ErrorDto("Mensaje","Causa")
        then: "Los atributos estan bien establecidos"
            errorDto.message == "Mensaje"
            errorDto.cause == "Causa"
    }

    def "Puedo crear un ErrorDto a partir de una excepcion"(){
        when: "Creo un ErrorDto con mensaje y causa"
            ErrorDto errorDto = new ErrorDto(new GenomeException("Mensaje",new StatsException("Causa")))
        then: "Los atributos estan bien establecidos"
            errorDto.message == "Mensaje"
            errorDto.cause == "Causa"
    }

}
