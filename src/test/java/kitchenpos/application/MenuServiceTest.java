package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static kitchenpos.domain.MenuTestFixture.createMenu;
import static kitchenpos.domain.MenuTestFixture.createMenuProduct;
import static kitchenpos.domain.ProductTestFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuService menuService;

    private Product honeyCombo;
    private Product cheeseBurger;
    private Product coke;
    private MenuGroup chickenSet;

    private MenuGroup burgerSet;
    private MenuProduct honeyComboProduct;
    private MenuProduct cokeProduct;

    private MenuProduct cheeseBurgerProduct;
    private Menu honeyComboSet;

    private Menu cheeseBurgerSet;


    @BeforeEach
    void setUp() {
        // 상품
        honeyCombo = createProduct(1L, "honeyCombo", BigDecimal.valueOf(18000));
        coke = createProduct(2L, "coke", BigDecimal.valueOf(3000));
        cheeseBurger = createProduct(3L, "cheeseBurger", BigDecimal.valueOf(6000));

        // 메뉴그룹
        chickenSet = createMenuGroup(1L, "chickenSet");
        burgerSet = createMenuGroup(2L, "burgerSet");

        // 메뉴 상품
        honeyComboProduct = createMenuProduct(1L, null, honeyCombo.getId(), 1L);
        cokeProduct = createMenuProduct(2L, null, coke.getId(), 1L);
        cheeseBurgerProduct = createMenuProduct(3L, null, cheeseBurger.getId(), 1L);


        honeyComboSet = createMenu(1L, "honeyComboSet", BigDecimal.valueOf(20000), chickenSet.getId(), Arrays.asList(honeyComboProduct, cokeProduct));
        cheeseBurgerSet = createMenu(2L, "cheeseBurgerSet", BigDecimal.valueOf(8000), burgerSet.getId(), Arrays.asList(cheeseBurgerProduct, cokeProduct));
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void 메뉴_생성() {
        // given
        when(menuGroupDao.existsById(honeyComboSet.getMenuGroupId())).thenReturn(true);
        when(productDao.findById(honeyComboProduct.getProductId())).thenReturn(Optional.of(honeyCombo));
        when(productDao.findById(cokeProduct.getProductId())).thenReturn(Optional.of(coke));
        when(menuDao.save(honeyComboSet)).thenReturn(honeyComboSet);
        when(menuProductDao.save(honeyComboProduct)).thenReturn(honeyComboProduct);
        when(menuProductDao.save(cokeProduct)).thenReturn(cokeProduct);
        // when
        Menu saveMenu = menuService.create(honeyComboSet);
        // then
        assertAll(
                () -> assertThat(saveMenu.getId()).isNotNull(),
                () -> assertThat(saveMenu.getMenuProducts()).containsExactly(honeyComboProduct, cokeProduct)
        );
    }

    @DisplayName("메뉴 전체 목록을 조회한다.")
    @Test
    void 메뉴_전체_목록_조회() {
        // given
        List<Menu> menus = Arrays.asList(cheeseBurgerSet, honeyComboSet);
        when(menuDao.findAll()).thenReturn(menus);
        when(menuProductDao.findAllByMenuId(cheeseBurgerSet.getId())).thenReturn(Arrays.asList(cheeseBurgerProduct, cokeProduct));
        when(menuProductDao.findAllByMenuId(honeyComboSet.getId())).thenReturn(Arrays.asList(honeyComboProduct, cokeProduct));

        // when
        List<Menu> findMenus = menuService.list();

        // then
        assertAll(
                () -> assertThat(findMenus).hasSize(menus.size()),
                () -> assertThat(findMenus).containsExactly(cheeseBurgerSet, honeyComboSet)
        );
    }

    @DisplayName("가격이 0원 미만인 메뉴는 생성할 수 없다.")
    @Test
    void 메뉴_생성_예외1() {
        // given
        Menu menu = createMenu(1L, "cheeseBurgerSet", BigDecimal.valueOf(-1000), burgerSet.getId(), Arrays.asList(cheeseBurgerProduct, cokeProduct));

        // when & then
        Assertions.assertThatThrownBy(
                () -> menuService.create(menu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 메뉴 그룹에 속하지 않은 메뉴는 생성할 수 없다.")
    @Test
    void 메뉴_생성_예외2() {
        // given
        Menu menu = createMenu(1L, "cheeseBurgerSet", BigDecimal.valueOf(8000L), burgerSet.getId(), singletonList(cokeProduct));
        when(menuGroupDao.existsById(cheeseBurgerSet.getMenuGroupId())).thenReturn(true);
        when(productDao.findById(cokeProduct.getProductId())).thenReturn(Optional.empty());
        // when & then
        Assertions.assertThatThrownBy(
                () -> menuService.create(menu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격은 해당 메뉴에 존재하는 메뉴 상품들의 가격의 합보다 클 수 없다.")
    @Test
    void 메뉴_생성_예외3() {
        // given
        Menu menu = createMenu(1L, "cheeseBurgerSet", BigDecimal.valueOf(9000L), burgerSet.getId(), Arrays.asList(cheeseBurgerProduct, cokeProduct));
        // when && then
        Assertions.assertThatThrownBy(
                () -> menuService.create(menu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

}
