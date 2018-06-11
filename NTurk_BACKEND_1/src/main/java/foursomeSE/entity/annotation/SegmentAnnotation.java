package foursomeSE.entity.annotation;

import foursomeSE.entity.Segment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "segment_annotations")
public class SegmentAnnotation extends Annotation {
    //    @Basic
//    @Column(length = 100000)

    @Transient
    private ArrayList<Segment> segments;

    @NotNull
    private Segment segment;


    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }
}
