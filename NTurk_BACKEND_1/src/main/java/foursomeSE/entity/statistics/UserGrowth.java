package foursomeSE.entity.statistics;

import java.time.LocalDate;

public class UserGrowth {
    private LocalDate date;
    private int value;

    public UserGrowth() {
    }

    public UserGrowth(LocalDate date, int value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGrowth)) return false;

        UserGrowth that = (UserGrowth) o;

        if (value != that.value) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "UserGrowth{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }
}
