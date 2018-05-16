package foursomeSE.entity;

import java.util.List;

public class Segment {
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
