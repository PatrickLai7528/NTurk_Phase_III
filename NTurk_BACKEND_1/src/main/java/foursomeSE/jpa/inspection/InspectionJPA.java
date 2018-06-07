package foursomeSE.jpa.inspection;

import foursomeSE.entity.communicate.CInspection;
import foursomeSE.entity.inspection.Inspection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface InspectionJPA extends CrudRepository<Inspection, Long> {
    @Query(value = "select annotation_id, (\n" +
            "  select ifnull(avg(rate), 1.4) from inspections isp\n" +
            "  where isp.annotation_id = a.annotation_id\n" +
            ") as rate\n" +
            "from (select * from annotation where img_name = ?1) a\n" +
            "order by rate DESC;",
            nativeQuery = true)
    List<Object[]> getBestForImgname(String imgName);

    // 同理，上面的也不需要了...

    @Query(value = "select * from inspections\n" +
            "where username = ?2 and annotation_id in (\n" +
            "    select annotation_id from annotations\n" +
            "    where microtask_id in (\n" +
            "        select microtask_id from microtasks\n" +
            "        where task_id = ?1\n" +
            "    )\n" +
            ")", nativeQuery = true)
    long countByTaskIdAndUsername(long taskId, String username);


    long countByAnnotationIdAndUsername(long annotationId, String username);

    List<Inspection> findByAnnotationId(long annotationId);

}

/*
foursomeSE.entity.communicate.

select new foursomeSE.entity.communicate.CInspection(x.annotation_id, x.rate) from (

) as x


select annotation_id, (
  select ifnull(avg(rate), 1.4) from inspections isp
  where isp.annotation_id = a.annotation_id
) as rate
from (select * from annotation where img_name = ?1) a
order by rate DESC;


select * from inspections
where username = ?2 and annotation_id in (
    select annotation_id from annotations
    where microtask_id in (
        select microtask_id from microtasks
        where task_id = ?1
    )
)
*/
