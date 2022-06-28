package kitchenpos.menugroup.application;

import kitchenpos.application.MenuGroupService;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.menugroup.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {
    public static final String MENU_GROUP_NAME01 = "점심특선메뉴";
    public static final String MENU_GROUP_NAME02 = "어린이전용메뉴";

    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        when(menuGroupRepository.save(any())).thenReturn(createMenuGroup01());

        // when
        MenuGroup created = menuGroupService.create(new MenuGroup(MENU_GROUP_NAME01));

        // then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
    }

    @DisplayName("메뉴 그룹 목록을 조회한다.")
    @Test
    void list() {
        when(menuGroupRepository.findAll()).thenReturn(createMenuGroupList());

        // when
        List<MenuGroup> list = menuGroupService.list();

        // then
        assertThat(list).isNotNull();
    }

    public static MenuGroup createMenuGroup01() {
        return new MenuGroup(1L, MENU_GROUP_NAME01);
    }

    public static MenuGroup createMenuGroup02() {
        return new MenuGroup(1L, MENU_GROUP_NAME02);
    }

    public static List<MenuGroup> createMenuGroupList() {
        return Arrays.asList(createMenuGroup01(), createMenuGroup02());
    }
}
