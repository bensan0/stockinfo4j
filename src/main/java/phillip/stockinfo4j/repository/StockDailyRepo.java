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

    @Query(value = "select * from stock_daily_trans where date in :dates order by date DESC",nativeQuery = true)
    List<StockDailyTran> findByDates(@Param("dates") List<Integer> dates);

    @Query(value = "select * from stock_daily_trans where date in :dates and code = :code order by date desc", nativeQuery = true)
    List<StockDailyTran> findByDatesAndCode(@Param("dates") Set<Integer> dates, @Param("code") String code);

    @Query(value = "select * from stock_daily_trans where date = :date and fluc_percent <= :flucPerUL and fluc_percent >= :flucPerLL", nativeQuery = true)
    List<StockDailyTran> findByflucPerAndDate(@Param("flucPerUL") Double flucPerUL, @Param("flucPerLL") Double flucPerLL, @Param("date") Integer date);

    @Query(value = "select * from stock_daily_trans where date between :startDate and :endDate", nativeQuery = true)
    List<StockDailyTran> findBetweenDate(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate);

    @Query(value = "delete from stock_daily_trans where date between :startDate and :endDate", nativeQuery = true)
    void delBetweenDate(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate);

}
