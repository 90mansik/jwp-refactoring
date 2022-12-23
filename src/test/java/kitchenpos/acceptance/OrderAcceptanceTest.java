package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static kitchenpos.acceptance.MenuGroupRestAssured.createMenuGroupRequest;
import static kitchenpos.acceptance.MenuRestAssured.createMenuRequest;
import static kitchenpos.acceptance.OrderRestAssured.*;
import static kitchenpos.acceptance.ProductRestAssured.createProductRequest;
import static kitchenpos.acceptance.TableRestAssured.createOrderTableRequest;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenu;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static kitchenpos.domain.OrderTestFixture.createOrder;
import static kitchenpos.domain.OrderTestFixture.createOrderLineItem;
import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문 관련 인수 테스트")
public class OrderAcceptanceTest extends AbstractAcceptanceTest {
    private Product frenchFries;
    private Product beefBurger;
    private Product chickenBurger;
    private Product coke;
    private MenuGroup burgerSet;
    private MenuProduct frenchFriesProduct;
    private MenuProduct beefBurgerProduct;
    private MenuProduct chickenBurgerProduct;
    private MenuProduct cokeProduct;
    private Menu beefBurgerSet;
    private Menu chickenBurgerSet;
    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private OrderLineItem beefBurgerSetOrder;
    private OrderLineItem chickenBurgerSetOrder;
    private Order order1;
    private Order order2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        burgerSet = createMenuGroupRequest(createMenuGroup("햄버거세트")).as(MenuGroup.class);
        frenchFries = createProductRequest(createProduct(null, "감자튀김", BigDecimal.valueOf(3000L))).as(Product.class);
        coke = createProductRequest(createProduct(null, "콜라", BigDecimal.valueOf(1500L))).as(Product.class);
        beefBurger = createProductRequest(createProduct(null, "불고기버거", BigDecimal.valueOf(4000L))).as(Product.class);
        chickenBurger = createProductRequest(createProduct(null, "치킨버거", BigDecimal.valueOf(4500L))).as(Product.class);
        frenchFriesProduct = createMenuProduct(1L, null, frenchFries.getId(), 1L);
        cokeProduct = createMenuProduct(2L, null, coke.getId(), 1L);
        beefBurgerProduct = createMenuProduct(3L, null, beefBurger.getId(), 1L);
        chickenBurgerProduct = createMenuProduct(3L, null, chickenBurger.getId(), 1L);
        beefBurgerSet = createMenuRequest(createMenu(null, "불고기버거세트", BigDecimal.valueOf(8500L), burgerSet.getId(), Arrays.asList(frenchFriesProduct, cokeProduct, beefBurgerProduct))).as(Menu.class);
        chickenBurgerSet = createMenuRequest(createMenu(null, "치킨버거세트", BigDecimal.valueOf(9000L), burgerSet.getId(), Arrays.asList(frenchFriesProduct, cokeProduct, chickenBurgerProduct))).as(Menu.class);
        orderTable1 = createOrderTableRequest(createOrderTable(null, null, 5, false)).as(OrderTable.class);
        orderTable2 = createOrderTableRequest(createOrderTable(null, null, 4, false)).as(OrderTable.class);
        beefBurgerSetOrder = createOrderLineItem(1L, null, beefBurgerSet.getId(), 2);
        chickenBurgerSetOrder = createOrderLineItem(2L, null, chickenBurgerSet.getId(), 1);
        order1 = createOrder(orderTable1.getId(), null, null, Arrays.asList(beefBurgerSetOrder, chickenBurgerSetOrder));
        order2 = createOrder(orderTable2.getId(), null, null, singletonList(beefBurgerSetOrder));
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void 주문_생성() {
        // when
        ExtractableResponse<Response> response = createOrderRequest(order1);
        // then
        주문_생성됨(response);
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void 주문_목록_전체_조회() {
        // given
        ExtractableResponse<Response> response1 = createOrderRequest(order1);
        ExtractableResponse<Response> response2 = createOrderRequest(order2);
        // when
        ExtractableResponse<Response> response = selectOrdersRequest();
        // then
        주문_목록_응답됨(response);
        주문_목록_포함됨(response, Arrays.asList(response1, response2));
    }

    @DisplayName("주문 상태를 변경한다.")
    @Test
    void 주문_상태_변경() {
        // given
        String expectOrderStatus = OrderStatus.MEAL.name();
        Order order = createOrderRequest(order1).as(Order.class);
        Order changeOrder = createOrder(order.getOrderTableId(), expectOrderStatus, order.getOrderedTime(), order.getOrderLineItems());
        // when
        ExtractableResponse<Response> response = changeOrderStatus(order.getId(), changeOrder);
        // then
        주문_상태_변경됨(response, expectOrderStatus);
    }

    private static void 주문_생성됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private static void 주문_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static void 주문_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedOrderIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[3]))
                .collect(Collectors.toList());

        List<Long> resultOrderIds = response.jsonPath().getList(".", Order.class).stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        assertThat(resultOrderIds).containsAll(expectedOrderIds);
    }

    private static void 주문_상태_변경됨(ExtractableResponse<Response> response, String expectOrderStatus) {
        String actualOrderStatus = response.jsonPath().getString("orderStatus");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualOrderStatus).isEqualTo(expectOrderStatus)
        );
    }

}
