package com.example.querydsl.dto;

import com.example.querydsl.entity.Item;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class ItemDto {
  public String name;
  public Integer cost;
  public Integer stock;

  public ItemDto(
    String name,
    Integer cost,
    Integer stock
  ) {
    this.name = name;
    this.cost = cost;
    this.stock = stock;
  }

  public static ItemDto fromEntity(Item entity) {
    return new ItemDto(
      entity.getName(),
      entity.getPrice(),
      entity.getStock()
    );
  }
}
