package kitchenpos.table.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TableGroupRepository extends JpaRepository<TableGroup, Long> {

    @Query(value = " SELECT EXISTS( "
                            + " SELECT * FROM order_table "
                            + " WHERE table_group_id in :orderTableIds"
                            + " )"
            , nativeQuery = true)
    boolean existsByOrderTables(List<Long> orderTableIds);

}
