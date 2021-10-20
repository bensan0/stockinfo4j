package phillip.stockinfo4j.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attention")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attention implements Serializable {

    @Column(name = "id", unique = true, columnDefinition = "int not null UNIQUE key auto_increment", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Id
    @Column(name = "code", nullable = false, length = 4)
    private String code;

    @Column(name = "note")
    private String note;

    @Column(name = "join_date")
    private Integer joinDate;
}
