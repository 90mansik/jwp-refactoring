package kitchenpos.table.application;

import static kitchenpos.common.ErrorMessage.NOT_EXIST_ORDER_TABLE;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.exception.NotCompletionStatusException;
import kitchenpos.exception.NotExistException;
import kitchenpos.order.dto.OrderTableRequest;
import kitchenpos.order.dto.OrderTableResponse;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.repository.OrderRepository;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.repository.OrderTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableService {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;

    public TableService(OrderRepository orderRepository, OrderTableRepository orderTableRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public OrderTableResponse create(final OrderTableRequest orderTableRequest) {
        final OrderTable orderTable = orderTableRequest.toOrderTable();
        final OrderTable persist = orderTableRepository.save(orderTable);
        return persist.toOrderTableResponse();
    }

    @Transactional(readOnly = true)
    public List<OrderTableResponse> list() {
        return orderTableRepository.findAll().stream()
                .map(OrderTable::toOrderTableResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderTableResponse changeEmpty(final Long orderTableId) {
        final OrderTable persistOrderTable = getOrderTable(orderTableId);

        if (orderRepository.existsOrdersByOrderTableIdAndOrderStatusNot(orderTableId, OrderStatus.COMPLETION)) {
            throw new NotCompletionStatusException();
        }

        persistOrderTable.validateExistGroupingTable();
        persistOrderTable.changeEmpty();
        return persistOrderTable.toOrderTableResponse();
    }

    @Transactional
    public OrderTableResponse changeNumberOfGuests(final Long orderTableId, final Integer guestNumber) {
        final OrderTable persistOrderTable = getOrderTable(orderTableId);
        persistOrderTable.changeGuestNumber(guestNumber);
        return persistOrderTable.toOrderTableResponse();
    }

    private OrderTable getOrderTable(Long orderTableId) {
        return orderTableRepository.findById(orderTableId)
                .orElseThrow(() -> new NotExistException(NOT_EXIST_ORDER_TABLE));
    }
}
