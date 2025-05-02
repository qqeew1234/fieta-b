package EtfRecommendService.admin;

import EtfRecommendService.admin.dto.AdminLoginRequest;
import EtfRecommendService.admin.dto.AdminLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest loginRequest) {
        AdminLoginResponse login = adminService.login(loginRequest);
        return ResponseEntity.ok(login);
    }


}
