package com.example.querydsl;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.querydsl.entity.QItem.item;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslDynamicQueryTests {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private JPAQueryFactory queryFactory;

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
    shopRepository.save(Shop.builder()
      .name("shopC")
      .description("shop C description")
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
        .stock(0)
        .build(),
      Item.builder()
        .shop(shopB)
        .name("itemC")
        .price(8000)
        .stock(0)
        .build(),
      Item.builder()
        .shop(shopB)
        .name("itemD")
        .price(10000)
        .stock(0)
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
  public void dynamicQueryTester() {
    List<Item> results = null;
    String name = "itemA";
    Integer price = 5000;
    Integer stock = 20;

    results = booleanBuilder(name, price, stock);
    results.forEach(System.out::println);

    results = booleanBuilder(name, null, null);
    results.forEach(System.out::println);

    results = booleanBuilder(null, price, null);
    results.forEach(System.out::println);

    results = booleanBuilder(null, null, stock);
    results.forEach(System.out::println);

    // 셋 다 null이어도 동작은 하지만 전체를 검색하므로, 막아두는 것이 좋을 것이다.
    // (조건이 들어가지 않아 where절은 존재하지 않는다.)
    results = booleanBuilder(null, null, null);
    results.forEach(System.out::println);

    // --------------------------------------------------------------------------------

    // where절 null 체크

    results = booleanExpressions(name, price, stock);
    results.forEach(System.out::println);

    results = booleanExpressions(name, null, null);
    results.forEach(System.out::println);

    results = booleanExpressions(null, price, null);
    results.forEach(System.out::println);

    results = booleanExpressions(null, null, stock);
    results.forEach(System.out::println);

    // 셋 다 null이어도 동작은 하지만 전체를 검색하므로, 막아두는 것이 좋을 것이다.
    results = booleanExpressions(null, null, null);
    results.forEach(System.out::println);

    // reusing expressions
    results = goeOrLoeOrBetween(6000, null);
    results.forEach(System.out::println);
    results = goeOrLoeOrBetween(null, 6000);
    results.forEach(System.out::println);
    results = goeOrLoeOrBetween(5500, 7500);
    results.forEach(System.out::println);
  }

  public List<Item> goeOrLoeOrBetween(
    Integer priceFloor,
    Integer priceCeil,
    Boolean isAvailable
  ) {
    return queryFactory
      .selectFrom(item)
      .where(
        priceBetween(priceFloor, priceCeil),
        isAvailable(isAvailable)
      )
      .fetch();
  }

  private  BooleanExpression isAvailable(boolean flag) {
    return flag ? item.stock.goe(1) : null;
  }

  // 최저가만 정해져있으면 최저가만 가지고 검색
  // 최고가만 정해져있으면 최고가만 가지고 검색
  // 최저가 ~ 최고가라면 그 사잇값만 가지고 검색
  public List<Item> goeOrLoeOrBetween(
    // 둘 다 있으면 사잇값
    // 하나만 있으면, floor는 최솟값, ceil은 최댓값
    Integer priceFloor,
    Integer priceCeil
  ) {
    return goeOrLoeOrBetween(priceFloor, priceCeil, true);

   /* return queryFactory
      .selectFrom(item)
      .where(priceBetween(priceFloor, priceCeil))
      .fetch();*/
  }

  // 조건을 나타내는 메서드를 만드는 것이기 때문에
  // 조건의 재활용이 간편하다.
  private BooleanExpression priceBetween(Integer floor, Integer ceil) {
    if (floor == null && ceil == null) return null;
    if (floor == null) return priceLoe(ceil);
    if (ceil == null) return priceGoe(floor);

    return item.price.between(floor, ceil);
  }

  private BooleanExpression priceLoe(Integer price) {
    return price != null ? item.price.loe(price) : null;
  }

  private BooleanExpression priceGoe(Integer price) {
    return price != null ? item.price.goe(price) : null;
  }

  public List<Item> booleanExpressions(
    String name,
    Integer price,
    Integer stock
  ) {
    return queryFactory
      .selectFrom(item)
      .where(
        // 아래와 같이 결과가 나오게끔 하고 싶은데,
        // 단 인자가 Null 이라면 들어가지 않게끔 하고 싶다.
        // -> where 메서드는 null을 인자로 받으면 무시한다.
        nameEquals(name),
        priceEquals(price),
        stockEquals(stock)
      )
      .fetch();
  }

  // 조건 자체를 격리할 수 있다.
  // 따라서, 가독성과 활용성이 좋다.
  private BooleanExpression nameEquals(String name) {
    return name != null ? item.name.eq(name) : null;

//    if (name != null) return item.name.eq(name);
//    return null;
  }

  private BooleanExpression priceEquals(Integer price) {
    return price != null ? item.price.eq(price) : null;
  }

  private BooleanExpression stockEquals(Integer stock) {
    return stock != null ? item.stock.eq(stock) : null;
  }

  public List<Item> booleanBuilder(
    String name,
    Integer price,
    Integer stock
  ) {
    // 1. BooleanBuilder
    // : 여러 조건을 엮어서 하나의 조건으로 만들어진 BooleanBuilder를 사용하는 방법
    // 생성자에 초기 조건 설정 가능
    // new BooleanBuilder(item.name.isNotNull());
    BooleanBuilder booleanBuilder = new BooleanBuilder();

    // 여태까지 누적된 조건에 대하여, 주어진 조건을 AND로 엮는다.
    if (name != null) // name이 null이 아닐 때, name과 같은 값을 조회
      // (여태까지의 조건) AND i.name = name
      booleanBuilder.and(item.name.eq(name));
    if (price != null)
      // (여태까지의 조건) AND i.price = price
      booleanBuilder.and(item.price.eq(price));
    if (stock != null)
      // (여태까지의 조건) AND i.stock = stock
      booleanBuilder.and(item.stock.eq(stock));
    // 모두가 null이 아니면 i.name = name AND i.price = price AND i.stock = stock

    return queryFactory
      .selectFrom(item)
      .where(booleanBuilder)
      .fetch();
  }
}


















