package kitchenpos.domain;

import kitchenpos.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("방문한 손님 수 관련 도메인 테스트")
public class NumberOfGuestsTest {

    @DisplayName("방문한 손님 수를 생성한다.")
    @Test
    void createNumberOfGuests() {
        // given
        int actualNumberOfGuests = 10;
        // when
        NumberOfGuests numberOfGuests = NumberOfGuests.from(actualNumberOfGuests);
        // then
        assertAll(
                () -> assertThat(numberOfGuests.getNumberOfGuests()).isEqualTo(actualNumberOfGuests),
                () -> assertThat(numberOfGuests).isEqualTo(NumberOfGuests.from(actualNumberOfGuests))
        );
    }

    @DisplayName("방문한 손님 수가 0보다 작으면 에러가 발생한다.")
    @Test
    void isValidNumberOfGuestsIsNegative() {
        // given
        int numberOfGuests = -3;
        // when
        assertThatThrownBy(
                () -> NumberOfGuests.from(numberOfGuests)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.NUMBER_OF_GUEST_NOT_NEGATIVE.getMessage());
    }
}
