package kitchenpos.application;

import static kitchenpos.utils.generator.MenuFixtureGenerator.generateMenu;
import static kitchenpos.utils.generator.MenuGroupFixtureGenerator.generateMenuGroup;
import static kitchenpos.utils.generator.OrderFixtureGenerator.generateOrder;
import static kitchenpos.utils.generator.OrderTableFixtureGenerator.generateOrderTable;
import static kitchenpos.utils.generator.ProductFixtureGenerator.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * - [x] 주문 생성
 *   - [x] 주문에 주문항목(OrderLineItem)이 없는 경우 예외 발생 검증
 *   - [x] DB에 존재하는 메뉴보다 주문한 메뉴가 많은 경우 예외 발생 검증
 *   - [x] 주문에 포함된 주문테이블이 DB에 존재하지 않는 경우 예외 발생 검증
 *   - [x] 주문에 포함된 주문테이블이 비어있는경우(주문을 요청한 테이블이 비어있는 경우) 예외 발생 검증
 * - [x] 주문 상태를 변경한다.
 *   - [x] 주문이 존재하지 않는 경우 예외 발생 검증
 *   - [x] 주문 상태 값이 없는 경우 예외 발생 검증
 *   - [x] 주문이 완료 상태인 경우 예외 발생 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Service:Order")
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

    private Menu menu;
    private MenuGroup menuGroup;
    private Product firstProduct, secondProduct;

    private OrderTable orderTable;
    private Order order;

    @BeforeEach
    void setUp() {
        firstProduct = generateProduct();
        secondProduct = generateProduct();
        menuGroup = generateMenuGroup();
        menu = generateMenu(menuGroup, Arrays.asList(firstProduct, secondProduct));

        orderTable = generateOrderTable();
        order = generateOrder(orderTable, menu);
    }

    @Test
    @DisplayName("주문을 생성한다.")
    void createOrder() {
        // Given
        given(menuDao.countByIdIn(anyList())).willReturn((long) order.getOrderLineItems().size());
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderDao.save(any(Order.class))).will(AdditionalAnswers.returnsFirstArg());

        // When
        orderService.create(order);

        // Then
        verify(menuDao).countByIdIn(anyList());
        verify(orderTableDao).findById(any());
        verify(orderDao).save(any(Order.class));
        verify(orderLineItemDao, times(order.getOrderLineItems().size())).save(any(OrderLineItem.class));
    }

    @Test
    @DisplayName("주문에 주문 항목이 없는 경우 예외가 발생한다.")
    public void throwException_WhenOrderLineItemIsEmpty() {
        // Given
        order.setOrderLineItems(Collections.emptyList());

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));
    }

    @Test
    @DisplayName("DB에 존재하는 메뉴보다 주문한 메뉴가 많은 경우 예외가 발생한다.")
    public void throwException_WhenOrderMenuCountIsOverThanPersistMenusCount() {
        // Given
        given(menuDao.countByIdIn(anyList())).willReturn(0L);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));

        verify(menuDao).countByIdIn(anyList());
    }

    @Test
    @DisplayName("주문에 포함된 주문 테이블이 DB에 존재하지 않는 경우 예외가 발생한다.")
    public void throwException_WhenOrderTableIsNotExist() {
        // Given
        given(menuDao.countByIdIn(anyList())).willReturn((long) order.getOrderLineItems().size());
        given(orderTableDao.findById(any())).willThrow(IllegalArgumentException.class);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));

        verify(menuDao).countByIdIn(anyList());
        verify(orderTableDao).findById(any());
    }

    @Test
    @DisplayName("주문에 포함된 주문테이블이 비어있는경우(주문을 요청한 테이블이 `isEmpty() = true`인 경우) 예외가 발생한다.")
    public void throwException_When() {
        given(menuDao.countByIdIn(anyList())).willReturn((long) order.getOrderLineItems().size());
        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        orderTable.setEmpty(true);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.create(order));

        verify(menuDao).countByIdIn(anyList());
        verify(orderTableDao).findById(any());
    }

    @Test
    @DisplayName("주문 목록을 조회한다.")
    public void getAllOrders() {
        // Given
        given(orderDao.findAll()).willReturn(Collections.singletonList(order));

        // When
        orderService.list();

        // Then
        verify(orderDao).findAll();
        verify(orderLineItemDao).findAllByOrderId(any());
    }

    @Test
    @DisplayName("주문 상태를 변경한다.")
    public void changeOrderStatus() {
        // Given
        given(orderDao.findById(any())).willReturn(Optional.of(order));

        Order updateOrderRequest = new Order();
        String newOrderStatus = OrderStatus.MEAL.name();
        updateOrderRequest.setOrderStatus(newOrderStatus);

        // When
        Order actualOrder = orderService.changeOrderStatus(order.getId(), updateOrderRequest);

        // Then
        verify(orderDao).findById(any());
        verify(orderDao).save(any(Order.class));
        verify(orderLineItemDao).findAllByOrderId(any());
        assertThat(actualOrder.getOrderStatus()).isEqualTo(newOrderStatus);
    }

    @Test
    @DisplayName("주문이 존재하지 않는 경우 예외가 발생한다.")
    public void throwException_WhenOrderIsNotExist() {
        // Given
        given(orderDao.findById(any())).willThrow(IllegalArgumentException.class);

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.changeOrderStatus(any(), order));

        verify(orderDao).findById(any());
    }

    @Test
    @DisplayName("주문 상태 값이 없는 경우 예외가 발생한다.")
    public void throwException_WhenOrderStatusIsNull() {
        // Given
        Order order = new Order();
        given(orderDao.findById(any())).willReturn(Optional.of(order));

        // When & Then
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> orderService.changeOrderStatus(order.getId(), order));

        verify(orderDao).findById(any());
    }

    @Test
    @DisplayName("주문이 완료 상태인 경우 예외가 발생한다.")
    public void throwException_WhenOrderStatusIsCompletion() {
        // Given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        given(orderDao.findById(any())).willReturn(Optional.of(order));

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderService.changeOrderStatus(any(), order));

        verify(orderDao).findById(any());
    }
}
