package ru.practikum.explore.compilations.service;

import org.springframework.data.domain.PageRequest;
import ru.practikum.explore.compilations.dto.CompilationDto;
import ru.practikum.explore.compilations.dto.NewCompilationDto;
import ru.practikum.explore.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(boolean pinned, PageRequest of);

    CompilationDto getbyId(Integer compId);

    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(Integer compId);

    CompilationDto patchCompilation(Integer compId, UpdateCompilationRequest dto);
}
