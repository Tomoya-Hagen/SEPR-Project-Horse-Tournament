package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Search for horses in the persistent data store matching all provided fields.
   * The name is considered a match, if the search string is a substring.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return a stream of the horses where the given fields match.
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters);

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws ValidationException if the update data given for the horse is invalid
   */
  HorseDetailDto update(HorseDetailDto horse) throws ValidationException, ConflictException;


  /**
   * Get the horse with given ID, with more detail information.
   * This includes the breed of the horse.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Create a horse with the data given.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the data given for the horse is invalid
   */
  HorseDetailDto create(HorseDetailDto horse) throws ValidationException;

  /**
   * Delete the horse with the given ID.
   *
   * @param id the ID of the horse to delete
   */
  void delete(long id) throws ConflictException;
}
