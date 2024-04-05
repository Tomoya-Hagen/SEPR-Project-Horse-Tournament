package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

/**
 * DAO for breeds.
 * Implements access functionality to the application's persistent data store regarding breeds.
 */
public interface BreedDao {

  /**
   * Find the breeds with the given ids.
   *
   * @param breedIds the ids of the breeds to find
   * @return A collection of the breeds with the given ids
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Search for breeds with the given search parameters.
   *
   * @param searchParams  the search parameters
   * @return A collection of the breeds with the given search parameters
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
