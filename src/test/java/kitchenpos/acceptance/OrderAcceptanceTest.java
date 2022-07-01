package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.fixture.MenuFixtureFactory;
import kitchenpos.fixture.MenuProductFixtureFactory;
import kitchenpos.fixture.OrderFixtureFactory;
import kitchenpos.fixture.OrderLineItemFixtureFactory;
import kitchenpos.fixture.OrderTableFixtureFactory;
import kitchenpos.fixture.TableGroupFixtureFactory;
import kitchenpos.menu.domain.MenuGroup;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.dto.MenuDto;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.dto.OrderLineItemDto;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.product.domain.Product;
import kitchenpos.table.dto.OrderTableResponse;
import kitchenpos.table.dto.TableGroupResponse;
import kitchenpos.utils.KitchenPosBehaviors;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class OrderAcceptanceTest extends AcceptanceTest {

    Product product;
    MenuGroup menuGroup;
    MenuDto menuDTO;
    OrderTableResponse orderTable1;
    OrderTableResponse orderTable2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        product = KitchenPosBehaviors.상품_생성됨("상품1", 10000);
        menuGroup = KitchenPosBehaviors.메뉴그룹_생성됨("메뉴그룹1");
        MenuProduct menuProduct = MenuProductFixtureFactory.createMenuProduct(product.getId(), 1);
        menuDTO = KitchenPosBehaviors.메뉴_생성됨(
                MenuFixtureFactory.createMenu(menuGroup, "강정치킨 한마리", 10000, Lists.newArrayList(menuProduct)));
        orderTable1 = KitchenPosBehaviors.테이블_생성됨(OrderTableFixtureFactory.createEmptyOrderTable());
        orderTable2 = KitchenPosBehaviors.테이블_생성됨(OrderTableFixtureFactory.createEmptyOrderTable());
    }

    /**
     * Scenario : 한 테이블 손님
     * Given 상품, 메뉴그룹, 메뉴, 테이블이 등록되어있다.
     *
     * When 3명의 손님이 와서 테이블의 상태를 비어있지 않은 상태로 변경한다.
     * Then 테이블의 상태가 비어있지 않은 상태로 변경된다.
     * When 주문내용을 받아 테이블에 주문을 추가한다.(주문1)
     * Then 주문1이 추가된다.
     * When 음식이 준비되어 주문1의 상태를 식사 중 상태로 바꾼다.
     * Then 주문1의 상태가 식사 중으로 변경된다.
     * When 추가 주문내용을 받고 테이블에 새로운 주문을 추가한다.(주문2)
     * Then 주문2가 추가된다
     * When 음식이 준비되어 주문2의 상태를 식사 중 상태로 바꾼다.
     * Then 주문2의 상태가 식사 중으로 변경된다.
     * When 식사가 끝나고 결제가 완료되면 모든 주문의 상태를 계산완료로 바꾼 후, 테이블의 상태를 빈 테이블로 변경한다.
     * Then 주문1, 주문2의 상태가 계산완료로 변경된다.
     * Then 테이블이 빈 테이블이 된다.
     */
    @Test
    @DisplayName("한 테이블 손님 시나리오")
    void scenario1() {
        KitchenPosBehaviors.테이블_공석여부_변경_요청(orderTable1.getId(),
                OrderTableFixtureFactory.createParamForChangeEmptyState(false));
        KitchenPosBehaviors.테이블_인원수_변경_요청(orderTable1.getId(),
                OrderTableFixtureFactory.createParamForChangeNumberOfGuests(3));

        OrderResponse savedOrder = 주문을_추가하고_확인한다(orderTable1.getId());
        주문의_상태를_변경하고_확인한다(savedOrder.getId(), OrderStatus.MEAL);

        OrderResponse savedOrder2 = 주문을_추가하고_확인한다(orderTable1.getId());
        주문의_상태를_변경하고_확인한다(savedOrder2.getId(), OrderStatus.MEAL);

        주문의_상태를_변경하고_확인한다(savedOrder.getId(), OrderStatus.COMPLETION);
        주문의_상태를_변경하고_확인한다(savedOrder2.getId(), OrderStatus.COMPLETION);

        KitchenPosBehaviors.테이블_공석여부_변경_요청(orderTable1.getId(),
                OrderTableFixtureFactory.createParamForChangeEmptyState(true));
        KitchenPosBehaviors.테이블_인원수_변경_요청(orderTable1.getId(),
                OrderTableFixtureFactory.createParamForChangeNumberOfGuests(0));
    }

    private OrderResponse 주문을_추가하고_확인한다(Long orderTableId) {
        OrderLineItemDto orderLineItemDto = OrderLineItemFixtureFactory.createOrderLine(menuDTO.getId(), 3);
        OrderRequest orderRequest = OrderFixtureFactory.createOrder(orderTableId, Lists.newArrayList(orderLineItemDto));

        ExtractableResponse<Response> createResponse = KitchenPosBehaviors.주문_추가_요청(orderRequest);
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return createResponse.as(OrderResponse.class);
    }

    private void 주문의_상태를_변경하고_확인한다(Long orderTableId, OrderStatus orderStatus) {
        ExtractableResponse<Response> response = KitchenPosBehaviors.주문상태변경_요청(orderTableId,
                OrderFixtureFactory.createParamForUpdateStatus(orderStatus));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    /**
     * Scenario : 단체 손님
     * Given 상품, 메뉴그룹, 메뉴, 테이블이 등록되어있다.
     *
     * When 7명의 손님이 와서 2개 테이블을 그룹지정한 후  4명, 3명으로 나눠 앉는다.
     * Then 테이블 그룹이 생성되며 테이블1, 테이블2의 상태가 비어있지않은 상태로 변경된다.
     * When 테이블1의 주문내용을 받고 테이블1 에 주문1을 추가한다.
     * Then 테이블1에 주문1이 추가된다.
     * When 테이블2의 주문내용을 받고 테이블2에 주문2를 추가한다.
     * Then 테이블2에 주문2가 추가된다.
     * When 음식이 준비되어 주문1의 상태를 식사 중 상태로 바꾼다.
     * Then 주문1의 상태가 식사로 변경된다.
     * When 음식이 준비되어 주문2의 상태를 식사 중 상태로 바꾼다.
     * Then 주문2의 상태가 식사로 변경된다.
     * When 식사가 끝나면 모든 주문의 상태를 계산완료로 변경한다.
     * Then 주문1, 주문2의 상태가 계산완료로 변경된다.
     * When 결제가 완료되면 테이블 그룹을 해제한다.
     * Then 테이블 그룹이 해제된다.
     */
    @Test
    @DisplayName("단체 손님 시나리오")
    void scenario2() {
        TableGroupResponse tableGroup = KitchenPosBehaviors.테이블그룹_생성(
                TableGroupFixtureFactory.createTableGroup(Lists.newArrayList(orderTable1, orderTable2)));
        KitchenPosBehaviors.테이블_인원수_변경_요청(orderTable1.getId(),
                OrderTableFixtureFactory.createParamForChangeNumberOfGuests(4));
        KitchenPosBehaviors.테이블_인원수_변경_요청(orderTable2.getId(),
                OrderTableFixtureFactory.createParamForChangeNumberOfGuests(3));

        OrderResponse savedOrder = 주문을_추가하고_확인한다(orderTable1.getId());
        OrderResponse savedOrder2 = 주문을_추가하고_확인한다(orderTable2.getId());

        주문의_상태를_변경하고_확인한다(savedOrder.getId(), OrderStatus.MEAL);
        주문의_상태를_변경하고_확인한다(savedOrder2.getId(), OrderStatus.MEAL);

        주문의_상태를_변경하고_확인한다(savedOrder.getId(), OrderStatus.COMPLETION);
        주문의_상태를_변경하고_확인한다(savedOrder2.getId(), OrderStatus.COMPLETION);

        KitchenPosBehaviors.테이블그룹_해제_요청(tableGroup.getId());
    }
}
