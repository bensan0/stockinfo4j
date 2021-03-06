package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;

import java.util.List;
import java.util.Set;

@Repository
public interface CorpDailyRepo extends JpaRepository<CorpDailyTran, Long> {

    @Query(value = "select * from corp_daily_trans where date in :dates and code = :code order by date desc",nativeQuery = true)
    List<CorpDailyTran> findByDatesAndCode(@Param("dates") Set<Integer> dates, @Param("code") String code);

}
