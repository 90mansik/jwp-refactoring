package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.acceptance.MenuGroupRestAssured.메뉴_그룹_생성_요청;
import static kitchenpos.acceptance.MenuRestAssured.메뉴_목록_조회_요청;
import static kitchenpos.acceptance.MenuRestAssured.메뉴_생성_요청;
import static kitchenpos.acceptance.ProductRestAssured.상품_생성_요청;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.ProductTestFixture.createProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴 관련 인수 테스트")
public class MenuAcceptanceTest extends AbstractAcceptanceTest {
    private ProductResponse bulgogiBurger;
    private ProductResponse cheeseBurger;
    private ProductResponse cola;
    private MenuGroup burgerSet;
    private MenuProduct bulgogiBurgerProduct;
    private MenuProduct colaProduct;
    private MenuProduct cheeseBurgerProduct;
    private Menu bulgogiBurgerSet;
    private Menu cheeseBurgerSet;

    @BeforeEach
    public void setUp() {
        super.setUp();

        burgerSet = 메뉴_그룹_생성_요청(createMenuGroup("burgerSet")).as(MenuGroup.class);
        cheeseBurger = 상품_생성_요청(createProductRequest("cheeseBurger", BigDecimal.valueOf(6000))).as(ProductResponse.class);
        bulgogiBurger = 상품_생성_요청(createProductRequest("bulgogiBurger", BigDecimal.valueOf(5000))).as(ProductResponse.class);
        cola = 상품_생성_요청(createProductRequest("cola", BigDecimal.valueOf(3000))).as(ProductResponse.class);
        cheeseBurgerProduct = createMenuProduct(1L, null, cheeseBurger.getId(), 1L);
        colaProduct = createMenuProduct(2L, null, cola.getId(), 1L);
        bulgogiBurgerProduct = createMenuProduct(3L, null, bulgogiBurger.getId(), 1L);
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void createMenuTest() {
        // when
        ExtractableResponse<Response> response = 메뉴_생성_요청(1L, "bulgogiBurgerSet", BigDecimal.valueOf(7500), burgerSet.getId(), Arrays.asList(bulgogiBurgerProduct, colaProduct));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("상품 전체 목록을 조회한다.")
    @Test
    void selectAllMenus() {
        // given
        ExtractableResponse<Response> response1 = 메뉴_생성_요청(1L, "bulgogiBurgerSet", BigDecimal.valueOf(7500), burgerSet.getId(), Arrays.asList(bulgogiBurgerProduct, colaProduct));
        ExtractableResponse<Response> response2 = 메뉴_생성_요청(2L, "cheeseBurgerSet", BigDecimal.valueOf(8000), burgerSet.getId(), Arrays.asList(cheeseBurgerProduct, colaProduct));
        // when
        ExtractableResponse<Response> menuResponse = 메뉴_목록_조회_요청();

        List<Long> expectedMenuIds = getExpectedMenuIds(Arrays.asList(response1, response2));
        List<Long> resultMenuIds = getResultMenuIds(menuResponse);
        // then
        assertAll(
                () -> assertThat(menuResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultMenuIds).containsAll(resultMenuIds)
        );
    }

    private static List<Long> getExpectedMenuIds(List<ExtractableResponse<Response>> responses) {
        return responses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[3]))
                .collect(Collectors.toList());
    }

    private static List<Long> getResultMenuIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", Menu.class).stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
    }

}
