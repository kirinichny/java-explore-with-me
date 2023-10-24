package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationCreateOrUpdateDto;
import ru.practicum.dto.compilation.CompilationResponseDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationResponseDto getCompilationById(long compilationId) {
        return compilationMapper.toCompilationResponseDto(getCompilationByIdOrThrow(compilationId));
    }

    @Override
    public List<CompilationResponseDto> getCompilations(boolean isPinned, Pageable pageable) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(isPinned, pageable);

        return compilationMapper.toCompilationResponseDto(compilations);
    }

    @Override
    public CompilationResponseDto createCompilation(CompilationCreateOrUpdateDto compilation) {
        Compilation compilationToSave = compilationMapper.toCompilation(compilation);
        assignEventsToCompilation(compilation.getEvents(), compilationToSave);

        return compilationMapper.toCompilationResponseDto(compilationRepository.save(compilationToSave));
    }

    @Override
    public CompilationResponseDto updateCompilation(long compilationId, CompilationCreateOrUpdateDto compilation) {
        Compilation currentCompilation = getCompilationByIdOrThrow(compilationId);

        String title = compilation.getTitle();
        Boolean isPinned = compilation.getPinned();
        List<Long> eventIds = compilation.getEvents();

        assignEventsToCompilation(eventIds, currentCompilation);

        if (Objects.nonNull(title) && !title.isBlank()) {
            currentCompilation.setTitle(title);
        }

        if (Objects.nonNull(isPinned)) {
            currentCompilation.setPinned(isPinned);
        }

        return compilationMapper.toCompilationResponseDto(compilationRepository.save(currentCompilation));
    }

    @Override
    public void deleteCompilation(long compilationId) {
        compilationRepository.delete(getCompilationByIdOrThrow(compilationId));
    }

    private Compilation getCompilationByIdOrThrow(long compilationId) throws NotFoundException {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Подборка событий #%d не найдена.", compilationId)));
    }

    private void assignEventsToCompilation(List<Long> eventIds, Compilation compilation) {
        if (Objects.nonNull(eventIds)) {
            List<Event> events = new ArrayList<>();

            if (!eventIds.isEmpty()) {
                boolean isEventsExist = eventRepository.existsAllByIdIn(eventIds);

                if (!isEventsExist) {
                    throw new NotFoundException("Одно или несколько событий не найдено.");
                }

                events = eventIds.stream()
                        .map(eventId -> Event.builder().id(eventId).build())
                        .collect(Collectors.toList());
            }

            compilation.setEvents(events);
        }
    }
}
