package fieta.etf;


import fieta.etf.domain.Etf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtfRepository extends JpaRepository<Etf, Long> {
}
