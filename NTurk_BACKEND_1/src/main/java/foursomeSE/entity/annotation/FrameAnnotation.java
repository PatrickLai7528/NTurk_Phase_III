package foursomeSE.entity.annotation;

import foursomeSE.entity.Frame;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "frame_annotations")
public class FrameAnnotation extends Annotation {
    private String color;

    @Basic
    @Column(length = 100000)
    private ArrayList<Frame> frames;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<Frame> frames) {
        this.frames = frames;
    }
}
