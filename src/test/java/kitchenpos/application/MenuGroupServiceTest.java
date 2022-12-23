package kitchenpos.application;

import kitchenpos.dao.MenuGroupDao;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static kitchenpos.domain.MenuGroupTestFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 그룹 비즈니스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupService menuGroupservice;

    private MenuGroup chickenSet;
    private MenuGroup burgerSet;

    @BeforeEach
    void setUp() {
        chickenSet = createMenuGroup(1L, "chickenSet");
        burgerSet = createMenuGroup(2L, "burgerSet");
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void 메뉴_그룹_생성() {
        // given
        when(menuGroupDao.save(chickenSet)).thenReturn(chickenSet);
        // when
        MenuGroup createMenuGrep = menuGroupservice.create(chickenSet);
        // then
        assertAll(
                () -> assertThat(createMenuGrep.getId()).isNotNull(),
                () -> assertThat(createMenuGrep.getName()).isEqualTo(chickenSet.getName())
        );
    }

    @DisplayName("메뉴 그룹 전체 목록을 조회한다.")
    @Test
    void 메뉴_그룹_전체_목록_조회() {
        // given
        List<MenuGroup> menuGroups = Arrays.asList(chickenSet, burgerSet);
        when(menuGroupDao.findAll()).thenReturn(menuGroups);
        // when
        List<MenuGroup> findMenuGroups = menuGroupservice.list();
        // then
        assertAll(
                () -> assertThat(findMenuGroups).hasSize(2),
                () -> assertThat(findMenuGroups).containsExactly(chickenSet, burgerSet)
        );
    }

}
