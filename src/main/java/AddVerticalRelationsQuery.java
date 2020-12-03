import java.util.HashMap;
import java.util.Map;

public class AddVerticalRelationsQuery implements  CypherQuery {

    @Override
    public String query() {
        return "MATCH (a),(b {indexH:a.indexH,indexV:a.indexV+1}) " +
                "CREATE (a)-[r:VERTICAL]->(b) " +
                "RETURN toString(COUNT(b))";
    }

    @Override
    public Map<String, Object> getParametros() {
        return new HashMap<>();
    }
}
