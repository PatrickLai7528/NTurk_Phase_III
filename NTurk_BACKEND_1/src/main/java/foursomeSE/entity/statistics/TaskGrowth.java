package foursomeSE.entity.statistics;

import java.time.LocalDate;

public class TaskGrowth {
    private LocalDate date;
    private int general, frame, segment;

    public TaskGrowth() {
    }

    public TaskGrowth(LocalDate date, int general, int frame, int segment) {
        this.date = date;
        this.general = general;
        this.frame = frame;
        this.segment = segment;
    }

    @Override
    public String
    toString() {
        return "TaskGrowth{" +
                "date=" + date +
                ", general=" + general +
                ", frame=" + frame +
                ", segment=" + segment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskGrowth)) return false;

        TaskGrowth that = (TaskGrowth) o;

        if (general != that.general) return false;
        if (frame != that.frame) return false;
        if (segment != that.segment) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + general;
        result = 31 * result + frame;
        result = 31 * result + segment;
        return result;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getGeneral() {
        return general;
    }

    public void setGeneral(int general) {
        this.general = general;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getSegment() {
        return segment;
    }

    public void setSegment(int segment) {
        this.segment = segment;
    }
}
