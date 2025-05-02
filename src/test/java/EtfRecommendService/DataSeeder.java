package EtfRecommendService;



import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataSeeder {


    UserRepository userRepository;
    EtfRepository etfRepository;

    public DataSeeder(UserRepository userRepository, EtfRepository etfRepository) {
        this.userRepository = userRepository;
        this.etfRepository = etfRepository;
    }




    @Transactional
    public void initData() {


        Etf etf1 = new Etf("QQQ빼빼로ETF","123456","뺴뺴로잘만드는회사", LocalDateTime.parse("2025-03-07T15:20:00"), Theme.SECTOR);

        etfRepository.save(etf1);

        User user1 = User.builder()
                .nickName("빼빼로부자")
                .loginId("pepero")
                .build();

        userRepository.save(user1);







    }
}
