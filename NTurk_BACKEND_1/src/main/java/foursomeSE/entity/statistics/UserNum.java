package foursomeSE.entity.statistics;

import foursomeSE.entity.user.UserType;

public class UserNum {
    private UserType name;
    private int value;

    public UserNum() {
    }

    public UserNum(UserType name, int value) {
        this.name = name;
        this.value = value;
    }

    public UserType getName() {
        return name;
    }

    public void setName(UserType name) {
        this.name = name;
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
        if (!(o instanceof UserNum)) return false;

        UserNum userNum = (UserNum) o;

        if (value != userNum.value) return false;
        return name == userNum.name;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "UserNum{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
