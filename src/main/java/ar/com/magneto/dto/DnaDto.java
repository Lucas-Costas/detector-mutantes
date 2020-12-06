package ar.com.magneto.dto;

import lombok.Data;

import java.util.Arrays;

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

    public Boolean hasContent(){
        return this.dna.length != 0;
    }

    public Boolean isSquare(){
        Integer genSize = this.getGenSize();
        return Arrays.stream(this.dna)
                .noneMatch(gen -> gen.length() != genSize);
    }

    public Boolean hasRightBases(){
        boolean hasCorrectBases = true;
        for (char base : String.join("",this.dna).toCharArray()){
            if (base != 'C' && base != 'G' && base != 'A' && base != 'T'){
                hasCorrectBases = false;
                break;
            }
        }
        return hasCorrectBases;
    }

}
