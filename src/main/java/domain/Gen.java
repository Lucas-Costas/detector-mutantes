package domain;

public class Gen {

    private GenCoordinate coordinate;
    private String base;

    public Gen(GenCoordinate coordinate, String base) {
        this.coordinate = coordinate;
        this.base = base;
    }

    public GenCoordinate getCoordinate() {
        return coordinate;
    }

    public String getBase() {
        return base;
    }
}
