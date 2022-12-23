package kitchenpos.domain;

import kitchenpos.common.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class MenuPrice {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    protected MenuPrice() {
    }

    private MenuPrice(BigDecimal price) {
        isValidPriceZero(price);
        this.price = price;
    }

    public static MenuPrice from(BigDecimal price) {
        return new MenuPrice(price);
    }

    public void isValidPriceZero(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorCode.CAN_NOT_LESS_THEN_ZERO_PRICE.getMessage());
        }
    }


    public BigDecimal getPrice() {
        return price;
    }

}
