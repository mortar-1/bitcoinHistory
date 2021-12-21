package mortar.BitcoinHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarketData implements Comparable<MarketData> {

    private LocalDate from;

    private LocalDate to;

    private List<MarketDataRecord> prices;

    private List<MarketDataRecord> marketCaps;

    private List<MarketDataRecord> totalVolumes;

    private Granularity granularity;
    
    private LocalDateTime created;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public MarketData(LocalDate from, LocalDate to) {

        this.from = from;
        this.to = to;
        this.prices = new ArrayList<>();
        this.marketCaps = new ArrayList<>();
        this.totalVolumes = new ArrayList<>();
        this.granularity = Granularity.FIVEMIN;
        this.created = LocalDateTime.now();
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

    public List<MarketDataRecord> getPrices() {
        return prices;
    }

    public void setPrices(List<MarketDataRecord> prices) {
        this.prices = prices;
    }

    public List<MarketDataRecord> getMarketCaps() {
        return marketCaps;
    }

    public void setMarketCaps(List<MarketDataRecord> marketCaps) {
        this.marketCaps = marketCaps;
    }

    public List<MarketDataRecord> getTotalVolumes() {
        return totalVolumes;
    }

    public void setTotalVolumes(List<MarketDataRecord> totalVolumes) {
        this.totalVolumes = totalVolumes;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }

    public MarketDataRecord highestPrice() {

        return findHighestFrom(getPricesAtMidnights());
    }

    public MarketDataRecord lowestPrice() {

        return findLowestFrom(getPricesAtMidnights());
    }

    public MarketDataRecord highestVolume() {

        return findHighestFrom(getVolumesAtMidnights());
    }

    public MarketDataRecord findHighestFrom(List<MarketDataRecord> list) {

        MarketDataRecord highest = list.get(0);

        for (MarketDataRecord mdr : list) {

            if (mdr.getValue() > highest.getValue()) {

                highest = mdr;
            }
        }

        return highest;
    }

    public MarketDataRecord highestVolumeToCapRatio() {

        List<MarketDataRecord> volumes = getVolumesAtMidnights();

        List<MarketDataRecord> caps = getMarketCapsAtMidnights();

        MarketDataRecord highest = createVolumeToCapRatioRecord(volumes, caps, 0);

        for (int i = 0; i < volumes.size(); i++) {
            
            MarketDataRecord challenger = createVolumeToCapRatioRecord(volumes, caps, i);
            
            if (challenger.getValue() > highest.getValue()) {
                
                highest = challenger;
            }
        }
        System.out.println("");
        System.out.println("Volume / Cap: value = " + highest.getValue() + "; DateTime = " + highest.getDateTime().format(formatter));
        System.out.println("");
        
        return highest;
    }

    public MarketDataRecord createVolumeToCapRatioRecord(List<MarketDataRecord> volumes, List<MarketDataRecord> caps, int index) {

        MarketDataRecord volumeToCap = new MarketDataRecord();

        volumeToCap.setUnixTimestamp(volumes.get(index).getUnixTimestamp());

        volumeToCap.setDateTime(volumes.get(index).getDateTime());

        volumeToCap.setDate(volumes.get(index).getDate());

        volumeToCap.setValue(volumes.get(index).getValue() / caps.get(index).getValue());

        return volumeToCap;
    }

    public MarketDataRecord findLowestFrom(List<MarketDataRecord> list) {
                
        MarketDataRecord lowest = list.get(0);

        for (MarketDataRecord mdr : list) {

            if (mdr.getValue() < lowest.getValue()) {

                lowest = mdr;
            }
        }

        return lowest;
    }

    public ValueGap whenToBuyAndSell() {

        if (lowestPrice().getDate().isBefore(highestPrice().getDate())) {

            System.out.println("!!!!!!!!!! LOWEST BEFORE HIGHEST !!!!!!!!!!");

            return new ValueGap(lowestPrice(), highestPrice());
        }

        return biggestPositiveValueGap(getPricesAtMidnights());
    }

    public ValueGap biggestPositiveValueGap(List<MarketDataRecord> list) {

        ValueGap biggest = new ValueGap(list.get(0), list.get(0));

        for (int i = 0; i < list.size(); i++) {

            if (biggest.gap() < biggestPositiveGapToFollowingTimePoints(list, i).gap()) {

                biggest = biggestPositiveGapToFollowingTimePoints(list, i);
            }
        }

        System.out.println("");
        System.out.println("BIGGEST: from = " + biggest.getLow().getDate() + " (" + biggest.getLow().getValue() + "); to = " + biggest.getHigh().getDate() + " (" + biggest.getHigh().getValue() + "); gap = " + biggest.gap());

        return biggest;
    }

    public ValueGap biggestPositiveGapToFollowingTimePoints(List<MarketDataRecord> list, int index) {

        MarketDataRecord comparable = list.get(index);

        ValueGap biggest = new ValueGap(comparable, comparable);

        for (int i = index; i < list.size(); i++) {

            MarketDataRecord compareTo = list.get(i);

            if (compareTo.getValue() - comparable.getValue() > biggest.gap()) {

                biggest = new ValueGap(comparable, compareTo);
            }
        }

        return biggest;
    }

    public List<MarketDataRecord> getPricesAtMidnights() {

        return getMidnightRecords(this.prices);
    }

    public List<MarketDataRecord> getMarketCapsAtMidnights() {

        return getMidnightRecords(this.marketCaps);
    }

    public List<MarketDataRecord> getVolumesAtMidnights() {

        return getMidnightRecords(this.totalVolumes);
    }

    public List<MarketDataRecord> getMidnightRecords(List<MarketDataRecord> list) {

        if (granularity.equals(granularity.DAILY)) {

            return list;
        }

        List<MarketDataRecord> midnights = new ArrayList<>();

        LocalDateTime nextMidnight = from.atStartOfDay();

        LocalDateTime lastMidnight = list.get(list.size() - 1).getDate().atStartOfDay();

        MarketDataRecord closest = list.get(0);

        for (MarketDataRecord mdr : list) {

            long distClosest = Math.abs(ChronoUnit.SECONDS.between(nextMidnight, closest.getDateTime()));

            long distChallenger = Math.abs(ChronoUnit.SECONDS.between(nextMidnight, mdr.getDateTime()));

            if (distChallenger <= distClosest) {

                closest = mdr;
            } else {

                closest.setDate(nextMidnight.toLocalDate());

                midnights.add(closest);

                nextMidnight = nextMidnight.plusDays(1);

                if (nextMidnight.isAfter(lastMidnight)) {

                    break;
                }
            }

            if (mdr == list.get(list.size() - 1)) {

                closest.setDate(nextMidnight.toLocalDate());

                midnights.add(closest);
            }
        }

        return midnights;
    }

    public List<Trend> longestBearish() {

        List<MarketDataRecord> pam = getPricesAtMidnights();

        List<Trend> bearTrends = new ArrayList<>();

        List<MarketDataRecord> longest = new ArrayList<>();

        List<MarketDataRecord> challenger = new ArrayList<>();

        List<List<MarketDataRecord>> equallyLongs = new ArrayList<>();

        for (int i = 1; i < pam.size(); i++) {

            MarketDataRecord current = pam.get(i);
            MarketDataRecord previous = pam.get(i - 1);

            if (current.getValue() < previous.getValue()) {

                challenger.add(current);

                if (!challenger.contains(previous)) {

                    challenger.add(previous);
                }

                if (i < pam.size() - 1) {

                    continue;
                }
            }

            if (challenger.size() == longest.size() && longest.size() > 0) {

                Collections.sort(challenger);

                equallyLongs.add(challenger);
            }

            if (challenger.size() > longest.size()) {

                longest = challenger;

                equallyLongs.clear();

                Collections.sort(longest);

                equallyLongs.add(longest);
            }

            challenger = new ArrayList<>();

        }

        if (equallyLongs.isEmpty()) {

            return bearTrends;
        }

        for (List<MarketDataRecord> oneOfLongest : equallyLongs) {

            LocalDate from = oneOfLongest.get(0).getDate();

            LocalDate to = oneOfLongest.get(longest.size() - 1).getDate();

            long days = ChronoUnit.DAYS.between(from, to);

            bearTrends.add(new Trend(oneOfLongest, from, to, days));
        }

        return bearTrends;
    }

    public List<Trend> longestBullish() {

        List<MarketDataRecord> pam = getPricesAtMidnights();

        // PRINT PRICES AT MIDNIGHT
        pam.stream().forEach(mdr -> System.out.println(mdr.getDate() + "  :  " + mdr.getValue()));
        System.out.println("");

        List<Trend> bullTrends = new ArrayList<>();

        List<MarketDataRecord> longest = new ArrayList<>();

        List<MarketDataRecord> challenger = new ArrayList<>();

        List<List<MarketDataRecord>> equallyLongs = new ArrayList<>();

        for (int i = 1; i < pam.size(); i++) {

            MarketDataRecord current = pam.get(i);
            MarketDataRecord previous = pam.get(i - 1);

            if (current.getValue() > previous.getValue()) {

                challenger.add(current);

                if (!challenger.contains(previous)) {

                    challenger.add(previous);
                }

                if (i < pam.size() - 1) {

                    continue;
                }
            }

            if (challenger.size() == longest.size() && longest.size() > 0) {

                Collections.sort(challenger);

                equallyLongs.add(challenger);
            }

            if (challenger.size() > longest.size()) {

                longest = challenger;

                equallyLongs.clear();

                Collections.sort(longest);

                equallyLongs.add(longest);
            }

            challenger = new ArrayList<>();

        }

        if (equallyLongs.isEmpty()) {

            return bullTrends;
        }

        for (List<MarketDataRecord> oneOfLongest : equallyLongs) {

            LocalDate from = oneOfLongest.get(0).getDate();

            LocalDate to = oneOfLongest.get(longest.size() - 1).getDate();

            long days = ChronoUnit.DAYS.between(from, to);

            bullTrends.add(new Trend(oneOfLongest, from, to, days));
        }

        return bullTrends;
    }
    
    @Override
    public int compareTo(MarketData other) {
        
        return other.created.compareTo(this.created);
    }
}
