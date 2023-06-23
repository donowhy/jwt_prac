package me.jwtPractice.tutorial.service;

import me.jwtPractice.tutorial.entity.User;
import me.jwtPractice.tutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
   // UserRepository를 주입받음
   private final UserRepository userRepository;

   public CustomUserDetailsService(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   // 사용자 이름으로 사용자 정보를 가져오는 메서드
   @Override
   @Transactional
   public UserDetails loadUserByUsername(final String username) {
      // userRepository를 통해 사용자 정보를 가져오고, 그 정보를 가지고 createUser 메서드를 호출
      // 만약 사용자를 찾을 수 없으면 예외를 발생시킴
      return userRepository.findOneWithAuthoritiesByUsername(username)
              .map(user -> createUser(username, user))
              .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
   }

   // User를 UserDetails로 변환하는 메서드
   private org.springframework.security.core.userdetails.User createUser(String username, User user) {
      // 사용자가 활성화되어 있는지 확인하고, 활성화되어 있지 않다면 예외를 발생시킴
      if (!user.isActivated()) {
         throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
      }

      // 사용자의 권한들을 GrantedAuthority의 리스트로 변환
      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
              .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
              .collect(Collectors.toList());

      // 변환된 권한 리스트를 가지는 UserDetails 객체를 생성하여 반환
      return new org.springframework.security.core.userdetails.User(user.getUsername(),
              user.getPassword(),
              grantedAuthorities);
   }
}
