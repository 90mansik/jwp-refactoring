package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import kitchenpos.domain.common.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("이름 관련 테스트")
public class NameTest {

    @DisplayName("이름을 생성한다.")
    @Test
    void createName() {
        // given
        String actualName = "치즈버거";
        // when
        Name name = Name.from(actualName);
        // then
        assertThat(actualName).isEqualTo(name.getName());
    }

    @DisplayName("이름에 Null 값을 넣으면 에러가 발생한다.")
    @Test
    void isValidNameIsnull() {
        // when && then
        assertThatThrownBy(
                () -> Name.from(null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.NAME_NOT_EMPTY.getMessage());
    }

    @DisplayName("이름에 빈 값을 넣으면 에러가 발생한다.")
    @Test
    void isValidNameIsEmpty() {
        // when && then
        assertThatThrownBy(
                () -> Name.from("")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.NAME_NOT_EMPTY.getMessage());
    }

}
