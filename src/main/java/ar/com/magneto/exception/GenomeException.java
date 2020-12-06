package ar.com.magneto.exception;

public class GenomeException extends RuntimeException {

    private static final String SUFFIX_MUTANT = " El ADN era mutante";
    private static final String SUFFIX_HUMAN = " El ADN era humano";

    public GenomeException(String message){
        super(message);
    }

    public GenomeException(String message, Boolean isMutant, Throwable ex) {
        super(message(message,isMutant),ex);
    }

    public GenomeException(String message, Throwable ex) {
        super(message,ex);
    }

    private static String message(String messageBase, Boolean isMutant){
        String suffix = isMutant ? SUFFIX_MUTANT : SUFFIX_HUMAN;
        return messageBase + suffix;
    }
}
