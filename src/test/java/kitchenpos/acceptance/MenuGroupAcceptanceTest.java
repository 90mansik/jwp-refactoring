package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import kitchenpos.AcceptanceTest;
import kitchenpos.utils.KitchenPosBehaviors;
import org.junit.jupiter.api.DisplayName;
import kitchenpos.menu.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class MenuGroupAcceptanceTest extends AcceptanceTest {
    /**
     * When 메뉴 그룹을 생성한다.
     * Then 메뉴 그룹이 생성된다.
     * When 메뉴그룹 목록을 조회한다.
     * Then 메뉴그룹 목록이 조회된다.
     */
    @Test
    @DisplayName("메뉴그룹 생성 및 조회 기능 인수테스트")
    void menuGroupAcceptanceTest() {
        ExtractableResponse<Response> createResponse = KitchenPosBehaviors.메뉴그룹_생성_요청("치킨");
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<MenuGroup> menuGroups = KitchenPosBehaviors.메뉴그룹_목록조회();
        assertThat(menuGroups).hasSize(1);
    }
}
