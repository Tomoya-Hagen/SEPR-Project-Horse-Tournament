package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO record for the details of horse
 *
 * @param id id of the horse
 * @param name name of the horse
 * @param sex sex of the horse
 * @param dateOfBirth date of birth of the horse
 * @param height height of the horse
 * @param weight weight of the horse
 * @param breed breed of the horse
 */
public record HorseDetailDto(
    Long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    float height,
    float weight,
    BreedDto breed
) {

  /**
   * Creates a new instance of HorseDetailDto with a new ID.
   *
   * @param newId new id
   * @return new instance of HorseDetailDto
   */
  public HorseDetailDto withId(long newId) {
    return new HorseDetailDto(
        newId,
        name,
        sex,
        dateOfBirth,
        height,
        weight,
        breed);
  }
}
