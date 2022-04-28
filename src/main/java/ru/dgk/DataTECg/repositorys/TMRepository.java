package ru.dgk.DataTECg.repositorys;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.dgk.DataTECg.entity.TMEntity;

import java.util.Collection;

public interface TMRepository extends CrudRepository<TMEntity, Long> {

    @Query(
            value = "SELECT * FROM RSDU2ELARH.EL010_6303821 WHERE time1970 > TO_DT1970(TO_DATE ('2022-03-23 0:00:00', 'YYYY-MM-DD HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE ('2022-03-23 23:00:00', 'YYYY-MM-DD HH24:MI:SS'))",
            nativeQuery = true)
    Collection<TMEntity> getSQL();


}
