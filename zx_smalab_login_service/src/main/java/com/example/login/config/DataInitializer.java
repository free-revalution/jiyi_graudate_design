package com.example.login.config;

import com.example.login.entity.UserAuthorize;
import com.example.login.repository.UserAuthorizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserAuthorizeRepository userAuthorizeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String encodedPw = passwordEncoder.encode("123456");

        initUser(1001L, "admin", "phone", encodedPw);
        initUser(1001L, "admin", "student_id", encodedPw);

        initUser(2001L, "T001", "teacher_id", encodedPw);
        initUser(2001L, "13800000001", "phone", encodedPw);

        initUser(2002L, "T002", "teacher_id", encodedPw);
        initUser(2002L, "13800000002", "phone", encodedPw);

        initUser(3001L, "20240001", "student_id", encodedPw);
        initUser(3001L, "13900000001", "phone", encodedPw);

        initUser(3002L, "20240002", "student_id", encodedPw);
        initUser(3002L, "13900000002", "phone", encodedPw);

        log.info("测试用户数据初始化完成");
    }

    private void initUser(Long userId, String identifier, String identityType, String credential) {
        boolean exists = userAuthorizeRepository
                .findByIdentifierAndIdentityType(identifier, identityType).isPresent();
        if (!exists) {
            UserAuthorize user = UserAuthorize.builder()
                    .userId(userId)
                    .userStatus("1")
                    .identityType(identityType)
                    .identifier(identifier)
                    .credential(credential)
                    .loginStatus("2")
                    .createdTime(new Date())
                    .modifiedTime(new Date())
                    .build();
            userAuthorizeRepository.save(user);
        }
    }
}
