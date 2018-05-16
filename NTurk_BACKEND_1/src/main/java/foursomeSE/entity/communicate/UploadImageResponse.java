package foursomeSE.entity.communicate;

public class UploadImageResponse {
    private String newImageName;

    public UploadImageResponse() {
    }

    public UploadImageResponse(String newImageName) {
        this.newImageName = newImageName;
    }

    public String getNewImageName() {
        return newImageName;
    }

    public void setNewImageName(String newImageName) {
        this.newImageName = newImageName;
    }
}
