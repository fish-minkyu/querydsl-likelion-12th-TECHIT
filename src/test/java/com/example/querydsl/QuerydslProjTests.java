package com.example.querydsl;

import com.example.querydsl.dto.ItemDto;
import com.example.querydsl.dto.ItemDtoProj;
import com.example.querydsl.dto.QItemDtoProj;
import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.querydsl.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslProjTests {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private JPAQueryFactory queryFactory;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private EntityManagerFactory managerFactory;
  private PersistenceUnitUtil unitUtil;

  // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
  @BeforeEach
  public void beforeEach() {
    Shop shopA = shopRepository.save(Shop.builder()
      .name("shopA")
      .description("shop A description")
      .build());
    Shop shopB = shopRepository.save(Shop.builder()
      .name("shopB")
      .description("shop B description")
      .build());

    itemRepository.saveAll(List.of(
      Item.builder()
        .shop(shopA)
        .name("itemA")
        .price(5000)
        .stock(20)
        .build(),
      Item.builder()
        .shop(shopA)
        .name("itemB")
        .price(6000)
        .stock(30)
        .build(),
      Item.builder()
        .shop(shopB)
        .name("itemC")
        .price(8000)
        .stock(40)
        .build(),
      Item.builder()
        .shop(shopB)
        .name("itemD")
        .price(10000)
        .stock(50)
        .build(),
      Item.builder()
        .name("itemE")
        .price(5500)
        .stock(10)
        .build(),
      Item.builder()
        .price(7500)
        .stock(25)
        .build()
    ));
  }

  @Test
  public void noProjection() {
    // 하나의 속성을 조회할때는, 해당 속성의 자료형으로 그대로 반환이 된다.
    String name = queryFactory
      // select에 넣는 속성에 따라 fetch들의 반환형이 바뀐다.
      .select(item.name)
      .from(item)
      .where(item.id.eq(4L))
      .fetchOne();

    assertEquals("itemD", name);

    // 집계 함수도 활용이 가능하다.
    Long count = queryFactory
      .select(item.count())
      .from(item)
      .fetchOne();

    assertEquals(6L, count);

    // fetch는 리스트로
    List<String> names = queryFactory
      .select(item.name)
      .from(item)
      .fetch();

    for (String foundName: names) {
      System.out.println(foundName);
    }

    // 단일 속성이 아니면 Tuple
    Tuple resultTuple = queryFactory
      .select(item.price, item.stock)
      .from(item)
      .where(item.name.eq("itemB"))
      .fetchOne();
    assertEquals(6000, resultTuple.get(item.price));
    assertEquals(30, resultTuple.get(item.stock));
    assertNull(resultTuple.get(item.name));

    List<Tuple> tuples = queryFactory
      .select(item.price, item.stock)
      .from(item)
      .fetch();

    for (Tuple tuple: tuples) {
      System.out.printf(
        "%d (%d)%n",
        tuple.get(item.price),
        tuple.get(item.stock)
      );
    }
  }

  /*
    SELECT new com.example.jpa.dto.ItemDto() ...
  */
  @Test
  // dto로 projection을 해보자
  public void dtoProjection() {
    List<ItemDto> itemDtoList = null;

    // Projections.bean: Setter 기반 Projection
    itemDtoList = queryFactory
      .select(Projections.bean(
        ItemDto.class,
        item.name,
        item.price.as("cost"),
        item.stock
      ))
      .from(item)
      .where(item.name.isNotNull())
      .fetch();
    itemDtoList.forEach(System.out::println);

    // Projections.fields: 속성 기반 Projection (Setter 없이 작동이 가능하다.)
    // (단, Dto의 속성 기반으로 작동을 하여 Querydsl과 Dto의 속성이 다르다면 null이 나온다.)
    itemDtoList = queryFactory
      .select(Projections.fields(
        ItemDto.class,
        item.name,
        // as로 alias 해주면 다른 속성 이름 사용 가능
        item.price.as("cost"),
        item.stock
      ))
      .from(item)
      .where(item.name.isNotNull())
      .fetch();
    itemDtoList.forEach(System.out::println);

    // Projections.constructor: 생성자 기반 Projections
    itemDtoList = queryFactory
      .select(Projections.constructor(
        ItemDto.class,
        // 인자를 넣을 수 있는 형태의 생성자를
        // 찾아서 실행함으로서 객체를 만든다.
        // (당연히, 생성자에 들어가는 인자의 순서가 일치하여야 한다.)
        item.name,
        item.price,
        item.stock
      ))
      .from(item)
      .where(
        item.price.isNotNull(),
        item.stock.isNotNull()
      )
      .fetch();
    itemDtoList.forEach(System.out::println);
  }

  @Test
  public void queryProjection() {
    List<ItemDtoProj> itemDtoList = queryFactory
      // 만들어진 QDto의 생성자를 호출함으로서
      // 결과를 Dto로 받을 수 있다.
      .select(new QItemDtoProj(
        item.name,
        item.price,
        item.stock
      ))
      .from(item)
      .where(item.name.isNotNull())
      .fetch();

    itemDtoList.forEach(System.out::println);
  }
}
