package foursomeSE.entity.annotation;

import foursomeSE.entity.Frame;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "frame_annotations")
public class FrameAnnotation extends Annotation {
    //    @Basic
//    @Column(length = 100000)
    @Transient
    private ArrayList<Frame> frames;

    @NotNull
    private Frame frame;


    public FrameAnnotation() {
    }

    public FrameAnnotation(FrameAnnotation frameAnnotation) {
        setSameFields(this, frameAnnotation);
    }


    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<Frame> frames) {
        this.frames = frames;
    }
}
