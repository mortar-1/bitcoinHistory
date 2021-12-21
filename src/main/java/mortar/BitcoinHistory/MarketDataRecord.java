package mortar.BitcoinHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MarketDataRecord implements Comparable<MarketDataRecord> {

    private long unixTimestamp;

    private LocalDateTime dateTime;

    private LocalDate date;

    private double value;

    public MarketDataRecord(long unixTimestamp, LocalDateTime time, LocalDate date, double value) {
        this.unixTimestamp = unixTimestamp;
        this.dateTime = time;
        this.date = date;
        this.value = value;
    }

    MarketDataRecord() {

    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime time) {
        this.dateTime = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(MarketDataRecord other) {

        return this.dateTime.compareTo(other.dateTime);
    }

}
