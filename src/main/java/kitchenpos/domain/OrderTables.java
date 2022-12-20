package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class OrderTables {

    private static final int MIN_SIZE = 2;

    private List<OrderTable> orderTables = new ArrayList<>();

    protected OrderTables() {
    }

    private OrderTables(List<OrderTable> orderTables) {
        isValidOrderTableMinSize(orderTables);
        this.orderTables = new ArrayList<>(orderTables);
    }

    public static OrderTables from(List<OrderTable> orderTables) {
        return new OrderTables(orderTables);
    }

    private void isValidOrderTableMinSize(List<OrderTable> orderTables) {
        if (orderTables.isEmpty() || orderTables.size() < MIN_SIZE) {
            throw new IllegalArgumentException(ErrorCode.ORDER_TABLE_NOT_MIN_SIZE.getMessage());
        }
    }

    public List<OrderTable> values() {
        return Collections.unmodifiableList(orderTables);
    }

    public void unGroupOrderTables() {
        orderTables.forEach(OrderTable::unGroup);
    }

}
