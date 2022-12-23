package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenu;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static kitchenpos.domain.OrderTestFixture.createOrder;
import static kitchenpos.domain.OrderTestFixture.createOrderLineItem;
import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("주문 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderService orderService;

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
    void setUp() {
        frenchFries = createProduct(1L, "frenchFries", BigDecimal.valueOf(3000L));
        coke = createProduct(2L, "coke", BigDecimal.valueOf(1500L));
        beefBurger = createProduct(3L, "beefBurger", BigDecimal.valueOf(4000L));
        chickenBurger = createProduct(4L, "chickenBurger", BigDecimal.valueOf(4500L));
        burgerSet = createMenuGroup(1L, "burgerSet");
        frenchFriesProduct = createMenuProduct(1L, null, frenchFries.getId(), 1L);
        cokeProduct = createMenuProduct(2L, null, coke.getId(), 1L);
        beefBurgerProduct = createMenuProduct(3L, null, beefBurger.getId(), 1L);
        chickenBurgerProduct = createMenuProduct(3L, null, chickenBurger.getId(), 1L);
        beefBurgerSet = createMenu(1L, "beefBurgerSet", BigDecimal.valueOf(8500L), burgerSet.getId(),
                Arrays.asList(frenchFriesProduct, cokeProduct, beefBurgerProduct));
        chickenBurgerSet = createMenu(2L, "chickenBurgerSet", BigDecimal.valueOf(9000L), burgerSet.getId(),
                Arrays.asList(frenchFriesProduct, cokeProduct, chickenBurgerProduct));
        orderTable1 = createOrderTable( 5, false);
        orderTable2 = createOrderTable(7, false);
        beefBurgerSetOrder = createOrderLineItem(1L, null, beefBurgerSet.getId(), 2);
        chickenBurgerSetOrder = createOrderLineItem(2L, null, chickenBurgerSet.getId(), 1);
        order1 = createOrder(orderTable1.getId(), null, null, Arrays.asList(beefBurgerSetOrder, chickenBurgerSetOrder));
        order2 = createOrder(orderTable2.getId(), null, null, singletonList(beefBurgerSetOrder));
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void 주문_생성() {
        // given
        List<Long> menuIds = order1.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        when(menuDao.countByIdIn(menuIds)).thenReturn((long) menuIds.size());
        when(orderTableDao.findById(order1.getOrderTableId())).thenReturn(Optional.of(orderTable1));
        when(orderDao.save(order1)).thenReturn(order1);
        when(orderLineItemDao.save(beefBurgerSetOrder)).thenReturn(beefBurgerSetOrder);
        when(orderLineItemDao.save(chickenBurgerSetOrder)).thenReturn(chickenBurgerSetOrder);
        // when
        Order order = orderService.create(order1);
        // then
        assertAll(
                () -> assertThat(order.getOrderedTime()).isNotNull(),
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name()),
                () -> assertThat(order.getId()).isEqualTo(beefBurgerSetOrder.getOrderId()),
                () -> assertThat(order.getId()).isEqualTo(chickenBurgerSetOrder.getOrderId())
        );
    }

    @DisplayName("주문 항목이 비어있으면 주문 생성 시 예외가 발생한다.")
    @Test
    void 주문_생성_예외1() {
        // given
        Order order = createOrder(orderTable1.getId(), null, null, null);
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 시, 주문 항목 내에 등록되지 않은 메뉴가 있다면 예외가 발생한다.")
    @Test
    void 주문_생성_예외2() {
        // given
        List<Long> menuIds = order1.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        when(menuDao.countByIdIn(menuIds)).thenReturn(0L);
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.create(order1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되어 있지않은 주문 테이블을 가진 주문은 생성될 수 없다.")
    @Test
    void 주문_생성_예외3() {
        // given
        List<Long> menuIds = order1.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        when(menuDao.countByIdIn(menuIds)).thenReturn((long) menuIds.size());
        when(orderTableDao.findById(order1.getOrderTableId())).thenReturn(Optional.empty());
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.create(order1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블이 비어있을 경우 주문은 생성될 수 없다.")
    @Test
    void 주문_생성_예외4() {
        // given
        OrderTable orderTable = createOrderTable(6, true);
        Order order = createOrder(orderTable.getId(), null, null, singletonList(beefBurgerSetOrder));
        List<Long> menuIds = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        when(menuDao.countByIdIn(menuIds)).thenReturn((long) menuIds.size());
        when(orderTableDao.findById(order.getOrderTableId())).thenReturn(Optional.of(orderTable));
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 전체 목록을 조회한다.")
    @Test
    void 주문_전체_목록_조회() {
        // given
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderDao.findAll()).thenReturn(orders);
        for (Order order : orders) {
            when(orderLineItemDao.findAllByOrderId(order.getId())).thenReturn(order.getOrderLineItems());
        }
        // when
        List<Order> findOrders = orderService.list();
        // then
        assertAll(
                () -> assertThat(findOrders).hasSize(orders.size()),
                () -> assertThat(findOrders).containsExactly(order1, order2)
        );
    }

    @DisplayName("주문 상태를 변경한다.")
    @Test
    void 주문_상태_변경() {
        // given
        String expectOrderStatus = OrderStatus.MEAL.name();
        Order changeOrder = createOrder(order2.getId(), order2.getOrderTableId(), expectOrderStatus, order2.getOrderedTime(), order2.getOrderLineItems());
        when(orderDao.findById(order2.getId())).thenReturn(Optional.of(order2));
        when(orderDao.save(order2)).thenReturn(order2);
        // when
        Order resultOrder = orderService.changeOrderStatus(order2.getId(), changeOrder);
        // then
        assertThat(resultOrder.getOrderStatus()).isEqualTo(expectOrderStatus);
    }

    @DisplayName("등록되지 않은 주문에 대해 주문 상태를 변경할 수 없다.")
    @Test
    void 주문_상태_변경_예외1() {
        // given
        Long notExistsOrderId = 5L;
        when(orderDao.findById(notExistsOrderId)).thenReturn(Optional.empty());
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.changeOrderStatus(notExistsOrderId, order2)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 완료된 주문이면 주문 상태를 변경할 수 없다.")
    @Test
    void 주문_상태_변경_예외2() {
        // given
        Order order = createOrder(orderTable2.getId(), OrderStatus.COMPLETION.name(), null,
                Arrays.asList(beefBurgerSetOrder, chickenBurgerSetOrder));
        Order changeOrder = createOrder(order.getId(), order.getOrderTableId(), OrderStatus.MEAL.name(), order.getOrderedTime(), order.getOrderLineItems());
        when(orderDao.findById(order.getId())).thenReturn(Optional.of(order));
        // when & then
        Assertions.assertThatThrownBy(
                () -> orderService.changeOrderStatus(order.getId(), changeOrder)
        ).isInstanceOf(IllegalArgumentException.class);

    }

}
