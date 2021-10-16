package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.Industry;

import java.util.List;

@Repository
public interface IndustryRepo extends JpaRepository<Industry, Long> {

    @Query(value = "select * from industry",nativeQuery = true)
    List<Industry> findAll();
}
