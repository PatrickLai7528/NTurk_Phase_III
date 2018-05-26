package foursomeSE.entity.annotation;

import foursomeSE.entity.Segment;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "segment_annotations")
public class SegmentAnnotation extends Annotation {
    private String color;

    @Basic
    @Column(length = 100000)
    private ArrayList<Segment> segments;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }
}
