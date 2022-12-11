package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuGroupRestAssured.메뉴_그룹_등록되어_있음;
import static kitchenpos.acceptance.MenuRestAssured.메뉴_등록되어_있음;
import static kitchenpos.acceptance.OrderRestAssured.주문_등록되어_있음;
import static kitchenpos.acceptance.ProductRestAssured.상품_등록되어_있음;
import static kitchenpos.acceptance.TableGroupRestAssured.단체_지정_등록되어_있음;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_등록되어_있음;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_목록_조회_요청;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_방문한_손님_수_변경_요청;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_빈_상태_변경_요청;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("주문 테이블 관련 인수 테스트")
class TableAcceptanceTest extends AcceptanceTest {

    private OrderTable 비어있지않은_주문_테이블1;
    private OrderTable 비어있지않은_주문_테이블2;
    private OrderTable 비어있는_주문_테이블1;
    private OrderTable 비어있는_주문_테이블2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        비어있지않은_주문_테이블1 = OrderTable.of(null, 2, false);
        비어있지않은_주문_테이블2 = OrderTable.of(null, 3, false);

        비어있는_주문_테이블1 = OrderTable.of(null, 2, true);
        비어있는_주문_테이블2 = OrderTable.of(null, 2, true);

    }

    /**
     * When 주문 테이블 생성 요청
     * Then 주문 테이블 생성됨
     */
    @DisplayName("주문 테이블을 생성한다.")
    @Test
    void create() {
        ExtractableResponse<Response> response = 주문_테이블_생성_요청(비어있지않은_주문_테이블1);

        주문_테이블_생성됨(response);
    }

    /**
     * Given 주문 테이블 여러개 등록되어 있음
     * When 주문 테이블 목록 조회 요청
     * Then 주문 테이블 목록 조회됨
     * Then 주문 테이블 목록에 등록된 주문 테이블 포함됨
     */
    @DisplayName("주문 태이블 목록을 조회한다.")
    @Test
    void list() {
        ExtractableResponse<Response> createResponse1 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블1);
        ExtractableResponse<Response> createResponse2 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블2);

        ExtractableResponse<Response> listResponse = 주문_테이블_목록_조회_요청();

        주문_테이블_목록_조회됨(listResponse);
        주문_테이블_목록에_등록된_주문_테이블_포함됨(listResponse, Arrays.asList(createResponse1, createResponse2));
    }

    /**
     * When 등록되지 않은 주문 테이블 빈 상태 변경 요청
     * Then 빈 상태 변경 요청 실패됨
     */
    @DisplayName("등록되지 않은 주문 테이블의 빈 상태를 변경할 수 없다.")
    @Test
    void changeEmptyFail() {
        ExtractableResponse<Response> response = 주문_테이블_빈_상태_변경_요청(1L, OrderTable.of(1L, 3, false));

        빈_상태_변경_요청_실패됨(response);
    }

    /**
     * Given 주문 테이블 여러개 등록되어 있음
     * And 단체 지정 등록되어 있음
     * When 주문 테이블 빈 상태 변경 요청
     * Then 빈 상태 변경 요청 실패됨
     */
    @DisplayName("단체 지정된 주문 테이블의 빈 상태를 변경할 수 없다.")
    @Test
    void changeEmptyFail2() {
        OrderTable 등록된_빈_주문_테이블1 = 주문_테이블_등록되어_있음(비어있는_주문_테이블1).as(OrderTable.class);
        OrderTable 등록된_빈_주문_테이블2 = 주문_테이블_등록되어_있음(비어있는_주문_테이블2).as(OrderTable.class);

        List<OrderTable> 주문_테이블_목록 = Arrays.asList(등록된_빈_주문_테이블1, 등록된_빈_주문_테이블2);
        단체_지정_등록되어_있음(TableGroup.of(1L, 주문_테이블_목록));

        OrderTable 변경할_주문_테이블 =
                OrderTable.of(등록된_빈_주문_테이블1.getId(), 등록된_빈_주문_테이블1.getNumberOfGuests(), !등록된_빈_주문_테이블1.isEmpty());
        ExtractableResponse<Response> response = 주문_테이블_빈_상태_변경_요청(변경할_주문_테이블.getId(), 변경할_주문_테이블);

        빈_상태_변경_요청_실패됨(response);
    }

    /**
     * Given 메뉴 그룹 등록되어 있음
     * And 상품 등록되어 있음
     * And 메뉴 등록되어 있음
     * And 주문 테이블 등록되어 있음
     * And 주문(조리) 등록되어 있음
     * When 주문 테이블 빈 상태 변경 요청
     * Then 빈 상태 변경 요청 실패됨
     */
    @DisplayName("주문 상태가 조리이면 주문 테이블의 빈 상태를 변경할 수 없다.")
    @Test
    void changeEmptyFail3() {
        // Given 메뉴 그룹 등록되어 있음
        MenuGroup 두마리메뉴 = 메뉴_그룹_등록되어_있음(MenuGroup.of(1L, "두마리메뉴")).as(MenuGroup.class);

        // And 상품 등록되어 있음
        Product 후라이드 = 상품_등록되어_있음(
                Product.of(1L, "후라이드", BigDecimal.valueOf(16_000))
        ).as(Product.class);

        // And 메뉴 등록되어 있음
        List<MenuProduct> 메뉴상품목록 = Arrays.asList(MenuProduct.of(1L, 1L, 후라이드.getId(), 2));
        Menu 후라이드치킨 = 메뉴_등록되어_있음(
                Menu.of(1L, "후라이드치킨", BigDecimal.valueOf(16_000), 두마리메뉴.getId(), 메뉴상품목록)
        ).as(Menu.class);

        // And 주문 테이블 등록되어 있음
        OrderTable 등록된_주문_테이블 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블1).as(OrderTable.class);

        // And 주문(조리) 등록되어 있음
        List<OrderLineItem> 주문항목 = Arrays.asList(OrderLineItem.of(후라이드치킨.getId(), 2));
        주문_등록되어_있음(Order.of(등록된_주문_테이블.getId(), 주문항목)).as(Order.class);

        // When 주문 테이블 빈 상태 변경 요청
        OrderTable 변경할_주문_테이블 =
                OrderTable.of(등록된_주문_테이블.getId(), 등록된_주문_테이블.getNumberOfGuests(), !등록된_주문_테이블.isEmpty());
        ExtractableResponse<Response> response = 주문_테이블_빈_상태_변경_요청(변경할_주문_테이블.getId(), 변경할_주문_테이블);

        // Then 빈 상태 변경 요청 실패됨
        빈_상태_변경_요청_실패됨(response);
    }

    /**
     * Given 메뉴 그룹 등록되어 있음
     * And 상품 등록되어 있음
     * And 메뉴 등록되어 있음
     * And 주문 테이블 등록되어 있음
     * And 주문 등록되어 있음
     * And 주문 상태(식사) 변경되어 있음
     * When 주문 테이블 빈 상태 변경 요청
     * Then 빈 상태 변경 요청 실패됨
     */
    @DisplayName("주문 상태가 식사이면 주문 테이블의 빈 상태를 변경할 수 없다.")
    @Test
    void changeEmptyFail4() {
        // Given 메뉴 그룹 등록되어 있음
        MenuGroup 두마리메뉴 = 메뉴_그룹_등록되어_있음(MenuGroup.of(1L, "두마리메뉴")).as(MenuGroup.class);

        // And 상품 등록되어 있음
        Product 후라이드 = 상품_등록되어_있음(
                Product.of(1L, "후라이드", BigDecimal.valueOf(16_000))
        ).as(Product.class);

        // And 메뉴 등록되어 있음
        List<MenuProduct> 메뉴상품목록 = Arrays.asList(MenuProduct.of(1L, 1L, 후라이드.getId(), 2));
        Menu 후라이드치킨 = 메뉴_등록되어_있음(
                Menu.of(1L, "후라이드치킨", BigDecimal.valueOf(16_000), 두마리메뉴.getId(), 메뉴상품목록)
        ).as(Menu.class);

        // And 주문 테이블 등록되어 있음
        OrderTable 등록된_주문_테이블 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블1).as(OrderTable.class);

        // And 주문 등록되어 있음
        List<OrderLineItem> 주문항목 = Arrays.asList(OrderLineItem.of(후라이드치킨.getId(), 2));
        Order 주문 = 주문_등록되어_있음(Order.of(등록된_주문_테이블.getId(), 주문항목)).as(Order.class);

        // And 주문 상태(식사) 변경되어 있음
        Order 식사상태_주문 = Order.of(주문.getId(), 주문.getOrderTableId(), 주문.getOrderLineItems());
        식사상태_주문.setOrderStatus(OrderStatus.MEAL.name());
        OrderRestAssured.주문_상태_변경_요청(식사상태_주문.getId(), 식사상태_주문);

        // When 주문 테이블 빈 상태 변경 요청
        OrderTable 변경할_주문_테이블 =
                OrderTable.of(등록된_주문_테이블.getId(), 등록된_주문_테이블.getNumberOfGuests(), !등록된_주문_테이블.isEmpty());
        ExtractableResponse<Response> response = 주문_테이블_빈_상태_변경_요청(변경할_주문_테이블.getId(), 변경할_주문_테이블);

        // Then 빈 상태 변경 요청 실패됨
        빈_상태_변경_요청_실패됨(response);
    }

    /**
     * Given 주문 테이블(단체 지정x, 주문 상태가 조리x 또는 식사x) 등록되어 있음
     * When 주문 테이블 빈 상태 변경 요청
     * Then 빈 상태 변경됨
     */
    @DisplayName("주문 테이블의 빈 상태를 변경할 수 있다.")
    @Test
    void changeEmpty() {
        OrderTable 주문_테이블 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블1).as(OrderTable.class);

        OrderTable 변경할_주문_테이블 = OrderTable.of(주문_테이블.getId(), 주문_테이블.getNumberOfGuests(), !주문_테이블.isEmpty());
        ExtractableResponse<Response> response = 주문_테이블_빈_상태_변경_요청(주문_테이블.getId(), 변경할_주문_테이블);

        빈_상태_변경됨(response, 변경할_주문_테이블.isEmpty());
    }

    /**
     * When 방문한 손님 수가 0보다 작은 값으로 주문 테이블의 방문한 손님 수 변경 요청
     * Then 방문한 손님 수 변경 실패됨
     */
    @DisplayName("방문한 손님 수가 0보다 작은경우 주문 테이블의 방문한 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsFail() {
        OrderTable 방문한_손님_수가_0보다_작은_주문_테이블 = OrderTable.of(1L, -1, false);
        ExtractableResponse<Response> response =
                주문_테이블_방문한_손님_수_변경_요청(1L, 방문한_손님_수가_0보다_작은_주문_테이블);

        방문한_손님_수_변경_실패됨(response);
    }

    /**
     * When 등록되지 않은 주문 테이블에 방문한 손님 수 변경 요청
     * Then 방문한 손님 수 변경 실패됨
     */
    @DisplayName("등록되지 않은 주문 테이블의 방문한 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsFail2() {
        OrderTable 등록되지_않은_주문_테이블 = OrderTable.of(1L, 10, false);
        ExtractableResponse<Response> response = 주문_테이블_방문한_손님_수_변경_요청(1L, 등록되지_않은_주문_테이블);

        방문한_손님_수_변경_실패됨(response);
    }

    /**
     * Given 주문 테이블(빈 상태) 등록되어 있음
     * When 빈 상태인 주문 테이블의 방문한 손님 수 변경 요청
     * Then 방문한 손님 수 변경 실패됨
     */
    @DisplayName("등록된 주문 테이블이 빈 상태이면 방문한 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsFail3() {
        OrderTable 빈_상태_주문_테이블 =
                주문_테이블_등록되어_있음(OrderTable.of(null, 0, true)).as(OrderTable.class);

        OrderTable 방문한_손님_수_변경한_주문_테이블 = OrderTable.of(빈_상태_주문_테이블.getId(), 10, false);
        ExtractableResponse<Response> response =
                주문_테이블_방문한_손님_수_변경_요청(빈_상태_주문_테이블.getId(), 방문한_손님_수_변경한_주문_테이블);

        방문한_손님_수_변경_실패됨(response);
    }

    /**
     * Given 주문 테이블(방문한 손님 수 0이상, 등록된 주문 테이블, 빈 상태 x)이 등록되어 있음
     * When 방문한 손님 수 변경 요청
     * Then 방문한 손님 수 변경됨
     */
    @DisplayName("주문 테이블의 방문한 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable 주문_테이블 = 주문_테이블_등록되어_있음(비어있지않은_주문_테이블1).as(OrderTable.class);

        OrderTable 방문한_손님_수_변경_주문_테이블 = OrderTable.of(주문_테이블.getId(), 10, 주문_테이블.isEmpty());
        ExtractableResponse<Response> response = 주문_테이블_방문한_손님_수_변경_요청(주문_테이블.getId(), 방문한_손님_수_변경_주문_테이블);

        방문한_손님_수_변경됨(response, 방문한_손님_수_변경_주문_테이블.getNumberOfGuests());
    }

    private void 주문_테이블_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 주문_테이블_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 주문_테이블_목록에_등록된_주문_테이블_포함됨(ExtractableResponse<Response> listResponse,
                                           List<ExtractableResponse<Response>> createResponses) {
        List<OrderTable> orderTables = listResponse.jsonPath().getList(".", OrderTable.class);
        List<OrderTable> createdOrderTables = createResponses.stream()
                .map(it -> it.as(OrderTable.class))
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(orderTables).hasSize(2),
                () -> assertThat(orderTables).containsAll(createdOrderTables)
        );
    }

    private void 빈_상태_변경_요청_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 빈_상태_변경됨(ExtractableResponse<Response> response, boolean empty) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(OrderTable.class).isEmpty()).isEqualTo(empty)
        );
    }

    private void 방문한_손님_수_변경_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 방문한_손님_수_변경됨(ExtractableResponse<Response> response, int numberOfGuests) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(OrderTable.class).getNumberOfGuests()).isEqualTo(numberOfGuests)
        );
    }
}
