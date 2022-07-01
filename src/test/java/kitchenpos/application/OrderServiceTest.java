package kitchenpos.application;

import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kitchenpos.domain.OrderLineItemTest.주문_항목_생성;
import static kitchenpos.domain.OrderTableTest.주문_태이블_생성;
import static kitchenpos.domain.OrderTest.주문_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;
    @InjectMocks
    private OrderService orderService;

    private OrderLineItem orderLineItem1;
    private OrderLineItem orderLineItem2;
    private List<OrderLineItem> orderLineItems;

    @BeforeEach
    void setup() {
        orderLineItem1 = 주문_항목_생성(null, 1L, 1);
        orderLineItem2 = 주문_항목_생성(null, 2L, 1);
        orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(menuRepository.countByIdIn(any())).thenReturn(orderLineItems.size());
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderLineItemDao.save(orderLineItem1)).thenReturn(orderLineItem1);
        when(orderLineItemDao.save(orderLineItem2)).thenReturn(orderLineItem2);
        when(orderRepository.save(order)).thenReturn(주문_생성(1L, orderTable.getId(), OrderStatus.COOKING.toString(),
                LocalDateTime.now(), orderLineItems));

        // when
        Order savedOrder = orderService.create(order);

        // then
        assertAll(
                () -> assertThat(savedOrder.getId()).isNotNull(),
                () -> assertThat(savedOrder.getOrderTableId()).isEqualTo(order.getOrderTableId()),
                () -> assertThat(savedOrder.getOrderStatus()).isEqualTo(order.getOrderStatus()),
                () -> assertThat(savedOrder.getOrderLineItems()).isEqualTo(order.getOrderLineItems())
        );

    }

    @DisplayName("주문항목이 1개 이상이여야 한다.")
    @Test
    void createOrder1() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹에 포함되어야 한다.")
    @Test
    void createOrder2() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(menuRepository.countByIdIn(any())).thenReturn(0);

        // when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("싱픔 정보가 존재하지 않으면 안된다.")
    @Test
    void createOrder3() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(menuRepository.countByIdIn(any())).thenReturn(orderLineItems.size());
        when(orderTableDao.findById(any())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 상품의 가격의 합보다 크면 안된다.")
    @Test
    void createOrder4() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, true);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(menuRepository.countByIdIn(any())).thenReturn(orderLineItems.size());
        when(orderTableDao.findById(any())).thenReturn(Optional.of(orderTable));

        // when, then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void list() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(orderLineItemDao.findAllByOrderId(order.getId())).thenReturn(orderLineItems);

        // when
        List<Order> list = orderService.list();

        // then
        assertThat(list).containsExactly(order);
    }

    @DisplayName("주문 상태를 변경한다.")
    @Test
    void changeOrderStatus() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COOKING.toString(), LocalDateTime.now(), orderLineItems);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderLineItemDao.findAllByOrderId(any())).thenReturn(orderLineItems);

        // when
        Order savedOrder = orderService.changeOrderStatus(1L, order);

        // then
        assertAll(
                () -> assertThat(savedOrder.getOrderTableId()).isEqualTo(order.getOrderTableId()),
                () -> assertThat(savedOrder.getOrderStatus()).isEqualTo(order.getOrderStatus()),
                () -> assertThat(savedOrder.getOrderLineItems()).isEqualTo(order.getOrderLineItems())
        );
    }

    @DisplayName("주문 상태를 변경한다.")
    @Test
    void changeOrderStatus2() {
        // given
        OrderTable orderTable = 주문_태이블_생성(1L, null, 1, false);
        Order order = 주문_생성(orderTable.getId(), OrderStatus.COMPLETION.toString(), LocalDateTime.now(), orderLineItems);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when, then
        assertThatThrownBy(() -> orderService.changeOrderStatus(1L, order))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
