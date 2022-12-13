package kitchenpos.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderTableAndOrderStatusIn(OrderTable orderTable, List<String> statusList);

    boolean existsByOrderTable_IdInAndOrderStatusIn(List<Long> orderTableIds, List<String> statusList);
}
