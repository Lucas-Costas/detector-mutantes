package ar.com.magneto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StatsDto {

    private Long countHumanDna;

    private Long countMutantDna;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Double ratio;

    public StatsDto(Long humanDnaCount, Long mutantDnaCount) {
        this.countHumanDna = humanDnaCount;
        this.countMutantDna = mutantDnaCount;
        this.ratio = this.ratio();
    }

    private Double ratio() {
        Long total = this.total();
        return total == 0 ? 0L : (double) this.countMutantDna/total;
    }

    private Long total(){
        return this.countMutantDna + this.countHumanDna;
    }

}
