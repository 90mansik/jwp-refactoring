package kitchenpos.tableGroup.domain;

import kitchenpos.exception.ErrorMessage;
import kitchenpos.exception.IllegalOrderException;
import kitchenpos.exception.IllegalOrderTableException;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.orderTable.domain.OrderTable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TableGroup {
    public static final int MIN_ORDER_TABLE_NUMBER = 2;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdDate;
    @OneToMany(mappedBy = "tableGroup")
    private List<OrderTable> orderTables;

    public TableGroup() {
    }

    public TableGroup(Long id, LocalDateTime createdDate, List<OrderTable> orderTables) {
        this.id = id;
        this.createdDate = createdDate;
        assignOrderTables(orderTables);
    }

    public TableGroup(LocalDateTime createdDate, List<OrderTable> orderTables) {
        this.createdDate = createdDate;
        assignOrderTables(orderTables);
    }

    public static TableGroup from(List<OrderTable> orderTables) {
        return new TableGroup(LocalDateTime.now(), orderTables);
    }

    private void assignOrderTables(List<OrderTable> orderTables) {
        validateOrderTables(orderTables);
        this.orderTables = orderTables;
        orderTables.forEach(orderTable -> {
                    orderTable.setTableGroup(this);
                    orderTable.setEmpty(false);
                }
        );
    }

    private void validateOrderTables(List<OrderTable> orderTables) {
        if (orderTables == null || orderTables.size() < MIN_ORDER_TABLE_NUMBER) {
            throw new IllegalOrderTableException(
                    String.format(ErrorMessage.ERROR_ORDER_TABLE_TOO_SMALL, MIN_ORDER_TABLE_NUMBER)
            );
        }
        if(orderTables.size() != orderTables.stream().distinct().count()){
            throw new IllegalOrderTableException(ErrorMessage.ERROR_ORDER_TABLE_DUPLICATED);
        }
        if (orderTables.stream().
                anyMatch(orderTable -> !orderTable.isEmpty())) {
            throw new IllegalOrderTableException(ErrorMessage.ERROR_ORDER_TABLE_NOT_EMPTY);
        }
        if (orderTables.stream().
                anyMatch(orderTable -> orderTable.isGrouped())) {
            throw new IllegalOrderTableException(ErrorMessage.ERROR_ORDER_TABLE_GROUPED);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<OrderTable> getOrderTables() {
        return orderTables;
    }

    public void setOrderTables(final List<OrderTable> orderTables) {
        this.orderTables = orderTables;
    }

    public void ungroup(List<Order> orders) {
        validateOrdersToUngroup(orders);
        for (final OrderTable orderTable : orderTables) {
            orderTable.detachTableGroup();
        }
    }

    private void validateOrdersToUngroup(List<Order> orders) {
        if (orders.stream()
                .anyMatch(order -> order.isCooking() || order.isEating())) {
            throw new IllegalOrderException(
                    String.format(ErrorMessage.ERROR_ORDER_INVALID_STATUS, OrderStatus.COOKING + " " + OrderStatus.MEAL)
            );
        }
    }
}
