package mortar.BitcoinHistory;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MarketDataService {

    @Autowired
    private QuerySession qSession;

    public void addAttributesToModelForPageMain(Model model) {

        List<MarketData> queries = qSession.getQueries();

        Collections.sort(queries);

        model.addAttribute("queries", queries);

    }

    public void fetch(String from, String to) {

        if (!(from == null || to == null || from.isBlank() || to.isBlank())) {

            MarketData md = getMarketData(from, to);

            qSession.getQueries().add(md);
        }
    }

    public void deleteQuery(int index) {

        if (!qSession.getQueries().isEmpty()) {

            qSession.getQueries().remove(index);
        }
    }

    public void clearSession() {
                
        qSession.getQueries().clear();
    }

    public MarketData getMarketData(String from, String to) {

        LocalDate fromDate = LocalDate.parse(from);

        LocalDate toDate = LocalDate.parse(to);

        long epochFrom = fromDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) - 60 * 10;

        long epochTo = toDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) + 60 * 60;

        Mono<JsonNode> nodeMono = WebClient.create().get()
                .uri("https://api.coingecko.com/api/v3/coins/bitcoin/market_chart/range?vs_currency=eur&from=" + epochFrom + "&to=" + epochTo)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class);

        JsonNode node = nodeMono.share().block();

        MarketData marketData = new MarketData(fromDate, toDate);

        marketData.getPrices().addAll(getRecordsFromNode(node, "prices"));

        marketData.getMarketCaps().addAll(getRecordsFromNode(node, "market_caps"));

        marketData.getTotalVolumes().addAll(getRecordsFromNode(node, "total_volumes"));

        LocalDateTime firstTimestamp = marketData.getPrices().get(0).getDateTime();

        LocalDateTime secondTimestamp = marketData.getPrices().get(1).getDateTime();

        if (ChronoUnit.SECONDS.between(firstTimestamp, secondTimestamp) > 60 * 50) {

            marketData.setGranularity(Granularity.HOURLY);
        }

        if (ChronoUnit.SECONDS.between(firstTimestamp, secondTimestamp) > 60 * 60 * 2) {

            marketData.setGranularity(Granularity.DAILY);
        }

        return marketData;
    }

    public ArrayList<MarketDataRecord> getRecordsFromNode(JsonNode node, String fieldName) {

        ArrayList<MarketDataRecord> list = new ArrayList<>();

        for (JsonNode row : node.get(fieldName)) {

            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(row.get(0).asLong()), ZoneOffset.UTC);

            MarketDataRecord mdr = new MarketDataRecord(row.get(0).asLong(), ldt, ldt.toLocalDate(), row.get(1).asDouble());

            list.add(mdr);
        }

        return list;
    }
}
