package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.CorpDailyTran;

@Repository
public interface CorpDailyRepo extends JpaRepository<CorpDailyTran, Long> {
}
