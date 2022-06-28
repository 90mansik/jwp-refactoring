package kitchenpos.product.domain;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Price {
    @Column(nullable = false)
    private BigDecimal price;

    protected Price() {}

    private Price(BigDecimal value) {
        validatePrice(value);
        this.price = value;
    }

    public static Price from(long value) {
        return new Price(BigDecimal.valueOf(value));
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isGreaterThan(Price value) {
        return price.compareTo(value.getPrice()) > 0;
    }

    public void add(Price value) {
        price = price.add(value.getPrice());
    }

    public void multiply(long value) {
        BigDecimal multiply = price.multiply(BigDecimal.valueOf(value));
        validatePrice(multiply);
        price = multiply;
    }

    private void validatePrice(BigDecimal value) {
        if (Objects.isNull(value) || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
    }
}
