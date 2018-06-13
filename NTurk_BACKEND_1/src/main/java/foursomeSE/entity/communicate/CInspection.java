package foursomeSE.entity.communicate;

import java.io.Serializable;

public class CInspection implements Serializable {
    private long annotationId;
    private double rate;

    public CInspection() {
    }

    public CInspection(long annotationId, double rate) {
        this.annotationId = annotationId;
        this.rate = rate;
    }

    public long getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(long annotationId) {
        this.annotationId = annotationId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "CInspection{" +
                "microtaskId=" + annotationId +
                ", rate=" + rate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CInspection)) return false;

        CInspection that = (CInspection) o;

        if (annotationId != that.annotationId) return false;
        return Double.compare(that.rate, rate) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (annotationId ^ (annotationId >>> 32));
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
