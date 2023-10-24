package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CompilationCreateOrUpdateDto {
    @NotBlank(groups = {ValidationGroup.Create.class})
    @Size(
            groups = {ValidationGroup.Create.class, ValidationGroup.Update.class},
            min = 1, max = 50
    )
    private String title;
    private List<Long> events;
    private Boolean pinned;
}