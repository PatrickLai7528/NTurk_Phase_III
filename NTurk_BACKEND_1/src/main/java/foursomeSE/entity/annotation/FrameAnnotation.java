package foursomeSE.entity.annotation;

import foursomeSE.entity.Frame;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static foursomeSE.util.ConvenientFunctions.setSameFields;

@Entity
@Table(name = "frame_annotations")
public class FrameAnnotation extends Annotation {
//        @Basic
//    @Column(length = 100000)
    @Transient
    private ArrayList<Frame> frames;

    @NotNull
    @Basic
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

    @Override
    public Object core() {
        return frame;
    }

    @Override
    public void setCore(ArrayList<Object> list) {
        // 但是有可能后面的不是，所以这么测也没什么用
        if (list.isEmpty() || list.get(0) instanceof Frame) {
            frames = list.stream().map(i -> (Frame)i)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            throw new InvalidParameterException("wrong type for frame");
        }

    }
}
