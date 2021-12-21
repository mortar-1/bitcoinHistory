package mortar.BitcoinHistory;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class QuerySession {

    private List<MarketData> queries = new ArrayList<>();

    public List<MarketData> getQueries() {
        return queries;
    }

    public void setQueries(List<MarketData> queries) {
        this.queries = queries;
    }

}
