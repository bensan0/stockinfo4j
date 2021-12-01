package phillip.stockinfo4j.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.model.dto.DailyTranDTO;
import phillip.stockinfo4j.model.dto.OverboughtRankingDTO;

import javax.persistence.*;
import java.io.Serializable;

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "OverboughtRankingDTOResult", classes = {
                @ConstructorResult(
                        targetClass = OverboughtRankingDTO.class,
                        columns = {
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "industry", type = String.class),
                                @ColumnResult(name = "overbought", type = Long.class),
                                @ColumnResult(name = "closing", type = Double.class),
                                @ColumnResult(name = "flucPer", type = Double.class),
                                @ColumnResult(name = "tradingAmount", type = Double.class)
                        })
        }),
        @SqlResultSetMapping(name = "DailyTranDTOResult", classes = {
                @ConstructorResult(
                        targetClass = DailyTranDTO.class,
                        columns = {
                                //順序必須符合類的變數宣告順序
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "industry", type = String.class),
                                @ColumnResult(name = "tradingVol", type = Long.class),
                                @ColumnResult(name = "deal", type = Long.class),
                                @ColumnResult(name = "tradingAmount", type = Long.class),
                                @ColumnResult(name = "opening", type = Double.class),
                                @ColumnResult(name = "closing", type = Double.class),
                                @ColumnResult(name = "highest", type = Double.class),
                                @ColumnResult(name = "lowest", type = Double.class),
                                @ColumnResult(name = "fluc", type = Double.class),
                                @ColumnResult(name = "flucPer", type = Double.class),
                                @ColumnResult(name = "date", type = Integer.class),
                                @ColumnResult(name = "cdUnion", type = String.class),
                                @ColumnResult(name = "per", type = Double.class),
                                @ColumnResult(name = "foreignInvestors", type = Long.class),
                                @ColumnResult(name = "foreignCorp", type = Long.class),
                                @ColumnResult(name = "investmentTrust", type = Long.class),
                                @ColumnResult(name = "dealer", type = Long.class),
                                @ColumnResult(name = "dealerSelf", type = Long.class),
                                @ColumnResult(name = "dealerHedge", type = Long.class),
                                @ColumnResult(name = "corpTotal", type = Long.class)
                        }
                )
        })
})


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_basic_info", indexes =
        {@Index(columnList = "code")})
public class Stock implements Serializable {

    @Column(name = "id", unique = true, columnDefinition = "int not null UNIQUE key auto_increment", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Id
    @Column(name = "code", nullable = false, length = 4, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 15)
    private String name;

    @Column(name = "industry", nullable = false, length = 15)
    private String industry;

    @Column(name = "market", nullable = false, length = 10)
    private String market;

}
