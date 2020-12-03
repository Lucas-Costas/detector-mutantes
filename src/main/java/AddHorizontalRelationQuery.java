import java.util.HashMap;
import java.util.Map;

public class AddHorizontalRelationQuery implements CypherQuery {

    private static final String INDEX_H_INICIO = "indexHA";
    private static final String INDEX_V_INICIO = "indexVA";
    private static final String INDEX_H_FIN = "indexHB";
    private static final String INDEX_V_FIN = "indexVB";
    private static final String BASE_FIN = "baseB";

    private Map<String, Object> parametros = new HashMap<>();

    public AddHorizontalRelationQuery(GenId genInicialId, Gen newGen) {
        parametros.put(INDEX_H_INICIO,genInicialId.getIndexHorizontal());
        parametros.put(INDEX_V_INICIO,genInicialId.getIndexVertical());
        parametros.put(INDEX_H_FIN, newGen.getId().getIndexHorizontal());
        parametros.put(INDEX_V_FIN,newGen.getId().getIndexVertical());
        parametros.put(BASE_FIN,newGen.getBase());
    }

    @Override
    public String query() {
        return "MATCH (a:Gen {indexH: $indexHA, indexV: $indexVA}) " +
                "CREATE (a)-[r:HORIZONTAL]->(b:Gen) " +
                "SET b.base= $baseB , b.indexH= $indexHB , b.indexV= $indexVB " +
                "RETURN b.base";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
