import resource.MutanteResource;
import service.GenomeService;

public class MutantDetectionAplication {

    private static GenomeService genomaService = new GenomeService();

    public static void main( String... args ) throws Exception
    {
        String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
        String mensaje = "La secuencia %s es mutante. Resultado :%b";
        MutanteResource resource = new MutanteResource();
        System.out.println(String.format(mensaje,dna,resource.isMutant(dna)));
    }

}
