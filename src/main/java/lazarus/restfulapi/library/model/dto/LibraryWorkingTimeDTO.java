package lazarus.restfulapi.library.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
@Schema(name = "Library Working Time")
public class LibraryWorkingTimeDTO {
    @Schema(description = "LibraryWorkingTime identifier", example = "1")
    private Long id;

    @Schema(description = "Day of week", example = "MONDAY")
    private DayOfWeek dayOfWeek;

    @Schema(description = "The time at which the library opens in 'HH:mm:ss' format", example = "08:00:00")
    private Time openingTime;

    @Schema(description = "The time at which the library closes in 'HH:mm:ss' format", example = "20:00:00")
    private Time closingTime;
}