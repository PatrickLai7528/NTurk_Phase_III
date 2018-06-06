package foursomeSE.jpa.annotation;

import foursomeSE.entity.annotation.Annotation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface AnnotationJPA extends CrudRepository<Annotation, Long> {
    @Query(value = "select * from annotation\n" +
            "where username = ?2 and microtask_id in (\n" +
            "    select microtask_id from microtasks\n" +
            "    where task_id = ?1\n" +
            ")", nativeQuery = true)
    long countByTaskIdAndUsername(long taskId, String username);

    @Query(value = "select id\n" +
            "from (\n" +
            "  select annotation.annotation_id as id, parallel, (\n" +
            "    select count(*) from inspections\n" +
            "    where annotation.annotation_id = inspections.annotation_id\n" +
            "  ) as have_done\n" +
            "  from annotation\n" +
            "  where microtask_id in (\n" +
            "    select * from microtasks\n" +
            "    where microtask_status = 2 -- unreviewed\n" +
            "      and microtask_id in (\n" +
            "        select microtask_id from microtasks\n" +
            "        where task_id = ?1\n" +
            "      )\n" +
            "  ) and annotation_status = 0 -- reviewable\n" +
            "    and username <> username\n" +
            ")\n" +
            "where parallel < 2 - have_done\n" +
            "order by have_done desc", nativeQuery = true)
    List<Long> getIdsByTaskId(long taskId, String username);
}

/*
select * from annotation
where username = ?2 and microtask_id in (
    select microtask_id from microtasks
    where task_id = ?1
)


select id
from (
  select annotation.annotation_id as id, parallel, (
    select count(*) from inspections
    where annotation.annotation_id = inspections.annotation_id
  ) as have_done
  from annotation
  where microtask_id in (
    select * from microtasks
    where microtask_status = 2 -- unreviewed
      and microtask_id in (
        select microtask_id from microtasks
        where task_id = ?1
      )
  ) and annotation_status = 0 -- reviewable
    and username <> username
)
where parallel < 2 - have_done
order by have_done desc



 */