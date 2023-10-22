package dto;


import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class RequestStat {
    private String app;
    private String uri;
    private int hit;
}
