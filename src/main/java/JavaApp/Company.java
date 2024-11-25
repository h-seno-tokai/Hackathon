package JavaApp;

public class Company {
    private String name;
    private String url;
    private String supplement;
    private String picturePath;

    // Constructor
    public Company(String name, String url, String supplement, String picturePath) {
        this.name = name;
        this.url = url;
        this.supplement = supplement;
        this.picturePath = picturePath;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSupplement() {
        return supplement;
    }

    public String getUrl() {
        return url;
    }

    public String getPicturePath() {
        return picturePath;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
