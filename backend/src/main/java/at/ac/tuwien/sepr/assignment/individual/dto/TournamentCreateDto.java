package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO record to bundle query parameters for creating a new tournament.
 *
 * @param name the name of the tournament to create
 * @param startDate the start date of the tournament to create
 * @param endDate the end date of the tournament to create
 * @param participants the participants of the tournament to create
 */
public record TournamentCreateDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    List<HorseSelectionDto> participants
) {
}
