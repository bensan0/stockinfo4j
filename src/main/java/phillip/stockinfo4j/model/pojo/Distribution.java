package phillip.stockinfo4j.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.model.dto.DistributionDTO;

import javax.persistence.*;
import java.io.Serializable;

@SqlResultSetMapping(name = "DistributionDTOResult", classes = {
        @ConstructorResult(
                targetClass = DistributionDTO.class,
                columns = {
                        @ColumnResult(name = "code", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "industry", type = String.class),
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
@Table(name = "ownership_distribution", indexes = {@Index(columnList = "code")})
public class Distribution implements Serializable {

    @Column(name = "id", unique = true, updatable = false, columnDefinition = "int not null UNIQUE key auto_increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code", nullable = false, length = 8)
    private String code;

    @Column(name = "rate11", nullable = false)//200-400
    private String rate11;

    @Column(name = "rate12", nullable = false)//400-600
    private String rate12;

    @Column(name = "rate13", nullable = false)//600-800
    private String rate13;

    @Column(name = "rate14", nullable = false)//800-1000
    private String rate14;

    @Column(name = "rate15", nullable = false)// >1000
    private String rate15;

    @Column(name = "total", nullable = false)// r1-r15
    private String total;

    @Column(name = "date", nullable = false)
    private Integer date;

    @Id
    @Column(name = "cd_union", nullable = false, length = 20)
    private String cdUnion;
}
