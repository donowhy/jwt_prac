package me.jwtPractice.tutorial.repository;

import me.jwtPractice.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   // EntityGraph는 Lazy조회가 아닌 Eager로 조회함과 동시에 authorities 정보를 갖고옴
   @EntityGraph(attributePaths = "authorities")
   Optional<User> findOneWithAuthoritiesByUsername(String username);

   Optional<User> findByEmail(String email);
}
