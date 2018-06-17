package foursomeSE.entity.statistics;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PHItem {
    public LocalDate date;
    public int ongoing = 0, underReview = 0, finished = 0;



    @Override
    public String toString() {
        return "PHItem{" +
                "date=" + date +
                ", ongoing=" + ongoing +
                ", underReview=" + underReview +
                ", finished=" + finished +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PHItem)) return false;

        PHItem phItem = (PHItem) o;

        if (ongoing != phItem.ongoing) return false;
        if (underReview != phItem.underReview) return false;
        if (finished != phItem.finished) return false;
        return date != null ? date.equals(phItem.date) : phItem.date == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + ongoing;
        result = 31 * result + underReview;
        result = 31 * result + finished;
        return result;
    }
}
