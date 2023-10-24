package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationResponseDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationResponseDto> getCompilations(
            @RequestParam(name = "pinned", defaultValue = "false") boolean isPinned,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size
    ) {
        log.debug("+ getCompilations");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationResponseDto> compilations = compilationService.getCompilations(isPinned, pageable);
        log.debug("- getCompilations: {}", compilations);
        return compilations;
    }

    @GetMapping("/{compilationId}")
    public CompilationResponseDto getCompilationById(@PathVariable long compilationId) {
        log.debug("+ getCompilationById: compilationId={}", compilationId);
        CompilationResponseDto compilation = compilationService.getCompilationById(compilationId);
        log.debug("- getCompilationById: {}", compilation);
        return compilation;
    }
}
