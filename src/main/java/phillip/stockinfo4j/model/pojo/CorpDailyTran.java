package phillip.stockinfo4j.model.pojo;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "corp_daily_trans", indexes =
        {@Index(columnList = "code")})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorpDailyTran implements Serializable {

    @Column(name = "id",unique = true, columnDefinition = "int not null UNIQUE key auto_increment", updatable = false/*避免saveall資料重複時update報can not be null*/)
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
}
