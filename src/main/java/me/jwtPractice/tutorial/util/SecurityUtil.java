package me.jwtPractice.tutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

   // 이 클래스에서 발생하는 이벤트를 로그로 남기기 위한 Logger
   private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

   // 인스턴스화를 막기 위해 private으로 선언한 생성자
   private SecurityUtil() {}

   // 현재 인증된 사용자의 사용자명을 가져오는 메서드
   public static Optional<String> getCurrentUsername() {
      // SecurityContext에서 Authentication 객체를 얻음
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // Authentication 객체가 null인 경우, 즉 인증된 사용자가 없는 경우, 이를 로그로 남기고 Optional.empty를 반환
      if (authentication == null) {
         logger.debug("Security Context에 인증 정보가 없습니다.");
         return Optional.empty();
      }

      String username = null;
      // 인증 정보의 주체(principal)가 UserDetails인지 확인
      if (authentication.getPrincipal() instanceof UserDetails) {
         // 주체(principal)을 UserDetails로 캐스팅하고 사용자명을 얻음
         UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
         username = springSecurityUser.getUsername();
      }
      // 인증 정보의 주체(principal)가 String인지 확인
      else if (authentication.getPrincipal() instanceof String) {
         // 주체(principal)을 String으로 캐스팅하고 이를 사용자명으로 설정
         username = (String) authentication.getPrincipal();
      }

      // username을 포함한 Optional 객체를 반환
      return Optional.ofNullable(username);
   }
}
