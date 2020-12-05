package ar.com.magneto.resource;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.dto.StatsDto;
import ar.com.magneto.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ar.com.magneto.service.GenomeService;

@RestController
public class MutanteResource {

    private GenomeService genomaService = new GenomeService();

    private StatsService statsService = new StatsService();

    @PostMapping("/mutant")
    public ResponseEntity isMutant(@RequestBody DnaDto dnaDto) {
        HttpStatus status = genomaService.isMutant(dnaDto) ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDto> stats() {
        return ResponseEntity.ok(statsService.stats());
    }

}
