package foursomeSE.jpa.gold;

import foursomeSE.entity.Gold;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface GoldJPA extends CrudRepository<Gold, Long> {

    @Query(value = "select img_name from microtasks\n" +
            "where microtask_id in (\n" +
            "    select microtask_id from annotation\n" +
            "    where annotation_id in (\n" +
            "        select annotation_id in gold\n" +
            "        where taskId = ?1 and ord = ?2\n" +
            "    )\n" +
            ")",
            nativeQuery = true)
    String findByTaskIdAndOrd(long taskId, int ord);
}

/*

select img_name from microtasks
where microtask_id in (
    select microtask_id from annotation
    where annotation_id in (
        select annotation_id in gold
        where taskId = ?1 and ord = ?2
    )
)

 */