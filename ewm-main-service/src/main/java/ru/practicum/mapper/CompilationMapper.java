package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.compilation.CompilationCreateOrUpdateDto;
import ru.practicum.dto.compilation.CompilationResponseDto;
import ru.practicum.model.compilation.Compilation;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EventMapper.class})
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(CompilationCreateOrUpdateDto compilation);

    @Mapping(source = "events", target = "events")
    CompilationResponseDto toCompilationResponseDto(Compilation compilation);

    List<CompilationResponseDto> toCompilationResponseDto(List<Compilation> compilations);
}
