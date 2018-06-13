package foursomeSE.entity.task;

import javax.persistence.*;
import java.io.Serializable;

//@Entity
//@Table(name = "tags")
public class Tag implements Serializable {
//    @Id
//    @GeneratedValue
    public long id;

//    @Column(unique = true)
    public String name;

    public Tag(String name) {
        this.name = name;
    }

    public Tag() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
