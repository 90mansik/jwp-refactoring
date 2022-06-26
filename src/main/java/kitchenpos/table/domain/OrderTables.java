package kitchenpos.table.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import kitchenpos.exception.InvalidTableNumberException;
import kitchenpos.order.dto.OrderTableResponse;

@Embeddable
public class OrderTables {
    @OneToMany(mappedBy = "tableGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderTable> orderTables = new ArrayList<>();

    protected OrderTables() {
    }

    public OrderTables(List<OrderTable> orderTables) {
        validateGrouping(orderTables);
        this.orderTables = orderTables;
    }

    private void validateGrouping(List<OrderTable> orderTables) {
        if (orderTables.size() < 2) {
            throw new InvalidTableNumberException();
        }
        orderTables.forEach(OrderTable::validateGrouping);
    }

    public List<OrderTable> get() {
        return orderTables;
    }

    public void ungroup() {
        this.orderTables.forEach(OrderTable::ungroupTable);
    }

    public List<OrderTableResponse> toOrderTableResponses() {
        return this.orderTables.stream()
                .map(OrderTable::toOrderTableResponse)
                .collect(Collectors.toList());
    }
}
