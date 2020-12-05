package ar.com.magneto.domain;

public class Gen {

    private GenCoordinate coordinate;
    private String base;
    private String genomeId;

    public Gen(GenCoordinate coordinate, String base,String genomeId) {
        this.coordinate = coordinate;
        this.base = base;
        this.genomeId = genomeId;
    }

    public GenCoordinate getCoordinate() {
        return coordinate;
    }

    public String getBase() {
        return base;
    }

    public String getGenomeId() {
        return genomeId;
    }
}
