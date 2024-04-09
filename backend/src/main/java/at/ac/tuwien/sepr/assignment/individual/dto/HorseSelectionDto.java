package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO record for selected properties of a horse.
 */
public record HorseSelectionDto(
    long id,
    String name,
    LocalDate dateOfBirth
) {
}
