package ar.com.magneto.service;

import ar.com.magneto.dto.StatsDto;
import ar.com.magneto.exception.RedisException;
import ar.com.magneto.exception.StatsException;
import ar.com.magneto.exception.StatsUpdateException;
import ar.com.magneto.redis.RedisAdapter;
import org.springframework.stereotype.Component;

@Component
public class StatsService {

    private static final String HUMAN_DNA_COUNT = "stats:count:dna:human";
    private static final String MUTANT_DNA_COUNT = "stats:count:dna:mutant";

    public static final String MUTANT_NUMBER_ERROR = "Fallo la consulta de la cantidad de mutantes";
    public static final String HUMAN_NUMBER_ERROR = "Fallo la consulta de la cantidad de humanos";
    public static final String UPDATE_STATS_ERROR = "Fallo la actualizacion de las estaditicas";

    RedisAdapter redisAdapter = new RedisAdapter();

    public StatsDto stats() {
        Long humanDnaCount = this.getHumanDnaCount();
        Long mutantDnaCount = this.getMutantDnaCount();
        return new StatsDto(humanDnaCount,mutantDnaCount);
    }

    public void registerStats(Boolean isMutant) {
        try {
            String counterKey = isMutant ? MUTANT_DNA_COUNT : HUMAN_DNA_COUNT;
            redisAdapter.incrementCounter(counterKey);
        } catch (RedisException ex) {
            throw new StatsUpdateException(UPDATE_STATS_ERROR,ex);
        }
    }

    public Long getHumanDnaCount() {
        try{
            return redisAdapter.getCounter(HUMAN_DNA_COUNT);
        } catch (RedisException ex){
            throw new StatsException(HUMAN_NUMBER_ERROR,ex);
        }
    }

    public Long getMutantDnaCount() {
        try {
            return redisAdapter.getCounter(MUTANT_DNA_COUNT);
        } catch (RedisException ex){
            throw new StatsException(MUTANT_NUMBER_ERROR,ex);
        }
    }

}
