package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO to bundle the query parameters used in searching tournaments.
 *
 * @param name the name of the tournament to search for
 * @param startDate the start date of the tournament to search for
 * @param endDate the end date of the tournament to search for
 * */
public record TournamentSearchParamsDto(
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
}
