package kitchenpos.ui;

import kitchenpos.application.MenuService;
import kitchenpos.dto.MenuProductRequestDto;
import kitchenpos.dto.MenuProductResponseDto;
import kitchenpos.dto.MenuRequestDto;
import kitchenpos.dto.MenuResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static kitchenpos.fixture.MenuFixture.메뉴_요청_데이터_생성;
import static kitchenpos.fixture.MenuFixture.메뉴_응답_데이터_생성;
import static kitchenpos.fixture.MenuProductFixture.메뉴상품_요청_데이터_생성;
import static kitchenpos.fixture.MenuProductFixture.메뉴상품_응답_데이터_생성;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuRestControllerTest extends BaseRestControllerTest {

    @Mock
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MenuRestController(menuService)).build();
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() throws Exception {
        //given
        String name = "menu";
        BigDecimal price = BigDecimal.valueOf(1000);
        Long menuGroupId = 1L;
        List<MenuProductRequestDto> menuProducts = Arrays.asList(메뉴상품_요청_데이터_생성(1L, 2));
        MenuRequestDto request = 메뉴_요청_데이터_생성(name, price, menuGroupId, menuProducts);
        String requestBody = objectMapper.writeValueAsString(request);

        Long id = 1L;
        MenuProductResponseDto menuProduct = 메뉴상품_응답_데이터_생성(1L, 1L, 1L, 2);
        MenuResponseDto menu = 메뉴_응답_데이터_생성(id, "menu", price, menuGroupId, Arrays.asList(menuProduct));
        given(menuService.create(any())).willReturn(menu);

        //when //then
        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id));
    }

    @DisplayName("메뉴와 메뉴상품을 전체 조회한다.")
    @Test
    void list() throws Exception {
        //given
        Long id = 1L;
        Long menuGroupId = 1L;
        BigDecimal price = BigDecimal.valueOf(1000);
        MenuProductResponseDto menuProduct = 메뉴상품_응답_데이터_생성(1L, 1L, 1L, 2);
        MenuResponseDto menu = 메뉴_응답_데이터_생성(id, "menu", price, menuGroupId, Arrays.asList(menuProduct));

        given(menuService.list()).willReturn(Arrays.asList(menu));

        //when //then
        mockMvc.perform(get("/api/menus"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

}