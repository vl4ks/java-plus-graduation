package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoForAdmin {
    private Long id;
    private String email;
    private String name;
    private Set<Long> forbiddenCommentEvents;
}
