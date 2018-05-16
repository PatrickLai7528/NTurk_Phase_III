package foursomeSE.entity.user;

import java.time.LocalDateTime;

public abstract class MyUser {
    private LocalDateTime createTime;
    private String emailAddress;
    private String nickname;
    private String password;
    private String iconName; // 作头像的图片名称
    private double credit; // 积分。
    private double experiencePoint; // 累积的积分。（如果是requester，那么是累积的充钱）
    private String province;

    public MyUser() {
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getExperiencePoint() {
        return experiencePoint;
    }

    public void setExperiencePoint(double experiencePoint) {
        this.experiencePoint = experiencePoint;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
