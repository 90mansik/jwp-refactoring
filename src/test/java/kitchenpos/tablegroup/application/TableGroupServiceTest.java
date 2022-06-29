package kitchenpos.tablegroup.application;

import kitchenpos.application.TableGroupService;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTableRepository;
import kitchenpos.tablegroup.domain.TableGroup;
import kitchenpos.tablegroup.domain.TableGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TableGroupServiceTest {
    private TableGroupService tableGroupService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderTableRepository orderTableDao;

    @Mock
    private TableGroupRepository tableGroupDao;

    @BeforeEach
    void setUp() {
        tableGroupService = new TableGroupService(orderRepository, orderTableDao, tableGroupDao);
    }

    @DisplayName("테이블 그룹을 생성한다.")
    @Test
    void create() {
        when(orderTableDao.findAllById(any())).thenReturn(createTableGroup().getOrderTables());
        when(tableGroupDao.save(any())).thenReturn(createTableGroup());
        when(orderTableDao.save(any())).thenReturn(new OrderTable(3, false));

        // when
        TableGroup tableGroup = tableGroupService.create(createTableGroup());

        // then
        assertThat(tableGroup).isNotNull();
    }

    @DisplayName("[예외] 주문 테이블 하나만으로 테이블 그룹 생성한다.")
    @Test
    void createTableGroup_with_one_order_table() {
        // when, then
        assertThatThrownBy(() -> {
            tableGroupService.create(createTableGroupWithOneOrderTable());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 저장 안된 주문 테이블을 포함하여 테이블 그룹 생성한다.")
    @Test
    void createTableGroup_with_not_saved_order_table() {
        when(orderTableDao.findAllById(any()))
                .thenReturn(Arrays.asList(new OrderTable(3L), new OrderTable(4L), new OrderTable(5L)));

        // when, then
        assertThatThrownBy(() -> {
            tableGroupService.create(createTableGroup());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 이용 중인 주문 테이블을 포함하여 테이블 그룹 생성한다.")
    @Test
    void createTableGroup_with_not_empty_order_table() {
        when(orderTableDao.findAllById(any()))
                .thenReturn(createTableGroupWithNotEmptyOrderTable().getOrderTables());

        // when, then
        assertThatThrownBy(() -> {
            tableGroupService.create(createTableGroup());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 이미 다른 테이블 그룹과 연결된 주문 테이블로 테이블 그룹 생성한다.")
    @Test
    void createTableGroup_with_order_table_already_mapping_with_other_table_group() {
        when(orderTableDao.findAllById(any()))
                .thenReturn(createTableGroupWithMappingWithOtherTableGroup().getOrderTables());

        // when, then
        assertThatThrownBy(() -> {
            tableGroupService.create(createTableGroup());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식사 완료된 테이블 그룹과 주문 정보 간에 관계를 해지한다.")
    @Test
    void ungroup() {
        when(orderTableDao.findAllByTableGroup(any())).thenReturn(createTableGroup().getOrderTables());
        when(orderRepository.existsByOrderTableIdInAndOrderStatusIn(any(), any())).thenReturn(false);
        when(orderTableDao.save(any())).thenReturn(new OrderTable(3, false));

        // when, then
        tableGroupService.ungroup(1L);
    }

    @DisplayName("[예외] 식사 중인 테이블 그룹과 주문 정보 간에 관계를 해지한다.")
    @Test
    void ungroup_not_completion_order() {
        when(orderTableDao.findAllByTableGroup(any())).thenReturn(createTableGroup().getOrderTables());
        when(orderRepository.existsByOrderTableIdInAndOrderStatusIn(any(), any())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> {
            tableGroupService.ungroup(1L);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    public static TableGroup createTableGroup() {
        OrderTable orderTable1 = new OrderTable(1L,3, true);
        OrderTable orderTable2 = new OrderTable(2L,5, true);
        return new TableGroup(1L, Arrays.asList(orderTable1, orderTable2));
    }

    public static TableGroup createTableGroupWithOneOrderTable() {
        OrderTable orderTable1 = new OrderTable(1L,3, true);
        return new TableGroup(1L, Arrays.asList(orderTable1));
    }

    public static TableGroup createTableGroupWithNotEmptyOrderTable() {
        OrderTable orderTable1 = new OrderTable(1L,3, false);
        OrderTable orderTable2 = new OrderTable(2L,5, false);
        return new TableGroup(1L, Arrays.asList(orderTable1, orderTable2));
    }

    public static TableGroup createTableGroupWithMappingWithOtherTableGroup() {
        OrderTable orderTable1 = new OrderTable(1L, new TableGroup(1L), 3, true);
        OrderTable orderTable2 = new OrderTable(2L, new TableGroup(1L),5, true);
        return new TableGroup(1L, Arrays.asList(orderTable1, orderTable2));
    }
}
