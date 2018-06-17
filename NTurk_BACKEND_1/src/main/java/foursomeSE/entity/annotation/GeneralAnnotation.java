package foursomeSE.entity.annotation;

import foursomeSE.entity.AnswerPair;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
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

    // 那两个core应该就不用管了，反正也是什么也没干。


    @Override
    public void setCore(Object core) {
        if (core != null && !(core instanceof String)) {
            throw new InvalidParameterException();
        }
        answer = (String) core;
    }
}
