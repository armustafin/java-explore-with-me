package ru.practikum.explore.compilations.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practikum.explore.events.dto.EventShortDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class CompilationDto {

    private int id;
    @NotBlank(message = "Please provide title")
    @Size(min = 1, max = 50)
    private String title;
    private List<EventShortDto> events;
    private boolean pinned;
}
