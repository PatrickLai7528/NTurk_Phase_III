package foursomeSE.entity.statistics;

public class UserActivity {
    private int name;
    private int value;

    public UserActivity() {
    }

    public UserActivity(int name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserActivity)) return false;

        UserActivity that = (UserActivity) o;

        if (name != that.name) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = name;
        result = 31 * result + value;
        return result;
    }
}
