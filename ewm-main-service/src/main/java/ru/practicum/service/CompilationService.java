package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationCreateOrUpdateDto;
import ru.practicum.dto.compilation.CompilationResponseDto;

import java.util.List;

public interface CompilationService {
    CompilationResponseDto getCompilationById(long compilationId);

    List<CompilationResponseDto> getCompilations(boolean isPinned, Pageable pageable);

    CompilationResponseDto createCompilation(CompilationCreateOrUpdateDto compilation);

    CompilationResponseDto updateCompilation(long compilationId, CompilationCreateOrUpdateDto compilation);

    void deleteCompilation(long compilationId);
}
