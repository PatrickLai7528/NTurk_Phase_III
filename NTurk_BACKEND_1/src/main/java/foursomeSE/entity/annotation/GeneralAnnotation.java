package foursomeSE.entity.annotation;

import foursomeSE.entity.AnswerPair;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "general_annotations")
public class GeneralAnnotation extends Annotation {
    @Basic
    @Column(length = 100000)
    private ArrayList<AnswerPair> answerPairs;


    public GeneralAnnotation() {
    }

    public ArrayList<AnswerPair> getAnswerPairs() {
        return answerPairs;
    }

    public void setAnswerPairs(ArrayList<AnswerPair> answerPairs) {
        this.answerPairs = answerPairs;
    }
}
