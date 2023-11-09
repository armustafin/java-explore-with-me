package dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ViewStat {
    private String app;
    private String uri;
    private long hits;

    public ViewStat(String app, String uri, int hits) {
        this.hits = hits;
        this.app = app;
        this.uri = uri;
    }
}
