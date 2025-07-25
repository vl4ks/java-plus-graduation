package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Name can't be empty")
    @Size(min = 1, max = 50, message = "Name should be from 1 to 50 symbols")
    private String name;
}
