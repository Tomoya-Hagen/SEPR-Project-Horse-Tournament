package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO record to bundle query parameters for creating a new tournament.
 */
public record TournamentCreateDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    List<HorseSelectionDto> participants
) {
}
