package phillip.stockinfo4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phillip.stockinfo4j.model.pojo.Distribution;

@Repository
public interface DistributionRepo extends JpaRepository<Distribution, Long> {
}
