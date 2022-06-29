package kitchenpos.table.ui;

import kitchenpos.table.application.TableService;
import kitchenpos.table.dto.OrderTableRequestDto;
import kitchenpos.table.dto.OrderTableResponseDto;
import kitchenpos.ui.BaseRestControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static kitchenpos.fixture.OrderTableFixture.주문테이블_요청_데이터_생성;
import static kitchenpos.fixture.OrderTableFixture.주문테이블_응답_데이터_생성;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TableRestControllerTest extends BaseRestControllerTest {

    @Mock
    private TableService tableService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TableRestController(tableService)).build();
    }

    @DisplayName("테이블을 생성한다.")
    @Test
    void create() throws Exception {
        //given
        OrderTableRequestDto request = 주문테이블_요청_데이터_생성(4);
        String requestBody = objectMapper.writeValueAsString(request);

        OrderTableResponseDto response = 주문테이블_응답_데이터_생성(1L, 4, false, null);
        given(tableService.create(any())).willReturn(response);

        //when //then
        mockMvc.perform(post("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @DisplayName("테이블 전체를 조회한다.")
    @Test
    void list() throws Exception {
        //given
        OrderTableResponseDto response = 주문테이블_응답_데이터_생성(1L, 4, false, null);
        given(tableService.list()).willReturn(Arrays.asList(response));

        //when //then
        mockMvc.perform(get("/api/tables"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @DisplayName("빈테이블로 변경한다.")
    @Test
    void changeEmpty() throws Exception {
        //given
        Long orderTableId = 1L;

        OrderTableResponseDto response = 주문테이블_응답_데이터_생성(1L, 4, true, null);
        given(tableService.changeEmpty(any())).willReturn(response);

        //when //then
        mockMvc.perform(put("/api/tables/{orderTableId}/empty", orderTableId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true));
    }

    @DisplayName("테이블의 손님수를 변경한다.")
    @Test
    void changeNumberOfGuests() throws Exception {
        //given
        Long orderTableId = 1L;
        int numberOfGuest = 4;
        OrderTableRequestDto request = 주문테이블_요청_데이터_생성(numberOfGuest);
        String requestBody = objectMapper.writeValueAsString(request);

        OrderTableResponseDto response = 주문테이블_응답_데이터_생성(1L, numberOfGuest, false, null);
        given(tableService.changeNumberOfGuests(any(), anyInt())).willReturn(response);

        //when //then
        mockMvc.perform(put("/api/tables/{orderTableId}/number-of-guests", orderTableId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfGuests").value(numberOfGuest));
    }
}