package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import kitchenpos.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static kitchenpos.acceptance.MenuGroupRestAssured.메뉴_그룹_생성_요청;
import static kitchenpos.acceptance.MenuRestAssured.메뉴_생성_요청;
import static kitchenpos.acceptance.OrderRestAssured.*;
import static kitchenpos.acceptance.ProductRestAssured.상품_생성_요청;
import static kitchenpos.acceptance.TableRestAssured.주문_테이블_생성_요청;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenu;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static kitchenpos.domain.OrderTestFixture.createOrder;
import static kitchenpos.domain.OrderTestFixture.createOrderLineItem;
import static kitchenpos.domain.ProductTestFixture.createProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 관련 인수 테스트")
public class OrderAcceptanceTest extends AbstractAcceptanceTest {
    private ProductResponse frenchfries;
    private ProductResponse bulgogiBurger;
    private ProductResponse chickenBurger;
    private ProductResponse cola;
    private MenuGroup burgerSet;
    private MenuProduct frenchfriesProduct;
    private MenuProduct bulgogiBurgerProduct;
    private MenuProduct chickenBurgerProduct;
    private MenuProduct colaProduct;
    private Menu bulgogiBurgerSet;
    private Menu chickenBurgerSet;
    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private OrderLineItem bulgogiBurgerSetOrder;
    private OrderLineItem chickenBurgerSetOrder;
    private Order Order1;
    private Order Order2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        burgerSet = 메뉴_그룹_생성_요청(createMenuGroup("burgerSet")).as(MenuGroup.class);
        frenchfries = 상품_생성_요청(createProductRequest("frenchfries", BigDecimal.valueOf(3000L))).as(ProductResponse.class);
        cola = 상품_생성_요청(createProductRequest("cola", BigDecimal.valueOf(1500L))).as(ProductResponse.class);
        bulgogiBurger = 상품_생성_요청(createProductRequest("bulgogiBurger", BigDecimal.valueOf(4000L))).as(ProductResponse.class);
        chickenBurger = 상품_생성_요청(createProductRequest("chickenBurger", BigDecimal.valueOf(4500L))).as(ProductResponse.class);
        frenchfriesProduct = createMenuProduct(1L, null, frenchfries.getId(), 1L);
        colaProduct = createMenuProduct(2L, null, cola.getId(), 1L);
        bulgogiBurgerProduct = createMenuProduct(3L, null, bulgogiBurger.getId(), 1L);
        chickenBurgerProduct = createMenuProduct(3L, null, chickenBurger.getId(), 1L);
        bulgogiBurgerSet = 메뉴_생성_요청(createMenu(null, "bulgogiBurgerSet", BigDecimal.valueOf(8500L), burgerSet.getId(), Arrays.asList(frenchfriesProduct, colaProduct, bulgogiBurgerProduct))).as(Menu.class);
        chickenBurgerSet = 메뉴_생성_요청(createMenu(null, "chickenBurgerSet", BigDecimal.valueOf(9000L), burgerSet.getId(), Arrays.asList(frenchfriesProduct, colaProduct, chickenBurgerProduct))).as(Menu.class);
        orderTable1 = 주문_테이블_생성_요청(createOrderTable(null, null, 5, false)).as(OrderTable.class);
        orderTable2 = 주문_테이블_생성_요청(createOrderTable(null, null, 4, false)).as(OrderTable.class);
        bulgogiBurgerSetOrder = createOrderLineItem(1L, null, bulgogiBurgerSet.getId(), 2);
        chickenBurgerSetOrder = createOrderLineItem(2L, null, chickenBurgerSet.getId(), 1);
        Order1 = createOrder(orderTable1.getId(), null, null, Arrays.asList(bulgogiBurgerSetOrder, chickenBurgerSetOrder));
        Order2 = createOrder(orderTable2.getId(), null, null, singletonList(bulgogiBurgerSetOrder));
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void creatOrderTest() {
        // when
        ExtractableResponse<Response> response = 주문_생성_요청(Order1);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void selectAllOrderList() {
        // given
        ExtractableResponse<Response> response1 = 주문_생성_요청(Order1);
        ExtractableResponse<Response> response2 = 주문_생성_요청(Order2);
        // when
        ExtractableResponse<Response> result = 주문_목록_조회_요청();

        List<Long> expectedOrderIds = getExpectedOrderIds(Arrays.asList(response1, response2));
        List<Long> resultOrderIds = getResultOrderIds(result);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultOrderIds).containsAll(expectedOrderIds);
    }

    @DisplayName("주문 상태를 변경한다.")
    @Test
    void changeOrderStatus() {
        // given
        String expectOrderStatus = OrderStatus.MEAL.name();
        Order order = 주문_생성_요청(Order1).as(Order.class);
        Order changeOrder = createOrder(order.getOrderTableId(), expectOrderStatus, order.getOrderedTime(), order.getOrderLineItems());
        // when
        ExtractableResponse<Response> response = 주문_상태_변경_요청(order.getId(), changeOrder);
        String actualOrderStatus = response.jsonPath().getString("orderStatus");
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualOrderStatus).isEqualTo(expectOrderStatus)
        );
    }

    private static List<Long> getExpectedOrderIds(List<ExtractableResponse<Response>> responses) {
        return responses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[3]))
                .collect(Collectors.toList());
    }

    private static List<Long> getResultOrderIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", Order.class).stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }

}
