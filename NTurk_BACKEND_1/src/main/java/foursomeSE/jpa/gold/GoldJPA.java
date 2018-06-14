package foursomeSE.jpa.gold;

import foursomeSE.entity.Gold;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;

@Transactional
public interface GoldJPA extends CrudRepository<Gold, Long> {

    @Query(value = "SELECT img_name FROM microtasks\n" +
            "WHERE microtask_id IN (\n" +
            "    SELECT microtask_id FROM annotation\n" +
            "    WHERE annotation_id IN (\n" +
            "        SELECT annotation_id FROM gold\n" +
            "        WHERE taskId = ?1 AND ord = ?2\n" +
            "    )\n" +
            ")",
            nativeQuery = true)
    String findByTaskIdAndOrd(long taskId, int ord);

    @Query(value = "SELECT annotation_id\n" +
            "FROM gold\n" +
            "WHERE annotation_id\n" +
            "      IN (SELECT annotation_id\n" +
            "          FROM annotation\n" +
            "          WHERE microtask_id IN (SELECT microtask_id\n" +
            "                                  FROM microtasks\n" +
            "                                  WHERE img_name = ?1))",
            nativeQuery = true)
    BigInteger goldAidByImgName(String imgName);
}

/*

select img_name from microtasks
where microtask_id in (
    select microtask_id from annotation
    where annotation_id in (
        select annotation_id from gold
        where taskId = ?1 and ord = ?2
    )
)


SELECT annotation_id
FROM gold
WHERE annotation_id
      IN (SELECT annotation_id
          FROM annotation
          WHERE microtask_id IN (SELECT microtask_id
                                  FROM microtasks
                                  WHERE img_name = ?1))

 */