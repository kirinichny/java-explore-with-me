package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationCreateOrUpdateDto;
import ru.practicum.dto.compilation.CompilationResponseDto;
import ru.practicum.service.CompilationService;
import ru.practicum.validation.ValidationGroup;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(
            @RequestBody @Validated(ValidationGroup.Create.class) CompilationCreateOrUpdateDto compilation) {
        log.debug("+ createCompilation: {}", compilation);
        CompilationResponseDto createdCompilation = compilationService.createCompilation(compilation);
        log.debug("- createCompilation: {}", createdCompilation);
        return createdCompilation;
    }

    @PatchMapping("/{compilationId}")
    public CompilationResponseDto updateCompilation(@PathVariable long compilationId,
                                                    @RequestBody @Validated(ValidationGroup.Update.class)
                                                    CompilationCreateOrUpdateDto compilation) {
        log.debug("+ updateCompilation: {}", compilation);
        CompilationResponseDto updatedCompilation = compilationService.updateCompilation(compilationId, compilation);
        log.debug("- updateCompilation: {}", updatedCompilation);
        return updatedCompilation;
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compilationId) {
        log.debug("+ deleteCompilation: CompilationId={}", compilationId);
        compilationService.deleteCompilation(compilationId);
        log.debug("- deleteCompilation");
    }
}