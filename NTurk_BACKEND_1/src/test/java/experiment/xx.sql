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



