package ar.com.magneto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ar.com.magneto")
public class MutantDetectionAplication {

    public static void main( String... args ) throws Exception
    {
        //String[] dna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
        SpringApplication.run(MutantDetectionAplication.class,args);
    }

}
