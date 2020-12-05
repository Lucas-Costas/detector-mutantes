package ar.com.magneto.resource;

import ar.com.magneto.dto.DnaDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ar.com.magneto.service.GenomeService;

@RestController
public class MutanteResource {

    private GenomeService genomaService = new GenomeService();

    @PostMapping("/mutant")
    public ResponseEntity isMutant(@RequestBody DnaDto dnaDto) {
        if(genomaService.isMutant(dnaDto)){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
