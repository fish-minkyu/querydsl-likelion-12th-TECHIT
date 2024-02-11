package com.example.querydsl.repo;

import com.example.querydsl.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository
  extends JpaRepository<Item, Long>, ItemQuerydslRepo {
/*  // Intellij가 여기에 들어갈 문자열은 JPQL임을 안다.
  @Query("SELECT i FROM Item i") // 인텔리제이가 jpql 문법 검사를 해준다.
  List<Item> findWithJpql();*/


}
