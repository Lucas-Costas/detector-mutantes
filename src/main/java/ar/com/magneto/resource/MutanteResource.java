package ar.com.magneto.resource;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.dto.ErrorDto;
import ar.com.magneto.dto.StatsDto;
import ar.com.magneto.exception.GenomeException;
import ar.com.magneto.exception.InvalidDnaException;
import ar.com.magneto.exception.StatsException;
import ar.com.magneto.exception.StatsUpdateException;
import ar.com.magneto.service.GenomeService;
import ar.com.magneto.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MutanteResource {

    private static final String DNA_INVALIDO_ERROR = "El ADN es inválido";
    private static final String STATS_ERROR = "Ocurrio un error al obtener las estadisticas";

    @Autowired
    private GenomeService genomeService;

    @Autowired
    private StatsService statsService;

    @PostMapping("/mutant")
    public ResponseEntity isMutant(@RequestBody DnaDto dnaDto) {
        HttpStatus status = genomeService.isMutant(dnaDto) ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDto> stats() {
        return ResponseEntity.ok(statsService.stats());
    }

    @ExceptionHandler(InvalidDnaException.class)
    public ResponseEntity<ErrorDto> handleInvalidDnaException(InvalidDnaException ide){
        return new ResponseEntity<>(new ErrorDto(DNA_INVALIDO_ERROR,ide.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({GenomeException.class, StatsUpdateException.class})
    public ResponseEntity<ErrorDto> handleDnaEvaluationException(RuntimeException ge){
        return new ResponseEntity<>(new ErrorDto(ge), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({StatsException.class})
    public ResponseEntity<ErrorDto> handleDnaEvaluationException(StatsException se){
        return new ResponseEntity<>(new ErrorDto(STATS_ERROR,se.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
