package mortar.BitcoinHistory;

public class ValueGap {

    private MarketDataRecord low;

    private MarketDataRecord high;

    public ValueGap() {

    }

    public ValueGap(MarketDataRecord low, MarketDataRecord high) {
        this.low = low;
        this.high = high;
    }

    public MarketDataRecord getLow() {
        return low;
    }

    public void setLow(MarketDataRecord low) {
        this.low = low;
    }

    public MarketDataRecord getHigh() {
        return high;
    }

    public void setHigh(MarketDataRecord high) {
        this.high = high;
    }
    
    public double gap() {
        
        return high.getValue() - low.getValue();
    }
    
    public double profit() {
        
        return (high.getValue() - low.getValue()) / low.getValue() * 100;
    }

}
