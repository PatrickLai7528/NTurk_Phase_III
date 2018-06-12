package foursomeSE.entity;

import foursomeSE.entity.annotation.Annotation;
import foursomeSE.entity.annotation.GeneralAnnotation;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Segment implements Serializable {
    private List<Point> polygon;

    private String tag;

    public Segment() {
    }

    public List<Point> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<Point> polygon) {
        this.polygon = polygon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static void main(String[] args) {
        ArrayList<Object> b = new ArrayList<>();
        ArrayList<Object> a = new ArrayList<>(Arrays.asList("1,2,3".split(",")));
//        ArrayList<String> b = a.stream().map(x -> (String) x).collect(Collectors.toCollection(ArrayList::new));
//        System.out.println(b);

        if (a.isEmpty() || a.get(0) instanceof String) {
            ArrayList<String> c = a.stream().map(x -> (String)x).collect(Collectors.toCollection(ArrayList::new));
            System.out.println(c);
        }

        if (b.isEmpty() || b.get(0) instanceof String) {
            ArrayList<String> c = b.stream().map(x -> (String)x).collect(Collectors.toCollection(ArrayList::new));
            System.out.println(c);
        }
    }
}
