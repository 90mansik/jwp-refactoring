package kitchenpos.domain.common;

import kitchenpos.common.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Price {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    protected Price() {

    }

    private Price(BigDecimal price) {
        this.price = price;
    }

    public static Price from(BigDecimal price) {
        isValidPriceIsNull(price);
        isValidPriceIsNegative(price);

        return new Price(price);
    }

    private static void isValidPriceIsNegative(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorCode.PRICE_NOT_NEGATIVE.getMessage());
        }
    }

    private static void isValidPriceIsNull(BigDecimal price) {
        if (Objects.isNull(price)) {
            throw new IllegalArgumentException(ErrorCode.PRICE_NOT_NULL.getMessage());
        }
    }

    public int compareTo(BigDecimal price) {
        return this.price.compareTo(price);
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price menuPrice = (Price) o;
        return Objects.equals(price, menuPrice.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
