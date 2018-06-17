package foursomeSE.entity.tag;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class TagAndWorker {
    @Id
    @GeneratedValue
    public long id;

    @NotNull
    public String tagName;

    @NotNull
    public String username;
}
