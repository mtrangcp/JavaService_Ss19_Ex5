package com.btvn.ss19ex5.service;

import com.btvn.ss19ex5.model.entity.RefreshToken;
import com.btvn.ss19ex5.model.entity.User;
import com.btvn.ss19ex5.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        // check hết hạn time
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn sử dụng. Vui lòng đăng nhập lại!");
        }

        // check trạng thái bị hủy bởi Admin (Panic Button)
        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token này đã bị vô hiệu hóa/Hủy bỏ quyền truy cập!");
        }
        return token;
    }

    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
