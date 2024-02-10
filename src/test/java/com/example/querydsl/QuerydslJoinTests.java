package com.example.querydsl;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
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
import static com.example.querydsl.entity.QShop.shop;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslJoinTests {
  // @Autowired
  // : 의존성 주입이 되어야 하는 속성임을 나타낸다.
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private JPAQueryFactory queryFactory;
  @Autowired
  // entityManager를 만들어주는 인터페이스
  private EntityManagerFactory managerFactory;
  // Jpa에서 몇가지 속성을 알아보고 싶을 때 사용하는 utility 클래스
  private PersistenceUnitUtil unitUtil;

  // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
  @BeforeEach
  public void beforeEach() {
    // unitUtil을 가져올 수 있다.
    unitUtil = managerFactory.getPersistenceUnitUtil();
//    Item temp = Item.builder().build();
//    // isLoaded
//    // : 실제 entity의 shop이 들어가 있는지
//    // 아님 shop을 가져오기 위한 proxy 객체가 들어있는지 확인하기 위한 메소드
//    unitUtil.isLoaded(temp.getShop());

    Shop shopA = shopRepository.save(Shop.builder()
      .name("shopA")
      .description("shop A description")
      .build());
    Shop shopB = shopRepository.save(Shop.builder()
      .name("shopB")
      .description("shop B description")
      .build());
    Shop shopC = shopRepository.save(Shop.builder()
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

  // JOIN 하는 방법
  @Test
  public void regularJoins() {
    List<Item> foundList = queryFactory
      .selectFrom(item)
      // INNER JOIN
       .join(item.shop)
      // LEFT JOIN
      // .leftJoin(item.shop)
      // RIGHT JOIN
      // .rightJoin(item.shop)
      .fetch();

    for (Item found: foundList) {
      System.out.println(found);
    }
  }


  // 영속성 컨텍스트가 위에 있는 로직을 통해 shop 데이터를 주고 있으므로
  // 일반 join을 했음에도 불구하고 shop 데이터가 load 되었다고 나왔다.
  // so, 영속성 컨텍스트를 초기화하기 위해 EntityManager를 추가했다.
  @Autowired
  private EntityManager entityManager;

  // fetch join
  // : 연관된 데이터를 한꺼번에 가져오기 (N+1 문제 방지)
  @Test
  public void fetchJoin() {
    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    // 그냥 join은 연관 데이터를 불러오지는 않는다. (연관된 데이터를 활용해서 where절에서 사용하기 위해 사용한다.)
    Item found = queryFactory
      .selectFrom(item)
      .join(item.shop)
      .where(item.name.eq("itemA"))
      .fetchOne();
    // 검색한 데이터의 Shop 데이터는 가져와지지 않은 상태
    assertFalse(unitUtil.isLoaded(found.getShop())); // false 성공

    found = queryFactory
      .selectFrom(item)
      .join(item.shop)
      // Fetch Join으로 변경
      .fetchJoin()
      .where(item.name.eq("itemB"))
      .fetchOne();
    // 검색한 데이터의 Shop 데이터는 가져와진 상태
    assertTrue(unitUtil.isLoaded(found.getShop())); // true 성공
  }

  /*
  SELECT count(*), max(item.price) FROM Item item;
  */

  // 집계 함수
  @Test
  public void aggregate() {
    // Querydsl의 Tuple
    // : 문자열로 속성을 표현하지 않도록 사용
    Tuple result = queryFactory
      // 집계하고 싶은 속성의 집계함수 메서드를 호출하여 select에 추가
      // item.(속성).(집계함수)()
      .select(
        item.count(),
        item.price.avg(),
        item.price.max(),
        item.stock.sum()
      )
      .from(item)
      // 집계 함수는 결과가 하나의 행만 나와서 fetchOne을 써도 된다.
      .fetchOne();

    // Tuple은 조회했던 QType의 속성 및 집계를 기준으로 데이터 회수 가능
    assertEquals(6, result.get(item.count()));
    assertEquals(175, result.get(item.stock.sum()));
    // 없는건 null로 반환
    assertNull(result.get(item.stock));

    // Gtoup By
    List<Tuple> results = queryFactory
      .select(
        shop.name,
        item.count(),
        item.price.avg(),
        item.stock.sum()
      )
      .from(item)
      .join(item.shop)
      .groupBy(shop.name)
      .fetch();

    for (Tuple tuple: results) {
      System.out.printf(
        "%s: %d, %.2f (%d)%n",
        tuple.get(shop.name),
        tuple.get(item.count()),
        tuple.get(item.price.avg()),
        tuple.get(item.stock.sum())
      );
    }
  }

}

