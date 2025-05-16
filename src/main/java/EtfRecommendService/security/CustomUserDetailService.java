package EtfRecommendService.security;

import EtfRecommendService.admin.Admin;
import EtfRecommendService.admin.AdminRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    //identifier 는 "type:username" 과 같은 형식으로 전달
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException{
        String[] identifiers = identifier.split(":");
        Collection<? extends GrantedAuthority> types =
                List.of(new SimpleGrantedAuthority("ROLE_"+identifiers[0]));
        String loginId = identifiers[1];

        if ("ADMIN".equals(identifiers[0])){
            Admin admin = adminRepository.findByLoginId(loginId).orElseThrow(()->
                    new UsernameNotFoundException("User Not Founded"));
            return new UserDetail(admin.getLoginId(), admin.getPassword().getHash(), types);
        }

        User user = userRepository.findByLoginIdAndIsDeletedFalse(loginId).orElseThrow(()->
                new UsernameNotFoundException("User Not Founded"));

        return new UserDetail(user.getLoginId(), user.getPassword().getHash(), types);
    }
}
