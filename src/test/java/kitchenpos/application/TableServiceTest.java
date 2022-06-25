package kitchenpos.application;

import static kitchenpos.fixture.OrderTableFixture.주문테이블_데이터_생성;
import static kitchenpos.fixture.OrderTableFixture.주문테이블_요청_데이터_생성;
import static kitchenpos.fixture.TableGroupFixture.단체_데이터_생성;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.OrderTableRequestDto;
import kitchenpos.dto.OrderTableResponseDto;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    private TableService tableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        tableService = new TableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블을 생성한다.")
    @Test
    void create() {
        //given
        int numberOfGuests = 4;
        OrderTableRequestDto request = 주문테이블_요청_데이터_생성(numberOfGuests);

        Long id = 1L;
        given(orderTableRepository.save(any()))
            .willReturn(주문테이블_데이터_생성(id, null, numberOfGuests, false));

        //when
        OrderTableResponseDto response = tableService.create(request);

        //then
        주문테이블_데이터_확인(response, id, numberOfGuests, false);
    }

    @DisplayName("테이블 전체를 조회한다.")
    @Test
    void list() {
        //given
        Long id = 1L;
        int numberOfGuests = 4;
        given(orderTableRepository.findAll())
            .willReturn(Arrays.asList(주문테이블_데이터_생성(id, null, numberOfGuests, false)));

        //when
        List<OrderTableResponseDto> response = tableService.list();

        //then
        assertEquals(1, response.size());
        주문테이블_데이터_확인(response.get(0), id, numberOfGuests, false);
    }

    @DisplayName("빈테이블로 변경한다.")
    @Test
    void changeEmpty() {
        //given
        Long orderTableId = 1L;
        given(orderTableRepository.findById(orderTableId))
            .willReturn(Optional.of(주문테이블_데이터_생성(orderTableId, null, 4, false)));
        given(orderRepository.existsByOrderTableIdAndOrderStatusIn(any(), anyList())).willReturn(false);

        //when
        OrderTableResponseDto response = tableService.changeEmpty(orderTableId);

        //then
        주문테이블_데이터_확인(response, orderTableId, 4, true);
    }

    @DisplayName("테이블이 없으면 변경할 수 없다.")
    @Test
    void changeEmpty_fail_notExistsOrderTable() {
        //given
        Long orderTableId = 1L;

        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.empty());

        //when //then
        assertThatExceptionOfType(EntityNotFoundException.class)
            .isThrownBy(() -> tableService.changeEmpty(orderTableId));
    }

    @DisplayName("단체 지정된 테이블이면 변경할 수 없다.")
    @Test
    void changeEmpty_fail_tableGroupId() {
        //given
        Long orderTableId = 1L;
        TableGroup tableGroup = 단체_데이터_생성(1L);

        given(orderTableRepository.findById(orderTableId))
                .willReturn(Optional.of(주문테이블_데이터_생성(orderTableId, tableGroup, 4, false)));

        //when //then
        assertThatIllegalArgumentException().isThrownBy(() -> tableService.changeEmpty(orderTableId));
    }

    @DisplayName("요리중, 식사중 주문이 있으면 변경할 수 없다.")
    @Test
    void changeEmpty_fail_invalidOrderStatus() {
        //given
        Long orderTableId = 1L;

        given(orderTableRepository.findById(orderTableId))
                .willReturn(Optional.of(주문테이블_데이터_생성(orderTableId, null, 4, false)));
        given(orderRepository.existsByOrderTableIdAndOrderStatusIn(any(), anyList())).willReturn(true);

        //when //then
        assertThatIllegalArgumentException().isThrownBy(() -> tableService.changeEmpty(orderTableId));
    }

    @DisplayName("테이블의 손님수를 변경한다.")
    @Test
    void changeNumberOfGuests() {
        //given
        Long orderTableId = 1L;
        int numberOfGuests = 4;

        given(orderTableRepository.findById(orderTableId)).willReturn(
            Optional.of(주문테이블_데이터_생성(orderTableId, null, 2, false)));

        //when
        OrderTableResponseDto response = tableService.changeNumberOfGuests(orderTableId, numberOfGuests);

        //then
        주문테이블_데이터_확인(response, orderTableId, numberOfGuests, false);
    }

    @DisplayName("등록된 주문 테이블이 아니면 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_fail_notExistsOrderTable() {
        //given
        Long orderTableId = 1L;
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        //when //then
        assertThatExceptionOfType(EntityNotFoundException.class)
            .isThrownBy(() -> tableService.changeNumberOfGuests(orderTableId, 4));
    }

    private void 주문테이블_데이터_확인(OrderTableResponseDto response, Long id, int numberOfGuests, boolean empty) {
        assertAll(
                () -> assertEquals(id, response.getId()),
                () -> assertEquals(numberOfGuests, response.getNumberOfGuests()),
                () -> assertEquals(empty, response.isEmpty())
        );
    }

}