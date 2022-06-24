package kitchenpos.menu.application;

import static kitchenpos.helper.MenuFixtures.통반세트_요청_만들기;
import static kitchenpos.helper.MenuGroupFixtures.없는메뉴_그룹;
import static kitchenpos.helper.MenuProductFixtures.반반치킨_메뉴상품_요청;
import static kitchenpos.helper.MenuProductFixtures.통구이_메뉴상품_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.menu.dto.MenuRequest;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.product.application.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayName("메뉴 관련 Service 기능 테스트")
@DataJpaTest
@Import({MenuService.class, ProductService.class, MenuGroupService.class})
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @DisplayName("메뉴를 등록한다.")
    @Test
    void create() {
        //given
        MenuRequest request = 통반세트_요청_만들기(30000);

        //when
        MenuResponse result = menuService.create(request);

        //then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getMenuProducts().get(0).getMenuId()).isEqualTo(result.getId()),
                () -> assertThat(result.getMenuProducts().get(1).getMenuId()).isEqualTo(result.getId())
        );
    }

    @DisplayName("메뉴 목록을 조회한다.")
    @Test
    void list() {

        //when
        List<MenuResponse> results = menuService.list();

        //then
        assertThat(results.stream().map(MenuResponse::getName).collect(Collectors.toList()))
                .contains("후라이드치킨", "양념치킨", "반반치킨", "통구이", "간장치킨", "순살치킨");

    }

    @DisplayName("메뉴 그룹이 등록 되어있지 않은 경우 메뉴를 등록 할 수 없다.")
    @Test
    void create_empty_menu_group_id() {
        //given
        MenuRequest request = new MenuRequest(null, "통반세트", 30000, 없는메뉴_그룹.getId(),
                Arrays.asList(통구이_메뉴상품_요청, 반반치킨_메뉴상품_요청));

        //when then
        assertThatIllegalArgumentException()
                .isThrownBy(() ->  menuService.create(request))
                .withMessageContaining("메뉴 그룹이 등록되어있지 않습니다.");

    }

    @DisplayName("메뉴 가격이 금액(가격 * 수량)보다 큰 경우 메뉴를 등록할 수 없다.")
    @Test
    void create_price_greater_than_amount() {
        //given
        MenuRequest request = 통반세트_요청_만들기(100_000);

        //when then
        assertThatIllegalArgumentException()
                .isThrownBy(() ->  menuService.create(request))
                .withMessageContaining("메뉴 가격은 총 금액을 넘을 수 없습니다.");

    }

    @DisplayName("메뉴 가격이 null 이거나 0원 미만이면 메뉴를 등록할 수 없다.")
    @Test
    void create_price_null_or_less_then_zero() {
        //given
        MenuRequest request_price_less_then_zero = 통반세트_요청_만들기(-1);
        MenuRequest request_price_null = 통반세트_요청_만들기(null);

        //when then
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request_price_less_then_zero)),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request_price_null))
        );

    }

}
