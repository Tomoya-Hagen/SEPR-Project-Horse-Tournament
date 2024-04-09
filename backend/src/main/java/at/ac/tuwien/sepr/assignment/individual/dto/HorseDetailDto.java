package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO record for the details of horse
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
