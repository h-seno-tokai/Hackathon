package JavaApp;

public class Company {
    private String name;
    private String url;

    // Constructor
    public Company(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}