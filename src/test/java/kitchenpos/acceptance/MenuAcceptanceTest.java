package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kitchenpos.acceptance.MenuGroupRestAssured.createMenuGroupRequest;
import static kitchenpos.acceptance.MenuRestAssured.selectMenusRequest;
import static kitchenpos.acceptance.ProductRestAssured.createProductRequest;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴 관련 인수 테스트")
public class MenuAcceptanceTest extends AbstractAcceptanceTest {
    private Product beefBurger;
    private Product cheeseBurger;
    private Product coke;
    private MenuGroup burgerSet;
    private MenuProduct beefBurgerProduct;
    private MenuProduct cokeProduct;
    private MenuProduct cheeseBurgerProduct;


    @BeforeEach
    public void setUp() {
        super.setUp();

        burgerSet = createMenuGroupRequest(createMenuGroup("햄버거세트")).as(MenuGroup.class);
        cheeseBurger = createProductRequest(createProduct(null, "치즈버거", BigDecimal.valueOf(6000))).as(Product.class);
        beefBurger = createProductRequest(createProduct(null, "불고기버거", BigDecimal.valueOf(5000))).as(Product.class);
        coke = createProductRequest(createProduct(null, "콜라", BigDecimal.valueOf(3000))).as(Product.class);
        cheeseBurgerProduct = createMenuProduct(1L, null, cheeseBurger.getId(), 1L);
        cokeProduct = createMenuProduct(2L, null, coke.getId(), 1L);
        beefBurgerProduct = createMenuProduct(3L, null, beefBurger.getId(), 1L);

    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void 메뉴_생성() {
        // when
        ExtractableResponse<Response> response = MenuRestAssured.createMenuRequest(1L, "불고기버거세트", BigDecimal.valueOf(7500), burgerSet.getId(), Arrays.asList(beefBurgerProduct, cokeProduct));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("상품 전체 목록을 조회한다.")
    @Test
    void 상품_전체_목록_조회() {
        // given
        ExtractableResponse<Response> response1 = MenuRestAssured.createMenuRequest(1L, "불고기버거세트", BigDecimal.valueOf(7500), burgerSet.getId(), Arrays.asList(beefBurgerProduct, cokeProduct));
        ExtractableResponse<Response> response2 = MenuRestAssured.createMenuRequest(2L, "치즈버거세트", BigDecimal.valueOf(8000), burgerSet.getId(), Arrays.asList(cheeseBurgerProduct, cokeProduct));
        // when
        ExtractableResponse<Response> menuResponse = selectMenusRequest();
        // then
        assertAll(
                () -> assertThat(menuResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> 메뉴_목록_포함_확인(menuResponse, Arrays.asList(response1, response2))
        );
    }
    
    private static void 메뉴_목록_포함_확인(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> responses) {
        List<Long> expectedMenuIds = responses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[3]))
                .collect(Collectors.toList());
        List<Long> resultMenuIds = response.jsonPath().getList(".", Menu.class).stream()
                .map(Menu::getId)
                .collect(Collectors.toList());

        assertThat(resultMenuIds).containsAll(expectedMenuIds);
    }


}
