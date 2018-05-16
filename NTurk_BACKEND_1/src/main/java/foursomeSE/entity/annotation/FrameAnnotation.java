package foursomeSE.entity.annotation;

import foursomeSE.entity.Frame;

import java.util.List;

public class FrameAnnotation extends Annotation {
    private String color;
    private List<Frame> frames;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }


}
