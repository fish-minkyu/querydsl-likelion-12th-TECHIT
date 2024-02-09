package com.example.querydsl.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 설정 클래스를 나타내는 어노테이션
@Configuration
// JPA 감사 기능을 활성화하는 어노테이션
// (감사 기능: 데이터 생성, 변경 시간 등을 자동으로 기록해주는 기능)
@EnableJpaAuditing
//@RequiredArgsConstructor
public class JpaConfig {
//  private final EntityManager entityManager;

  // 외부에 있는 라이브러리 결과물을 Bean 객체로 등록하고 싶다면 Bean 어노테이션 사용
  @Bean
  public JPAQueryFactory jpaQueryFactory(
    EntityManager entityManager
  ) {
    return new JPAQueryFactory(entityManager);
  }
}
