# QueryDsl

- 2024.0206 ~ 0207 `12주차`
- 0206 Querydsl
- 0207 Querydsl - .and() & .or(), Tuple, Projection & QueryProjection & With Spring Data Jpa


## 스팩


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


<새로 생성한 파일>
- QuerydslJoinTests: Join 연습.

</details>

2월 7일
- QuerydslProjTests
- ItemDto
- ItemDtoProj
- ItemQuerydslRepo
- ItemQuerydslRepoImpl
- ItemController
- ItemRepository

# 스펙

# Key Point

# Github
[likelion-backend-8-querydsl](https://github.com/edujeeho0/likelion-backend-8-querydsl)