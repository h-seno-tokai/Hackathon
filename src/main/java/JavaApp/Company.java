package JavaApp;

public class Company {
    private String name;
    private String url;
    private String supplement;

    // Constructor
    public Company(String name, String url, String supplement) {
        this.name = name;
        this.url = url;
        this.supplement = supplement;
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
}