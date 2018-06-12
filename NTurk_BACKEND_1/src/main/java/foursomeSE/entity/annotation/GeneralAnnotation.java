package foursomeSE.entity.annotation;

import foursomeSE.entity.AnswerPair;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "general_annotations")
public class GeneralAnnotation extends Annotation {
//    @Basic
//    @Column(length = 100000)
//    private ArrayList<AnswerPair> answerPairs;

    @NotNull
    private String answer;


    public GeneralAnnotation() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
