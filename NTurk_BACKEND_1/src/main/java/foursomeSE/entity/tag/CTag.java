package foursomeSE.entity.tag;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

// 感觉这个可能不需要了

public class CTag {
    public String value;

    public CTag(String value) {
        this.value = value;
    }
}
