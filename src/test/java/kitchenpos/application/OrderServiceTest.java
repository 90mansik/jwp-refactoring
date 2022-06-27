package kitchenpos.application;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import kitchenpos.ServiceTest;
import kitchenpos.application.helper.ServiceTestHelper;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.fixture.MenuFixtureFactory;
import kitchenpos.fixture.MenuProductFixtureFactory;
import kitchenpos.fixture.OrderLineItemFixtureFactory;
import kitchenpos.fixture.OrderTableFixtureFactory;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderServiceTest extends ServiceTest {
    @Autowired
    private ServiceTestHelper serviceTestHelper;

    @Autowired
    private OrderService orderService;

    private MenuGroup menuGroup;
    private Product product1;
    private Product product2;
    private Product product3;
    private Menu menu1;
    private Menu menu2;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        menuGroup = serviceTestHelper.메뉴그룹_생성됨("메뉴그룹1");
        product1 = serviceTestHelper.상품_생성됨("상품1", 1000);
        product2 = serviceTestHelper.상품_생성됨("상품2", 2000);
        product3 = serviceTestHelper.상품_생성됨("상품3", 3000);
        MenuProduct menuProduct1 = MenuProductFixtureFactory.createMenuProduct(product1.getId(), 4);
        MenuProduct menuProduct2 = MenuProductFixtureFactory.createMenuProduct(product2.getId(), 2);
        menu1 = serviceTestHelper.메뉴_생성됨(menuGroup, "메뉴1", 4000, Lists.newArrayList(menuProduct1));
        menu2 = serviceTestHelper.메뉴_생성됨(menuGroup, "메뉴2", 4000, Lists.newArrayList(menuProduct2));
        orderTable = serviceTestHelper.비어있지않은테이블_생성됨(3);
    }

    @Test
    @DisplayName("주문 등록")
    void 주문등록() {
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);

        Order savedOrder = serviceTestHelper.주문_생성됨(orderTable.getId(),
                Lists.newArrayList(orderLineItem1, orderLineItem2));

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
        assertThat(savedOrder.getOrderTableId()).isEqualTo(orderTable.getId());
        assertThat(savedOrder.getOrderLineItems()).hasSize(2);
    }

    @Test
    @DisplayName("인원수가 0명이라도 주문 등록 가능")
    void 주문등록_인원수가0일때도_주문등록가능() {
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);
        OrderTable zeroGuestTable = serviceTestHelper.비어있지않은테이블_생성됨(0);
        Order savedOrder = serviceTestHelper.주문_생성됨(zeroGuestTable.getId(),
                Lists.newArrayList(orderLineItem1, orderLineItem2));

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
        assertThat(savedOrder.getOrderTableId()).isEqualTo(zeroGuestTable.getId());
        assertThat(savedOrder.getOrderLineItems()).hasSize(2);
    }

    @Test
    @DisplayName("주문항목이 없는경우 주문 등록 실패")
    void 주문등록_주문항목이_없는경우() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> serviceTestHelper.주문_생성됨(orderTable.getId(), emptyList()));
    }

    @Test
    @DisplayName("주문항목에 표시된 메뉴가 존재하지 않는 경우 주문 등록 실패")
    void 주문등록_주문항목에_표시된_메뉴가_저장되지않은경우() {
        MenuProduct menuProduct = MenuProductFixtureFactory.createMenuProduct(product1.getId(), 1);
        Menu notSavedMenu = MenuFixtureFactory.createMenu(menuGroup, "메뉴", 4000, Lists.newArrayList(menuProduct));
        OrderLineItem orderLineItem = OrderLineItemFixtureFactory.createOrderLine(notSavedMenu.getId(), 3);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> serviceTestHelper.주문_생성됨(orderTable.getId(), Lists.newArrayList(orderLineItem)));
    }

    @Test
    @DisplayName("주문테이블이 존재하지 않는 경우 주문 등록 실패")
    void 주문등록_주문테이블이_없는경우() {
        int numberOfGuests = 3;
        OrderTable notSavedOrderTable = OrderTableFixtureFactory.createNotEmptyOrderTable(numberOfGuests);
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> serviceTestHelper.주문_생성됨(notSavedOrderTable.getId(),
                        Lists.newArrayList(orderLineItem1, orderLineItem2)));
    }

    @Test
    @DisplayName("주문테이블이 빈 테이블인 경우 주문 등록 실패")
    void 주문등록_주문테이블이_빈테이블인_경우() {
        OrderTable orderTable = serviceTestHelper.빈테이블_생성됨();
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> serviceTestHelper.주문_생성됨(orderTable.getId(),
                        Lists.newArrayList(orderLineItem1, orderLineItem2)));
    }

    @Test
    @DisplayName("주문 목록 조회")
    void 주문목록_조회() {
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);
        OrderLineItem orderLineItem3 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 2);

        serviceTestHelper.주문_생성됨(orderTable.getId(), Lists.newArrayList(orderLineItem1, orderLineItem2));
        serviceTestHelper.주문_생성됨(orderTable.getId(), Lists.newArrayList(orderLineItem3));

        List<Order> orders = orderService.list();
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("주문 상태 변경")
    void 주문상태_변경() {
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);
        Order order = serviceTestHelper.주문_생성됨(orderTable.getId(), Lists.newArrayList(orderLineItem1, orderLineItem2));

        Order updatedOrder = serviceTestHelper.주문상태_변경(order.getId(), OrderStatus.MEAL);

        assertThat(updatedOrder.getId()).isEqualTo(order.getId());
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }

    @Test
    @DisplayName("주문 상태가 계산완료인 경우 주문 상태 변경 불가")
    void 주문상태_변경_이미_계산완료상태인경우() {
        OrderLineItem orderLineItem1 = OrderLineItemFixtureFactory.createOrderLine(menu1.getId(), 3);
        OrderLineItem orderLineItem2 = OrderLineItemFixtureFactory.createOrderLine(menu2.getId(), 3);

        Order order = serviceTestHelper.주문_생성됨(orderTable.getId(), Lists.newArrayList(orderLineItem1, orderLineItem2));
        serviceTestHelper.주문상태_변경(order.getId(), OrderStatus.COMPLETION);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> serviceTestHelper.주문상태_변경(order.getId(), OrderStatus.MEAL));
    }
}
