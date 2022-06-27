package kitchenpos.tablegroup.dto;

import kitchenpos.table.domain.OrderTables;
import kitchenpos.table.dto.OrderTableResponse;
import kitchenpos.tablegroup.domain.TableGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TableGroupResponse {

    private Long id;

    private LocalDateTime createdDate;

    private List<OrderTableResponse> orderTables;

    protected TableGroupResponse() {
    }

    public TableGroupResponse(Long id, LocalDateTime createdDate, List<OrderTableResponse> orderTables) {
        this.id = id;
        this.createdDate = createdDate;
        this.orderTables = orderTables;
    }

    public static TableGroupResponse from(TableGroup tableGroup, OrderTables orderTables) {
        return new TableGroupResponse(tableGroup.getId(), tableGroup.getCreatedDate(),
                orderTables.getValues()
                        .stream()
                        .map(OrderTableResponse::from)
                        .collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public List<OrderTableResponse> getOrderTables() {
        return orderTables;
    }
}