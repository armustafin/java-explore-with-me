package ru.practikum.explore.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.compilations.dto.CompilationDto;
import ru.practikum.explore.compilations.dto.NewCompilationDto;
import ru.practikum.explore.compilations.dto.UpdateCompilationRequest;
import ru.practikum.explore.compilations.service.CompilationService;


import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilation(@Valid @RequestBody UpdateCompilationRequest dto,
                                           @PathVariable Integer compId) {
        return compilationService.patchCompilation(compId, dto);
    }
}
