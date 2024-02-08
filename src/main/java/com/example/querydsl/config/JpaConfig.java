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

  @Bean
  public JPAQueryFactory jpaQueryFactory(
    EntityManager entityManager
  ) {
    return new JPAQueryFactory(entityManager);
  }
}
