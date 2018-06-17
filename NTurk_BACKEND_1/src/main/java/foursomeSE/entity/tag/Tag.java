package foursomeSE.entity.tag;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

// 感觉这个可能不需要了

//@Entity
//@Table(name = "tags")

public class Tag {
//    @Id
    public long id;

//    @NotNull
    public String name;
}
