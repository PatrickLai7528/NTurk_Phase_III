package foursomeSE.jpa.inspection;

import foursomeSE.entity.communicate.CInspection;
import foursomeSE.entity.inspection.Inspection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface InspectionJPA extends CrudRepository<Inspection, Long> {
    @Query(value =
            "select annotation_id, (\n" +
            "  select ifnull(avg(rate), 1.4) from inspections isp\n" +
            "  where isp.annotation_id = a.annotation_id\n" +
            ") as rate\n" +
            "from (select * from annotation where img_name = ?1) a\n" +
            "order by rate DESC;",
            nativeQuery = true)
    List<Object[]> getBestForImgname(String imgName);
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
*/
