package phillip.stockinfo4j.model.stockdaily;

import javax.persistence.*;

@Entity
@Table(name = "stock_daily_trans",indexes= {
        @Index(columnList="code")
})
public class StockDailyTran {

    @Column(name = "id",unique = true, nullable = false, columnDefinition = "int not null UNIQUE key auto_increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code",nullable = false, length = 8)
    private String code;

    @Column(name = "name",nullable = false, length = 15)
    private String name;

    @Column(name = "trading_vol",nullable = false)
    private Integer tradingVol = 0;//成交股數

    @Column(name = "deal",nullable = false)
    private Integer deal = 0;//成交筆數

    @Column(name = "opening",nullable = false)
    private Double opening = 0.00 ;//開盤價

    @Column(name = "closing",nullable = false)
    private Double closing = 0.00;//收盤價

    @Column(name = "highest",nullable = false)
    private Double highest = 0.00;//盤中最高

    @Column(name = "lowest",nullable = false)
    private Double lowest = 0.00;//盤中最低

    @Column(name = "fluctuation",nullable = false)
    private Double fluc = 0.00;//漲跌價差

    @Column(name = "fluc_percent",nullable = false)
    private Double flucPer = 0.00;//漲跌幅

    @Column(name = "date",nullable = false)
    private Integer date;

    @Id
    @Column(name = "cd_union",nullable = false,length = 20)
    private String cdUnion;

    @Column(name = "per")
    private Double per;//本益比

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTradingVol() {
        return tradingVol;
    }

    public void setTradingVol(Integer tradingVol) {
        this.tradingVol = tradingVol;
    }

    public Integer getDeal() {
        return deal;
    }

    public void setDeal(Integer deal) {
        this.deal = deal;
    }

    public Double getOpening() {
        return opening;
    }

    public void setOpening(Double opening) {
        this.opening = opening;
    }

    public Double getClosing() {
        return closing;
    }

    public void setClosing(Double closing) {
        this.closing = closing;
    }

    public Double getHighest() {
        return highest;
    }

    public void setHighest(Double highest) {
        this.highest = highest;
    }

    public Double getLowest() {
        return lowest;
    }

    public void setLowest(Double lowest) {
        this.lowest = lowest;
    }

    public Double getFluc() {
        return fluc;
    }

    public void setFluc(Double fluc) {
        this.fluc = fluc;
    }

    public Double getFlucPer() {
        return flucPer;
    }

    public void setFlucPer(Double flucPer) {
        this.flucPer = flucPer;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getCdUnion() {
        return cdUnion;
    }

    public void setCdUnion(String cdUnion) {
        this.cdUnion = cdUnion;
    }

    public Double getPer() {
        return per;
    }

    public void setPer(Double per) {
        this.per = per;
    }

    @Override
    public String toString() {
        return "StockDailyTran{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", tradingVol=" + tradingVol +
                ", deal=" + deal +
                ", opening=" + opening +
                ", closing=" + closing +
                ", highest=" + highest +
                ", lowest=" + lowest +
                ", fluc=" + fluc +
                ", flucPer=" + flucPer +
                ", date=" + date +
                ", cdUnion='" + cdUnion + '\'' +
                ", per=" + per +
                '}';
    }
}
