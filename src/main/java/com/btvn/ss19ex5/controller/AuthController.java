package com.btvn.ss19ex5.controller;

import com.btvn.ss19ex5.model.entity.RefreshToken;
import com.btvn.ss19ex5.model.entity.User;
import com.btvn.ss19ex5.repository.RefreshTokenRepository;
import com.btvn.ss19ex5.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String requestRefreshToken) {
        var tokenOpt = refreshTokenRepository.findByToken(requestRefreshToken);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Refresh Token không tồn tại trong hệ thống!");
        }

        try {
            RefreshToken refreshToken = refreshTokenService.verifyExpiration(tokenOpt.get());
            User user = refreshToken.getUser();

            String newAccessToken = "GIA_LAP_NEW_ACCESS_TOKEN_CHO_" + user.getUsername();
            Map<String, String> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            data.put("refreshToken", requestRefreshToken);

            return ResponseEntity.ok(data);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PostMapping("/panic-revoke")
    public ResponseEntity<String> panicRevoke(@RequestParam Long userId) {
        refreshTokenService.revokeAllUserTokens(userId);
        return ResponseEntity.ok("KÍCH HOẠT PANIC BUTTON THÀNH CÔNG: Toàn bộ phiên làm việc của User ID " + userId + " đã bị hủy bỏ tuyệt đối!");
    }
}