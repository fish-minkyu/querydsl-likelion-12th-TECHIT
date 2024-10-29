package com.example.querydsl.repo;


import com.example.querydsl.dto.ItemSearchParams;
import com.example.querydsl.entity.Item;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.querydsl.entity.QItem.item;

// @Repository를 안붙여도 Bean으로서 관리가 된다.
@Slf4j
@RequiredArgsConstructor
public class ItemQuerydslRepoImpl implements ItemQuerydslRepo {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Item> searchDynamic(ItemSearchParams searchParams) {

    log.info(searchParams.toString());
    return queryFactory
        .selectFrom(item)
        .where(
            nameEquals(searchParams.getName()),
            priceBetween(searchParams.getPriceFloor(), searchParams.getPriceCeil())
        )
        .fetch();
  }

  @Override
  public Page<Item> searchDynamic(ItemSearchParams searchParams, Pageable pageable) {
    // pageable은 몇번째 페이지인지, 한 페이지 당 몇개의 데이터가 있는지, offset에 대한 정보가 있다.

    log.info(searchParams.toString());
    // Page를 만드는데 필요한 3가지 정보
    // 1. (Offset, Limit 으로 페이지 처리 된) 실제 데이터
    List<Item> content = queryFactory
      .selectFrom(item)
      .where(
          nameEquals(searchParams.getName()),
          priceBetween(searchParams.getPriceFloor(), searchParams.getPriceCeil())
          )
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();
    // 2. Pageable에 대한 데이터 (몇번째 페이지, 페이지 당 내용)
    //  -> 인자로 주어진다.
    // 파라미터로 받는 Pageable 객체에 몇번째 페이지(index), 크기(size), offset에 대한 정보가 다 있다.

/*    // 3. 총 갯수 (총 페이지를 위해서 필요한 정보)
    Long count = queryFactory
      .select(item.count())
      .from(item)
      .fetchOne();
    // PageImpl로 반환
    return new PageImpl<>(content, pageable, count);
*/

    // 3+@. 총 갯수를 반환할 수 있는 방법
    JPAQuery<Long> countQuery = queryFactory
      .select(item.count())
      .from(item);
    // PageableExcutionUtils.getPage()
    // 1. 첫번째 페이지
    // 2. (페이지 당 갯수를 채우지 못한) 마지막 페이지
    // 의 경우에는 Count 쿼리를 실행하지 않는다. <- 즉, 쿼리를 1번 덜 실행하여 서버의 부담을 덜어준다.
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    // countQuery::fetchOne
    // : fetchOne 메서드를 호출하면 총 item의 개수가 몇개인지 확인할 수 있다.
  }

  // ---------------------------------------
  // where 다중 조건으로 동적쿼리 구현

  private BooleanExpression nameEquals(String name) {
    return name != null ? item.name.eq(name) : null;
  }

  private BooleanExpression priceBetween(Integer floor, Integer ceil) {
    if (floor == null && ceil == null) return null;
    if (floor == null) return priceLoe(ceil);
    if (ceil == null) return priceGoe(floor);

    return item.price.between(floor, ceil);
  }

  // 최고가
  private BooleanExpression priceLoe(Integer price) {
    return price != null ? item.price.loe(price) : null;
  }

  // 최저가
  private BooleanExpression priceGoe(Integer price) {
    return price != null ? item.price.goe(price) : null;
  }
}



