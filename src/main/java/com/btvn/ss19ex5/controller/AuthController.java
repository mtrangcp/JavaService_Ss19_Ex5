package com.btvn.ss19ex5.controller;

import com.btvn.ss19ex5.model.entity.RefreshToken;
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
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = "GIA_LAP_NEW_ACCESS_TOKEN_CHO_" + user.getUsername();

                    Map<String, String> data = new HashMap<>();
                    data.put("accessToken", newAccessToken);
                    data.put("refreshToken", requestRefreshToken);
                    return ResponseEntity.ok((Object) data);
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Forbidden");
                    errorResponse.put("message", "Refresh Token không tồn tại trong hệ thống!");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                });
    }


    @PostMapping("/panic-revoke")
    public ResponseEntity<String> panicRevoke(@RequestParam Long userId) {
        refreshTokenService.revokeAllUserTokens(userId);
        return ResponseEntity.ok("KÍCH HOẠT PANIC BUTTON THÀNH CÔNG: Toàn bộ phiên làm việc của User ID " + userId + " đã bị hủy bỏ tuyệt đối!");
    }
}