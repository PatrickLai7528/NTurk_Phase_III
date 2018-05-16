package foursomeSE.entity.annotation;

import foursomeSE.entity.AnswerPair;

import java.util.List;

public class GeneralAnnotation extends Annotation {
    private List<AnswerPair> answerPairs;

    public List<AnswerPair> getAnswerPairs() {
        return answerPairs;
    }

    public void setAnswerPairs(List<AnswerPair> answerPairs) {
        this.answerPairs = answerPairs;
    }

}
