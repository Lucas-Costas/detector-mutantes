package ar.com.magneto.service;

import ar.com.magneto.dto.StatsDto;
import ar.com.magneto.redis.RedisAdapter;
import org.springframework.stereotype.Component;

@Component
public class StatsService {

    private static final String HUMAN_DNA_COUNT = "stats:count:dna:human";
    private static final String MUTANT_DNA_COUNT = "stats:count:dna:mutant";

    RedisAdapter redisAdapter = new RedisAdapter();

    public StatsDto stats() {
        Long humanDnaCount = this.getHumanDnaCount();
        Long mutantDnaCount = this.getMutantDnaCount();
        return new StatsDto(humanDnaCount,mutantDnaCount);
    }

    public void registerStats(Boolean isMutant) {
        String counterKey = isMutant ? MUTANT_DNA_COUNT : HUMAN_DNA_COUNT;
        redisAdapter.incrementCounter(counterKey);
    }

    public Long getHumanDnaCount() {
        return redisAdapter.getCounter(HUMAN_DNA_COUNT);
    }

    public Long getMutantDnaCount() {
        return redisAdapter.getCounter(MUTANT_DNA_COUNT);
    }

}
