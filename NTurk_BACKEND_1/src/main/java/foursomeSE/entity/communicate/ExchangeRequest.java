package foursomeSE.entity.communicate;

public class ExchangeRequest {
    private double point;
    private double money;

    public ExchangeRequest() {
    }

    public ExchangeRequest(double point, double money) {
        this.point = point;
        this.money = money;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
