package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO record for a horse in the list of horses in search view.
 *
 * @param id id of the horse
 * @param name name of the horse
 * @param sex sex of the horse
 * @param dateOfBirth date of birth of the horse
 * @param breed breed of the horse
 */
public record HorseListDto(
    Long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    BreedDto breed
) {
}
