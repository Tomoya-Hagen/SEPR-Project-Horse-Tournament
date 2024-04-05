package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

public interface BreedDao {

  Collection<Breed> findBreedsById(Set<Long> breedIds);

  Collection<Breed> search(BreedSearchDto searchParams);
}
