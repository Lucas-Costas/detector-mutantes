public class Gen {

    private GenId id;
    private String base;

    public Gen(Integer xIndex, Integer yIndex, String base) {
        this.id = new GenId(xIndex,yIndex);
        this.base = base;
    }

    public GenId getId() {
        return id;
    }

    public String getBase() {
        return base;
    }
}
