package kitchenpos.domain;

import javax.persistence.*;

@Entity
public class OrderTable {
    private static final String NUMBER_OF_GUESTS_IS_NOT_NEGATIVE = "방문한 손님 수는 마이너스가 될 수 없습니다";
    private static final String EMPTY_TABLE_CANNOT_CHANGE = "빈 테이블은 변경할 수 없습니다";
    private static final String NOT_EMPTY_OR_GROUPED_TABLE_CANNOT_GROUP = "빈 테이블이 아니거나 이미 단체 지정되었다면 단체 지정 할 수 없습니다";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private TableGroup tableGroup;
    private int numberOfGuests;
    private boolean empty;

    protected OrderTable() {
    }

    public OrderTable(int numberOfGuests, boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public void changeEmpty(boolean empty) {
        if (tableGroup != null) {
            throw new IllegalArgumentException();
        }
        this.empty = empty;
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests < 0) {
            throw new IllegalArgumentException(NUMBER_OF_GUESTS_IS_NOT_NEGATIVE);
        }
        if (isEmpty()) {
            throw new IllegalArgumentException(EMPTY_TABLE_CANNOT_CHANGE);
        }
        this.numberOfGuests = numberOfGuests;
    }

    public void groupBy(TableGroup tableGroup) {
        if (!isEmpty() || isGrouped() || tableGroup == null) {
            throw new IllegalArgumentException(NOT_EMPTY_OR_GROUPED_TABLE_CANNOT_GROUP);
        }
        this.tableGroup = tableGroup;
        tableGroup.getOrderTables().getOrderTables().add(this);
        empty = false;
    }

    private boolean isGrouped() {
        return tableGroup != null;
    }

    public void ungroup() {
        tableGroup = null;
    }

    public Long getId() {
        return id;
    }

    public TableGroup getTableGroup() {
        return tableGroup;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }
}
