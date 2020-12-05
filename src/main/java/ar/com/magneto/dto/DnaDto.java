package ar.com.magneto.dto;

import lombok.Data;

@Data
public class DnaDto {

    private String[] dna;

    public DnaDto() {}

    public DnaDto(String[] dna) {
        this.dna = dna;
    }

    public String getIdGenome(){
        return String.join(",",dna);
    }

    public Integer getGenSize() {
        return dna.length;
    }
}
