package com.btvn.ss19ex5;

import com.btvn.ss19ex5.model.entity.User;
import com.btvn.ss19ex5.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Ss19Ex5Application {

    public static void main(String[] args) {
        SpringApplication.run(Ss19Ex5Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("tranghtm").isEmpty()) {
                User sampleUser = new User();
                sampleUser.setUsername("tranghtm");
                sampleUser.setPassword("password123");

                userRepository.save(sampleUser);
                System.out.println(">>>> [HỆ THỐNG] Đã khởi tạo thành công User mẫu: tranghtm / password123");
            }
        };
    }
}
