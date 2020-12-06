package ar.com.magneto.resource;

import ar.com.magneto.dto.DnaDto;
import ar.com.magneto.dto.StatsDto;
import ar.com.magneto.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ar.com.magneto.service.GenomeService;

@RestController
public class MutanteResource {

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

}
