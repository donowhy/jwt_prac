package me.jwtPractice.tutorial.service;

import java.util.Collections;

import me.jwtPractice.tutorial.dto.UserDto;
import me.jwtPractice.tutorial.entity.Authority;
import me.jwtPractice.tutorial.entity.User;
import me.jwtPractice.tutorial.exception.DuplicateMemberException;
import me.jwtPractice.tutorial.exception.NotFoundMemberException;
import me.jwtPractice.tutorial.repository.UserRepository;
import me.jwtPractice.tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // UserRepository와 PasswordEncoder를 주입받는 생성자
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 등록을 처리하는 메서드
    @Transactional
    public UserDto signup(UserDto userDto) {
        // 이미 등록된 사용자인지 체크
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 사용자 권한 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 사용자 정보 설정
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))  // 비밀번호 암호화
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))  // 권한 설정
                .activated(true)  // 활성화 상태로 설정
                .build();

        // 사용자 정보 저장 후 반환
        return UserDto.from(userRepository.save(user));
    }

    // 사용자와 그의 권한을 가져오는 메서드
    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    // 현재 인증된 사용자와 그의 권한을 가져오는 메서드
    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                // 현재 인증된 사용자의 username을 가져온다
                SecurityUtil.getCurrentUsername()
                        // username을 기반으로 사용자 정보와 그의 권한을 가져온다
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        // 사용자 정보가 없을 경우 예외를 발생시킨다
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
