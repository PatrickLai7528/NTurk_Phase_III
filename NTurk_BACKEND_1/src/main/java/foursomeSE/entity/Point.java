package foursomeSE.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Point implements Serializable {
    public double x, y;
}
