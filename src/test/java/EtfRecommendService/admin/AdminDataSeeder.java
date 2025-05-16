package EtfRecommendService.admin;

import EtfRecommendService.user.Password;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminDataSeeder {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void seedAdmin(){
        Admin admin = new Admin("admin",new Password("password"));
        em.persist(admin);
        em.flush();
        em.clear();
    }

}
