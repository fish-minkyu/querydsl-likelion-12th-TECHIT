# QueryDsl

- 2024.0206 ~ 0207 `12주차`
- 0206 Querydsl - Querydsl 초기 설정, fetchOne(), where절 조건 메서드
- 0207 Querydsl - .and() & .or(), Tuple, Projection & QueryProjection & With Spring Data Jpa


## 스팩

- Spring Boot 3.2.2
- Spring Web
- Lombok
- querydsl 5.1.0
- Spring Data Jpa
- SQLite 3.41.2.2
- H2
- Starter Test


## 히스토리

<details>
<summary><strong>02/06 ~ 02/07 2교시 Querydsl Basics</strong></summary>

<새로 생성한 파일>
- BaseEntity: Entity끼리 공통된 속성을 묶기 위해 만듬.
- Item: Item Entity.
- Shop: Shop Entity.
- ItemRepository: JPA를 사용하여 Item Entity와 DB간의 상호작용을 관리.
- ShopRepository: JPA를 사용하여 Shop Entity와 DB간의 상호작용을 관리.
- QuerydslRepo: Querydsl을 이용하여 간단한 쿼리 작성.
- JpaConfig: EntityManager를 주입 받아 JpaQueryFactory를 생성하고 Bean 등록.
- TestController: QuerydslRepo의 컨트롤러.
- QuerydslQTypeTests: fetchOne() 공부.
- QuerydslQueryTests: Where 절에 사용할 조건들 공부.

</details>

<details>
<summary><strong>02/07 3교시 ~ Querydsl Extra</strong></summary>

<수정한 파일>
- QuerydslQueryTests: or, and 조건 쿼리 연습.
- ItemDto: fromEntity 메소드 만듬.

<새로 생성한 파일>
- QuerydslJoinTests: Join 연습.
- QuerydslProjTests: Projection 공부.
- ItemDto: Projection을 활용하기 위해 DTO를 만들었다.
- ItemDtoProj: @QueryProjection을 위해 DTO를 만들었다.
- QuerydslDynamicQueryTests: Dynamic Query를 연습하기 위해 만들었다.
- ItemController: Querydsl 커스텀 기능을 구현한 메소드를 사용하기 위한 컨트롤러다.
- ItemSearchParams: 검색 기준을 담기 위한 DTO의 일종이다.
- ItemQuerydslRepo: Querydsl 커스텀 기능을 구현하기 위한 인터페이스다.
- ItemQuerydslRepoImpl: ItemQuerydslRepo의 구현 클래스다. (따로 설정이 없는 한 뒤에 Impl은 고정이다.)

</details>

## Key Point

1. Querydsl 초기 설정. (EntityManager & JpaConfig)
2. where절 조건문.
3. 동적 쿼리.  
    3-1. BooleanBuilder  
    3-2. BooleanExpression(where 다중 조건)
4. Querydsl 커스텀 메소드.(Pageable)