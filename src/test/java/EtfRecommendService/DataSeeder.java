package EtfRecommendService;



import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.user.Password;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataSeeder {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void initData() {

        Etf etf1 = new Etf("QQQ빼빼로ETF","123456","뺴뺴로잘만드는회사", LocalDateTime.parse("2025-03-07T15:20:00"), Theme.SECTOR);

        em.persist(etf1);

        User user1 = new User("pepero",new Password("password"),"빼빼로부자",false);

        em.persist(user1);
    }
}