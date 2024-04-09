package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO class for list of tournaments in search view.
 *
 * @param id the ID of the tournament
 * @param name the name of the tournament
 * @param startDate the start date of the tournament
 * @param endDate the end date of the tournament
 */
public record TournamentListDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}

