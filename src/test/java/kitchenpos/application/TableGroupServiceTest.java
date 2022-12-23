package kitchenpos.application;


import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static kitchenpos.domain.OrderTableTestFixture.createOrderTable;
import static kitchenpos.domain.TableGroupTestFixture.createTableGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("단체 지정 관련 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class TableGroupServiceTest {
    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupService tableGroupService;

    private OrderTable orderTable1;
    private OrderTable orderTable2;
    private TableGroup group1;

    @BeforeEach
    void setUp() {
        orderTable1 = createOrderTable( 5, true);
        orderTable2 = createOrderTable(4, true);
        group1 = createTableGroup(1L, null, Arrays.asList(orderTable1, orderTable2));
    }

    @DisplayName("주문 테이블들에 대해 단체를 설정한다.")
    @Test
    void 단체_생성() {
        // given
        when(orderTableDao.findAllByIdIn(Arrays.asList(orderTable1.getId(), orderTable2.getId()))).thenReturn(Arrays.asList(orderTable1, orderTable2));
        when(tableGroupDao.save(group1)).thenReturn(group1);
        when(orderTableDao.save(orderTable1)).thenReturn(orderTable1);
        when(orderTableDao.save(orderTable2)).thenReturn(orderTable2);
        // when
        TableGroup saveTableGroup = tableGroupService.create(group1);
        // then
        assertAll(
                () -> assertThat(orderTable1.getTableGroupId()).isEqualTo(saveTableGroup.getId()),
                () -> assertThat(orderTable2.getTableGroupId()).isEqualTo(saveTableGroup.getId()),
                () -> assertThat(orderTable1.isEmpty()).isFalse(),
                () -> assertThat(orderTable2.isEmpty()).isFalse(),
                () -> assertThat(saveTableGroup.getCreatedDate()).isNotNull()
        );
    }

    @DisplayName("단체 지정을 해제한다.")
    @Test
    void 단체_지정_해제() {
        // given
        when(orderTableDao.findAllByTableGroupId(group1.getId())).thenReturn(Arrays.asList(orderTable1, orderTable2));
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(Arrays.asList(orderTable1.getId(), orderTable2.getId()),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(false);
        when(orderTableDao.save(orderTable1)).thenReturn(orderTable1);
        when(orderTableDao.save(orderTable2)).thenReturn(orderTable2);
        // when
        tableGroupService.ungroup(group1.getId());
        // then
        assertAll(
                () -> assertThat(orderTable1.getTableGroupId()).isNull(),
                () -> assertThat(orderTable2.getTableGroupId()).isNull()
        );
    }

    @DisplayName("생성하고자 하는 단체에 속하는 주문 테이블이 2개 미만이면 예외가 발생한다.")
    @Test
    void 단체_생성_예외1() {
        // given
        TableGroup tableGroup = createTableGroup(2L, null, singletonList(orderTable1));
        // when & then
        Assertions.assertThatThrownBy(
                () -> tableGroupService.create(tableGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 주문 테이블을 가진 단체는 생성할 수 없다.")
    @Test
    void 단체_생성_예외2() {
        // given
        when(orderTableDao.findAllByIdIn(Arrays.asList(orderTable1.getId(), orderTable2.getId()))).thenReturn(singletonList(orderTable1));
        // when & then
        Assertions.assertThatThrownBy(
                () -> tableGroupService.create(group1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단체 생성 시, 해당 단체에 속할 주문 테이블들 중 빈 테이블이 아닌 테이블이 있으면 예외가 발생한다.")
    @Test
    void 단체_생성_예외3() {
        // given
        OrderTable orderTable = createOrderTable( 5, false);
        TableGroup tableGroup = createTableGroup(3L, null, Arrays.asList(orderTable, orderTable1));
        when(orderTableDao.findAllByIdIn(Arrays.asList(orderTable.getId(), orderTable1.getId()))).thenReturn(
                Arrays.asList(orderTable, orderTable1));

        // when & then
        Assertions.assertThatThrownBy(
                () -> tableGroupService.create(tableGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단체 생성 시, 다른 단체에 포함된 주문 테이블이 있다면 예외가 발생한다.")
    @Test
    void 단체_생성_예외4() {
        // given
        OrderTable orderTable = createOrderTable(5, true);
        TableGroup tableGroup = createTableGroup(3L, null, Arrays.asList(orderTable, orderTable1));
        when(orderTableDao.findAllByIdIn(Arrays.asList(orderTable.getId(), orderTable1.getId()))).thenReturn(
                Arrays.asList(orderTable, orderTable1));
        // when & then
        Assertions.assertThatThrownBy(
                () -> tableGroupService.create(tableGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("단체 내 주문 테이블의 상태가 조리중이거나 식사중이면 단체 지정을 해제할 수 없다.")
    @Test
    void upGroupThrowErrorWhenOrderTableStatusIsCookingOrMeal() {
        // given
        when(orderTableDao.findAllByTableGroupId(group1.getId())).thenReturn(Arrays.asList(orderTable1, orderTable2));
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(Arrays.asList(orderTable1.getId(), orderTable2.getId()),
                Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(true);
        // when & then
        Assertions.assertThatThrownBy(
                () -> tableGroupService.ungroup(group1.getId())
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
