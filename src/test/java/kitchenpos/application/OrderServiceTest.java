package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
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

    private OrderLineItem orderLineItem1;
    private OrderLineItem orderLineItem2;
    private Order order;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        order = new Order();
        orderLineItem1 = new OrderLineItem();
        orderLineItem2 = new OrderLineItem();
        orderTable = new OrderTable();
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void create() {
        //given
        order.setOrderLineItems(Arrays.asList(orderLineItem1));
        given(menuDao.countByIdIn(any())).willReturn(1L);
        orderTable.setEmpty(false);
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderLineItemDao.save(any())).willReturn(orderLineItem1);
        given(orderDao.save(any())).willReturn(order);

        //when
        Order savedOrder = orderService.create(order);

        //then
        assertThat(savedOrder).isEqualTo(order);
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(savedOrder.getOrderLineItems()).isNotEmpty().containsExactly(orderLineItem1);
    }

    @Test
    @DisplayName("주문 항목이 비어있으면 주문에 실패한다.")
    void create_fail_1() {
        //given
        order.setOrderLineItems(Collections.emptyList());

        //then
        assertThatThrownBy(() -> orderService.create(order)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 항목의 요청 갯수와 실제 저장된 주문 항목을 조회했을 때 갯수가 다르면 주문에 실패한다.")
    void create_fail_2() {
        //given
        order.setOrderLineItems(Arrays.asList(orderLineItem2));
        given(menuDao.countByIdIn(any())).willReturn(0L);

        //then
        assertThatThrownBy(() -> orderService.create(order)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 테이블 조회 결과가 없으면 주문에 실패한다.")
    void create_fail_3() {
        //given
        order.setOrderLineItems(Arrays.asList(orderLineItem1));
        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> orderService.create(order)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 테이블이 비어있으면 주문에 실패한다.")
    void create_fail_4() {
        //given
        order.setOrderLineItems(Arrays.asList(orderLineItem1));
        given(menuDao.countByIdIn(any())).willReturn(1L);
        orderTable.setEmpty(true);
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));

        //then
        assertThatThrownBy(() -> orderService.create(order)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 주문을 조회할 수 있다.")
    void list() {
        //given
        given(orderDao.findAll()).willReturn(Arrays.asList(order));

        //then
        assertThat(orderService.list()).containsExactly(order);
    }

    @Test
    @DisplayName("주문 상태를 변경할 수 있다.")
    void changeOrderStatus() {
        //given
        order.setOrderStatus(OrderStatus.MEAL.name());
        given(orderDao.findById(any())).willReturn(Optional.of(order));
        given(orderDao.save(any())).willReturn(order);
        given(orderLineItemDao.findAllByOrderId(any())).willReturn(Arrays.asList(orderLineItem2));

        //when
        Order savedOrder = orderService.changeOrderStatus(0L, order);

        //then
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
        assertThat(savedOrder.getOrderLineItems()).isNotEmpty().containsExactly(orderLineItem2);
    }

    @Test
    @DisplayName("주문이 비어 있으면 상태를 변경할 수 없다.")
    void changeOrderStatus_failed_1() {
        //given
        given(orderDao.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> orderService.changeOrderStatus(0L, order)).isExactlyInstanceOf(
                IllegalArgumentException.class);

    }

    @Test
    @DisplayName("계산 완료된 주문은 상태를 변경할 수 없다.")
    void changeOrderStatus_failed_2() {
        //given
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        given(orderDao.findById(any())).willReturn(Optional.of(order));

        //then
        assertThatThrownBy(() -> orderService.changeOrderStatus(0L, order)).isExactlyInstanceOf(
                IllegalArgumentException.class);
    }
}
