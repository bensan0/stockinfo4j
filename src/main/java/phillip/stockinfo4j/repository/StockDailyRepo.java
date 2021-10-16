package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

import java.util.List;
import java.util.Set;

@Repository
public interface StockDailyRepo extends JpaRepository<StockDailyTran, Long> {

    @Query(value = "select * from stock_daily_trans where date in :dates",nativeQuery = true)
    List<StockDailyTran> findByDates(@Param("dates") Set<Integer> dates);
}
