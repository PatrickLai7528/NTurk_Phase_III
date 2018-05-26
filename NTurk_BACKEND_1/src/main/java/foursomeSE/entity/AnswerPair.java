package foursomeSE.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AnswerPair implements Serializable {
    private String question;
    private String answer;

    public AnswerPair() {
    }

    public AnswerPair(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
