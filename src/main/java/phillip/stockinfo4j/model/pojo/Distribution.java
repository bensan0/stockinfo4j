package phillip.stockinfo4j.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.model.dto.DistributionDTO;
import phillip.stockinfo4j.model.dto.StockIndustryDTO;

import javax.persistence.*;
import java.io.Serializable;
@SqlResultSetMapping(name = "DistributionDTOResult", classes = {
        @ConstructorResult(
                targetClass = DistributionDTO.class,
                columns = {
                        @ColumnResult(name = "code", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "rate11", type = String.class),
                        @ColumnResult(name = "rate12", type = String.class),
                        @ColumnResult(name = "rate13", type = String.class),
                        @ColumnResult(name = "rate14", type = String.class),
                        @ColumnResult(name = "rate15", type = String.class),
                        @ColumnResult(name = "total", type = String.class),
                        @ColumnResult(name = "date", type = Integer.class),
                })
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ownership_distribution",indexes = {@Index(columnList = "code")})
public class Distribution implements Serializable {

    @Column(name = "id",unique = true,updatable = false, columnDefinition = "int not null UNIQUE key auto_increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code",nullable = false, length = 8)
    private String code;

    @Column(name = "rate11", nullable = false)
    private String rate11;

    @Column(name = "rate12",nullable = false)
    private String rate12;

    @Column(name = "rate13",nullable = false)
    private String rate13;

    @Column(name = "rate14",nullable = false)
    private String rate14;

    @Column(name = "rate15",nullable = false)
    private String rate15;

    @Column(name = "total",nullable = false)
    private String total ;

    @Column(name = "date",nullable = false)
    private Integer date;

    @Id
    @Column(name = "cd_union",nullable = false,length = 20)
    private String cdUnion;
}
