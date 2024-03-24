package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO to bundle the query parameters used in searching tournaments.
 * */
public record TournamentSearchDto(
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate,
    Integer limit
) {
}
