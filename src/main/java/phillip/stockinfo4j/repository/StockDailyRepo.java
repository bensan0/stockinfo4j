package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.StockDailyTran;

@Repository
public interface StockDailyRepo extends JpaRepository<StockDailyTran, Long> {
}
