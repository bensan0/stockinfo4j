package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.Attention;

@Repository
public interface AttentionRepo extends JpaRepository<Attention, Long> {

    @Query(value = "delete from attention where code = :code", nativeQuery = true)
    void delByCode(@Param("code") String code);
}
