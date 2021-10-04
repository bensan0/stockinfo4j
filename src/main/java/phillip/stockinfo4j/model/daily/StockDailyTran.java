package phillip.stockinfo4j.model.daily;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
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
