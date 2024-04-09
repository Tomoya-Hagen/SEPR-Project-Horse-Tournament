package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO record for selected properties of a horse.
 *
 * @param id id of the horse
 * @param name name of the horse
 * @param dateOfBirth date of birth of the horse
 */
public record HorseSelectionDto(
    long id,
    String name,
    LocalDate dateOfBirth
) {
}
