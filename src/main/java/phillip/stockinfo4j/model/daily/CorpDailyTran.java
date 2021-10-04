package phillip.stockinfo4j.model.daily;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "corp_daily_trans", indexes =
        {@Index(columnList = "code")})
@Getter
@Setter
public class CorpDailyTran {

    @Column(name = "id",unique = true, nullable = false, columnDefinition = "int not null UNIQUE key auto_increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code",nullable = false, length = 8)
    private String code;

    @Column(name = "name",nullable = false, length = 15)
    private String name;

    @Column(name = "foreign_investors",nullable = false)
    private Integer foreignInvestors = 0;//外陸資

    @Column(name = "foreign_corp",nullable = false)
    private Integer foreignCorp = 0;//外資自營商

    @Column(name = "investment_trust",nullable = false)
    private Integer investmentTrust = 0;//投信

    @Column(name = "dealer",nullable = false)
    private Integer dealer = 0;//自營商

    @Column(name = "dealer_self",nullable = false)
    private Integer dealerSelf = 0;//自營商自行

    @Column(name = "dealer_hedge",nullable = false)
    private Integer dealerHedge = 0;//自營商避險

    @Column(name = "total",nullable = false)
    private Integer total = 0;//三大法人合計(不含自營商)

    @Column(name = "date",nullable = false)
    private Integer date;

    @Id
    @Column(name = "cd_union",nullable = false,length = 20)
    private String cdUnion;

    @Override
    public String toString() {
        return "CorpDailyTran{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", foreignInvestors=" + foreignInvestors +
                ", foreignCorp=" + foreignCorp +
                ", investmentTrust=" + investmentTrust +
                ", dealer=" + dealer +
                ", dealerSelf=" + dealerSelf +
                ", dealerHedge=" + dealerHedge +
                ", total=" + total +
                ", date=" + date +
                ", cdUnion='" + cdUnion + '\'' +
                '}';
    }
}
