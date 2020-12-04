package resource;

import service.GenomeService;

public class MutanteResource {

    private GenomeService genomaService = new GenomeService();

    public Boolean isMutant(String[] dna) {
        return genomaService.isMutant(dna);
    }
}
