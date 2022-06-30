package kitchenpos.ui;

import static kitchenpos.utils.generator.MenuFixtureGenerator.메뉴_생성_요청;
import static kitchenpos.utils.generator.MenuGroupFixtureGenerator.메뉴_그룹_생성_요청;
import static kitchenpos.utils.generator.OrderFixtureGenerator.주문_상태_변경_요청;
import static kitchenpos.utils.generator.OrderFixtureGenerator.주문_생성_요청;
import static kitchenpos.utils.generator.OrderTableFixtureGenerator.비어있지_않은_주문_테이블_생성_요청;
import static kitchenpos.utils.generator.ProductFixtureGenerator.상품_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.utils.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("API:Order")
public class OrderRestControllerTest extends BaseTest {

    public static final String ORDER_API_BASE_URL = "/api/orders";
    public static final String UPDATE_ORDER_STATUS_API_URL_TEMPLATE = ORDER_API_BASE_URL
        .concat("/{orderId}/order-status");

    private Product savedFirstProduct, savedSecondProduct, savedThirdProduct, savedForthProduct;
    private MenuGroup savedFirstMenuGroup, savedSecondMenuGroup;
    private Menu savedFirstMenu, savedSecondMenu;
    private OrderTable savedOrderTable;

    /**
     * @Given 상품을 2개를 생성한다.
     * @Given 메뉴 그룹을 생성한다.
     * @Given 메뉴 그룹과 메뉴에 포함할 상품정보를 이용하여 메뉴를 생성한다.
     * @Given 위와 동일한 방법으로 메뉴를 하나 더 생성한다.
     * @Given 고객의 매장 주문을 위한 주문 테이블을 생성한다.
     */
    @BeforeEach
    void setUp() throws Exception {
        savedFirstProduct = mockMvcUtil.as(mockMvcUtil.post(상품_생성_요청()), Product.class);
        savedSecondProduct = mockMvcUtil.as(mockMvcUtil.post(상품_생성_요청()), Product.class);
        savedThirdProduct = mockMvcUtil.as(mockMvcUtil.post(상품_생성_요청()), Product.class);
        savedForthProduct = mockMvcUtil.as(mockMvcUtil.post(상품_생성_요청()), Product.class);

        savedFirstMenuGroup = mockMvcUtil.as(mockMvcUtil.post(메뉴_그룹_생성_요청()), MenuGroup.class);
        savedSecondMenuGroup = mockMvcUtil.as(mockMvcUtil.post(메뉴_그룹_생성_요청()), MenuGroup.class);

        savedFirstMenu = mockMvcUtil
            .as(mockMvcUtil.post(메뉴_생성_요청(savedFirstMenuGroup, savedFirstProduct, savedSecondProduct)), Menu.class);
        savedSecondMenu = mockMvcUtil
            .as(mockMvcUtil.post(메뉴_생성_요청(savedSecondMenuGroup, savedThirdProduct, savedForthProduct)), Menu.class);

        savedOrderTable = mockMvcUtil.as(mockMvcUtil.post(비어있지_않은_주문_테이블_생성_요청()), OrderTable.class);
    }

    /**
     * @When 특정 주문 테이블의 주문을 등록한다.
     * @Then 전체 주문 조회 내역에서 등록한 주문을 조회할 수 있다.
     */
    @Test
    @DisplayName("주문을 등록한다.")
    public void order() throws Exception {
        // When
        ResultActions resultActions = mockMvcUtil.post(주문_생성_요청(savedOrderTable, savedFirstMenu, savedSecondMenu));

        // Then
        resultActions
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.orderTableId").value(savedOrderTable.getId()))
            .andExpect(jsonPath("$.orderStatus").value(OrderStatus.COOKING.name()))
            .andExpect(jsonPath("$.orderedTime").exists())
            .andExpect(jsonPath("$.orderLineItems[*]").exists());
    }

    @Test
    @DisplayName("주문 목록을 조회한다.")
    public void getAllOrders() throws Exception {
        // When
        mockMvcUtil.post(주문_생성_요청(savedOrderTable, savedFirstMenu, savedSecondMenu));

        ResultActions resultActions = mockMvcUtil.get(ORDER_API_BASE_URL);

        // Then
        resultActions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*]", hasSize(1)));
    }

    @Test
    @DisplayName("주문 상태를 변경한다.")
    public void updateOrderStatus() throws Exception {
        // Given
        Order savedOrder = mockMvcUtil.as(mockMvcUtil.post(주문_생성_요청(savedOrderTable, savedFirstMenu, savedSecondMenu)), Order.class);

        Order updateOrderStatusRequest = new Order();
        updateOrderStatusRequest.setOrderStatus(OrderStatus.MEAL.name());

        // When
        ResultActions resultActions = mockMvcUtil.put(주문_상태_변경_요청(updateOrderStatusRequest, savedOrder.getId()));

        // Then
        resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderStatus").value(OrderStatus.MEAL.name()));

        Order updatedOrder = mockMvcUtil.as(resultActions, Order.class);
        assertThat(updatedOrder)
            .usingRecursiveComparison()
            .as("주문상태, 주문시간을 제외한 나머지 항목이 주문상태 변경 전과 동일한지 여부 검증")
            .ignoringFields("orderStatus", "orderedTime")
            .isEqualTo(savedOrder);
    }
}
