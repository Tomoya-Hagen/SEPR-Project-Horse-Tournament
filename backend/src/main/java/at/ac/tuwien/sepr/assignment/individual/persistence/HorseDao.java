package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface for DAO for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get the horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the horses where all given parameters match.
   */
  Collection<Horse> search(HorseSearchDto searchParameters);


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   */
  Horse update(HorseDetailDto horse) throws ConflictException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if no horse with the given ID is found
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Create a horse with the data given.
   *
   * @param horse the horse to create
   * @return the created horse
   */
  Horse create(HorseDetailDto horse);

  /**
   * Delete the horse with the given ID.
   *
   * @param id the ID of the horse to delete
   */
  void delete(long id) throws ConflictException;

  /**
   * Get the horses with the given IDs from the persistent data store.
   *
   * @param ids the IDs of the horses to get
   * @return the horses
   */
  List<Horse> getByIds(Set<Long> ids) throws NotFoundException;

}
