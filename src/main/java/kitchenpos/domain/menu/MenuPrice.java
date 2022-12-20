package kitchenpos.domain.menu;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class MenuPrice {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    protected MenuPrice(){

    }

    private MenuPrice(BigDecimal price){
        this.price = price;
    }

    public static MenuPrice from(BigDecimal price){
        isValidPriceIsNull(price);
        isValidPriceIsNegative(price);

        return new MenuPrice(price);
    }

    private static void isValidPriceIsNegative(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
    }

    private static void isValidPriceIsNull(BigDecimal price){
        if (Objects.isNull(price)) {
            throw new IllegalArgumentException();
        }
    }
    public int compareTo(BigDecimal price){
        return this.price.compareTo(price);
    }

    public BigDecimal getPrice(){
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuPrice menuPrice = (MenuPrice) o;
        return Objects.equals(price, menuPrice.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
