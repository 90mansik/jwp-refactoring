package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.acceptance.MenuAcceptanceTest.메뉴_등록되어_있음;
import static kitchenpos.acceptance.MenuGroupAcceptanceTest.메뉴그룹_등록되어_있음;
import static kitchenpos.acceptance.ProductAcceptanceTest.상품_등록되어_있음;
import static kitchenpos.utils.RestAssuredMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 관련 기능")
public class OrderAcceptanceTest extends AcceptanceTest {
    private ProductResponse 김치찌개_product;
    private ProductResponse 공기밥_product;
    private MenuGroupResponse 한식_menuGroup;
    private MenuResponse 김치찌개1인세트_menu;
    private MenuResponse 김치찌개2인세트_menu;
    private MenuProductRequest 김치찌개_menuProduct_1인;
    private MenuProductRequest 공기밥_menuProduct_1인;
    private MenuProductRequest 김치찌개_menuProduct_2인;
    private MenuProductRequest 공기밥_menuProduct_2인;
    private OrderTableResponse 테이블1_table;
    private OrderTableResponse 테이블2_table;

    private OrderRequest 테이블1_orderRequest;
    private OrderRequest 테이블2_orderRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        한식_menuGroup = 메뉴그룹_등록되어_있음("한식").as(MenuGroupResponse.class);
        김치찌개_product = 상품_등록되어_있음("김치찌개", 8000).as(ProductResponse.class);
        공기밥_product = 상품_등록되어_있음("공기밥", 1000).as(ProductResponse.class);

        김치찌개_menuProduct_1인 = MenuProductRequest.of(김치찌개_product.getId(), 1);
        공기밥_menuProduct_1인 = MenuProductRequest.of(공기밥_product.getId(), 1);
        김치찌개_menuProduct_2인 = MenuProductRequest.of(김치찌개_product.getId(), 2);
        공기밥_menuProduct_2인 = MenuProductRequest.of(공기밥_product.getId(), 2);
        김치찌개1인세트_menu = 메뉴_등록되어_있음("김치찌개 1인세트", 8500, 한식_menuGroup.getId(),
                Arrays.asList(김치찌개_menuProduct_1인, 공기밥_menuProduct_1인)).as(MenuResponse.class);
        김치찌개2인세트_menu = 메뉴_등록되어_있음("김치찌개 2인세트", 1500, 한식_menuGroup.getId(),
                Arrays.asList(김치찌개_menuProduct_2인, 공기밥_menuProduct_2인)).as(MenuResponse.class);

        테이블1_table = TableAcceptanceTest.테이블_등록되어_있음(4, false).as(OrderTableResponse.class);
        테이블2_table = TableAcceptanceTest.테이블_등록되어_있음(3, false).as(OrderTableResponse.class);

        OrderLineItemRequest 김치찌개1인세트_1개_orderLineItem = OrderLineItemRequest.of(김치찌개1인세트_menu.getId(), 1);
        OrderLineItemRequest 김치찌개2인세트_2개_orderLineItem = OrderLineItemRequest.of(김치찌개2인세트_menu.getId(), 2);
        테이블1_orderRequest = OrderRequest.of(테이블1_table.getId(), Arrays.asList(김치찌개1인세트_1개_orderLineItem));
        테이블2_orderRequest = OrderRequest.of(테이블2_table.getId(), Arrays.asList(김치찌개2인세트_2개_orderLineItem));
    }

    /**
     * Feature: 주문 관련 기능
     *
     *   Scenario: 주문을 관리
     *     When 테이블1 주문 등록 요청
     *     Then 테이블1 주문 등록됨
     *     When 테이블2 주문 등록 요청
     *     Then 테이블2 주문 등록됨
     *     When 주문 조회 요청
     *     Then 테이블1 주문, 테이블2 주문 조회됨
     *     When 테이블1 주문 상태를 '조리중' → '식사중'으로 업데이트 요청
     *     Then 주문 업데이트됨
     *     When 테이블1 주문 상태를 '식사중' → '완료'로 업데이트 요청
     *     Then 주문 업데이트됨
     */
    @DisplayName("주문을 관리한다")
    @Test
    void 주문_관리_정상_시나리오() {
        ExtractableResponse<Response> 테이블1_주문_등록 = 주문_등록_요청(테이블1_orderRequest);
        주문_등록됨(테이블1_주문_등록);

        ExtractableResponse<Response> 테이블2_주문_등록 = 주문_등록_요청(테이블2_orderRequest);
        주문_등록됨(테이블2_주문_등록);

        ExtractableResponse<Response> 주문_목록_조회 = 주문_목록_조회_요청();
        주문_목록_응답됨(주문_목록_조회);
        주문_목록_포함됨(주문_목록_조회, Arrays.asList(테이블1_주문_등록, 테이블2_주문_등록));

        OrderStatusRequest request_식사중 = OrderStatusRequest.from(OrderStatus.MEAL.name());
        ExtractableResponse<Response> 주문_업데이트_to_식사중 = 주문_상태_수정_요청(테이블1_주문_등록, request_식사중);
        주문_수정됨(주문_업데이트_to_식사중);

        OrderStatusRequest request_완료 = OrderStatusRequest.from(OrderStatus.MEAL.name());
        ExtractableResponse<Response> 주문_업데이트_to_완료 = 주문_상태_수정_요청(테이블1_주문_등록, request_완료);
        주문_수정됨(주문_업데이트_to_완료);
    }

    public static ExtractableResponse<Response> 주문_등록_요청(OrderRequest params) {
        return post("/api/orders", params);
    }

    public static ExtractableResponse<Response> 주문_목록_조회_요청() {
        return get("/api/orders");
    }

    public static ExtractableResponse<Response> 주문_상태_수정_요청(ExtractableResponse<Response> response, OrderStatusRequest params) {
        String uri = response.header("Location") + "/order-status";
        return put(uri, params);
    }

    public static void 주문_등록됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 주문_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedOrderIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultOrderIds = response.jsonPath().getList(".", OrderResponse.class).stream()
                .map(OrderResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultOrderIds).containsAll(expectedOrderIds);
    }

    public static void 주문_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
