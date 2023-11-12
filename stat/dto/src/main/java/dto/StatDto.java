package dto;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class StatDto {
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timeStamp;
    private String uri;
    private String ip;
    private String app;
}
