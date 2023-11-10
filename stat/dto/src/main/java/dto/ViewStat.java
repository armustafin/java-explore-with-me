package dto;


public class ViewStat {
    private String app;
    private String uri;
    private long hits;

    public ViewStat(String app, String uri, int hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    public long getHits() {
        return hits;
    }

    public String getApp() {
        return app;
    }

    public String getUri() {
        return uri;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
