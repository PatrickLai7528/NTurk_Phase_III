package foursomeSE.entity.message;

import java.time.LocalDateTime;

public class Message {
    private long id;
    private String username;
    private String title;
    private String content;
    private LocalDateTime createTime;

    public Message() {
    }

    public static Message createMessage(String username, MessageType messageType) {
        return createMessage(username, messageType, new String[]{});
    }

    public static Message createMessage(String username, MessageType messageType, String[] others) {
        Message message = new Message();
        message.setUsername(username);
        message.setCreateTime(LocalDateTime.now());

        switch (messageType) {
            case CREAT_WORKER:
            case CREAT_REQUESTER:
                message.setTitle("欢迎您，新用户");
                message.setContent("创建于：" + message.getCreateTime().toLocalDate().toString());
                break;
            case MAKE_CONTRACT:
                message.setTitle("进入新任务");
                message.setContent("于" + message.getCreateTime().toLocalDate().toString() + "参与”" + others[0] + "”任务");
                break;
            case FULFIL_CONTRACT:
                message.setTitle("完成任务");
                message.setContent("于" + message.getCreateTime().toLocalDate().toString() + "完成”" + others[0] + "”任务");
                break;
            case ABORT_CONTRACT:
                message.setTitle("任务停止");
                message.setContent("任务“" + others[0] + "”于" + message.getCreateTime().toLocalDate().toString() + "因" + others[1] + "停止");
                break;
            case GET_REWARD:
                message.setTitle("发放奖励");
                message.setContent("由于您在“" + others[0] + "”任务中表现出色，获得奖励点" + others[1]);
                break;
            case WORKER_EXCHANGE:
                message.setTitle("兑换奖励");
                message.setContent("您用" + others[0] + "的奖励点兑换现金" + others[1] + "元，继续加油吧！");
                break;
            case REQUESTER_EXCHANGE:
                message.setTitle("冲值");
                message.setContent("您冲值" + others[0] + "元，获得奖励点" + others[1]);
                break;
            case ISSUE_TASK:
                message.setTitle("发布任务");
                message.setContent("任务“" + others[0] + "”于" + message.getCreateTime().toLocalDate().toString() + "发布, 扣除" + others[1] + "奖励点");
                break;
            case FINISH_TASK:
                message.setTitle("结束任务");
                message.setContent("任务“" + others[0] + "“已结束" + others[1]);
                break;
            case REIMBURSE:
                message.setTitle("退款");
                message.setContent("任务“" + others[0] + "”完成人数未达到预期人数，现退款" + others[1] + "元"); // 5
                break;
        }

        return message;
    }

    /**
     * trivial
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
