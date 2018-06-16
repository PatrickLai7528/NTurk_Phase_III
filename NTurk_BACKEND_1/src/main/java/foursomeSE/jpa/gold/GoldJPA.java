package foursomeSE.jpa.gold;

import foursomeSE.entity.Gold;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;

@Transactional
public interface GoldJPA extends CrudRepository<Gold, Long> {

    /**
     * 这个是返回给工人img时用的
     * */
    @Query(value = "SELECT img_name\n" +
            "FROM microtasks\n" +
            "WHERE microtask_id IN (\n" +
            "  SELECT microtask_id\n" +
            "  FROM annotation\n" +
            "  WHERE annotation_id IN (\n" +
            "    SELECT annotation_id\n" +
            "    FROM gold\n" +
            "    WHERE task_id = ?1 AND ord = ?2 AND verification_type = ?3\n" +
            "  )\n" +
            ")",
            nativeQuery = true)
    String findByTaskIdAndOrdAndVerificationType(long taskId, int ord, int vTypeOrd);


    /**
     * 这个是当工人通过imgName找gold时用的
     * */
    @Query(value = "SELECT annotation_id\n" +
            "FROM gold\n" +
            "WHERE annotation_id\n" +
            "      IN (SELECT annotation_id\n" +
            "          FROM annotation\n" +
            "          WHERE microtask_id IN (SELECT microtask_id\n" +
            "                                 FROM microtasks\n" +
            "                                 WHERE img_name = ?1))\n" +
            "AND verification_type = ?2",
            nativeQuery = true)
    BigInteger goldAidByImgName(String imgName, int vTypeOrd);
}

/*

SELECT img_name
FROM microtasks
WHERE microtask_id IN (
  SELECT microtask_id
  FROM annotation
  WHERE annotation_id IN (
    SELECT annotation_id
    FROM gold
    WHERE task_id = ?1 AND ord = ?2 AND verification_type = ?3
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
AND verification_type = ?2


 */