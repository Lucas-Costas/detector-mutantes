package domain;

public class GenCoordinate {

    private Integer horizontalIndex;

    private Integer verticalIndex;

    public GenCoordinate(Integer indexHorizontal, Integer indexVertical) {
        this.horizontalIndex = indexHorizontal;
        this.verticalIndex = indexVertical;
    }

    public Integer getHorizontalIndex() {
        return horizontalIndex;
    }

    public Integer getVerticalIndex() {
        return verticalIndex;
    }

    public GenCoordinate nextCoordinateRight(){
        return new GenCoordinate(this.horizontalIndex +1,this.verticalIndex);
    }

}
