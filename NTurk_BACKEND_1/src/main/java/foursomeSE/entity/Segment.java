package foursomeSE.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.List;

@Embeddable
public class Segment implements Serializable {
    private List<Point> polygon;
    private String tag;

    public Segment() {
    }

    public List<Point> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<Point> polygon) {
        this.polygon = polygon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
