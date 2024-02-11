package com.example.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class ItemDtoProj {
  public String name;
  public Integer cost;
  public Integer stock;

  // @QueryProjection
  // : 해당 생성자를 이용하여 Qurydsl의 Query 결과를 Projection하는데 활용하겠다.란 어노테이션
  // QDto를 만들고, 그 생성자를 사용해 데이터를 Projection할 수 있다.
  @QueryProjection
  public ItemDtoProj(
    String name,
    Integer cost,
    Integer stock
  ) {
    this.name = name;
    this.cost = cost;
    this.stock = stock;
  }
}
// 단점
// Querydsl과 DTO의 결합력(Coupling)이 강해진다.
// Querydsl에 대한 의존도가 높아진다.