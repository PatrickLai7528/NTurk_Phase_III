package foursomeSE.entity.annotation;

import foursomeSE.entity.Segment;

import java.util.List;

public class SegmentAnnotation extends Annotation {
    private String color;
    private List<Segment> segments;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }
}
