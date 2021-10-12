package phillip.stockinfo4j.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_basic_info", indexes =
        {@Index(columnList = "code")})
public class Stock implements Serializable {

    @Column(name = "id", unique = true, columnDefinition = "int not null UNIQUE key auto_increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Id
    @Column(name = "code", nullable = false, length = 4)
    private String code;

    @Column(name = "name", nullable = false, length = 15)
    private String name;

    @ManyToOne
    @JoinColumn(name = "indust_id", nullable = false)
    private Industry industry;

}
