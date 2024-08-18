package com.example.querydsl.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @Configuration
// 설정 클래스를 나타내는 어노테이션
@Configuration
// @EnableJpaAuditing
// : JPA 감사 기능을 활성화하는 어노테이션
// (감사 기능: 데이터 생성, 변경 시간 등을 자동으로 기록해주는 기능)
@EnableJpaAuditing
//@RequiredArgsConstructor
public class JpaConfig {
//  private final EntityManager entityManager; // @RequiredArgsConstructor 사용 시 가능한 의존성 주입

  // @Bean
  // : 외부에 있는 라이브러리 결과물을 Bean 객체로 등록하고 싶다면 @Configuration 클래스에서 Bean 어노테이션 사용
  @Bean
  // JpaQueryFactory
  // : EntityManager를 받아서 Jpa를 사용해 DB를 조회하는 Querydsl 모듈 중 하나
  public JPAQueryFactory jpaQueryFactory(
    EntityManager entityManager
  ) {
    return new JPAQueryFactory(entityManager);
  }
}
