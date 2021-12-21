package mortar.BitcoinHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Trend {

    private List<MarketDataRecord> records;

    private LocalDate from;

    private LocalDate to;

    private long days;

    public Trend(List<MarketDataRecord> records, LocalDate from, LocalDate to, long days) {
        this.records = records;
        this.from = from;
        this.to = to;
        this.days = days;
    }

    Trend() {
        this.records = new ArrayList<>();
        this.days = 0;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public long getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

}
