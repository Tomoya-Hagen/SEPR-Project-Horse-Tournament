package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.springframework.stereotype.Component;

/**
 * Mapper class that maps a {@link Breed} to a {@link BreedDto}.
 */
@Component
public class BreedMapper {
  public BreedDto entityToDto(Breed breed) {
    return new BreedDto(breed.getId(), breed.getName());
  }
}
