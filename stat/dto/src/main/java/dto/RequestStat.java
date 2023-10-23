package dto;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@Setter
public class RequestStat {
    private String app;
    private String uri;
    private int hit;
}
