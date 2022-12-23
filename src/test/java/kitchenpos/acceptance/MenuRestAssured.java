package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.domain.MenuTestFixture.createMenu;

public class MenuRestAssured {

    public static ExtractableResponse<Response> createMenuRequest(Long id, String name, BigDecimal price, Long menuGroupId, List<MenuProduct> menuProducts) {
        Menu menus = createMenu(id, name, price, menuGroupId, menuProducts);

        return createMenuRequest(menus);
    }

    public static ExtractableResponse<Response> createMenuRequest(Menu menus) {

        return RestAssured.given().log().all()
                .body(menus)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/menus")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> selectMenusRequest() {
        return RestAssured.given().log().all()
                .when().get("/api/menus")
                .then().log().all()
                .extract();
    }
}
