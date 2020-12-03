import java.util.HashMap;
import java.util.Map;

public class CreateGen implements CypherQuery {

    private static final String INDEX_HORIZONTAL = "indexH";
    private static final String INDEX_VERTICAL = "indexV";
    private static final String BASE = "base";

    private Map<String, Object> parametros = new HashMap<>();

    public CreateGen(Gen gen) {
        parametros.put(INDEX_HORIZONTAL,gen.getId().getIndexHorizontal());
        parametros.put(INDEX_VERTICAL,gen.getId().getIndexVertical());
        parametros.put(BASE,gen.getBase());
    }

    @Override
    public String query() {
        return "CREATE (a:Gen) " +
                "SET a.base= $base, a.indexV= $indexV, a.indexH= $indexH " +
                "RETURN a.base";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
