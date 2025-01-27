package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Interface for DAO for tournaments.
 * Implements access functionality to the application's persistent data store regarding tournaments.
 */
public interface TournamentDao {

  /**
   * Return the tournaments that match with the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParams the parameters to use in searching.
   * @return A collection of the tournaments where all given parameters match.
   */
  Collection<Tournament> search(TournamentSearchParamsDto searchParams);


  /**
   * Create a new tournament with the given data.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   */
  Tournament create(TournamentCreateDto tournament) throws ConflictException;


  /**
   * Get the tournament with the given id.
   *
   * @param tournamentId the id of the tournament to get
   * @return the tournament with the given id
   */
  Tournament getById(Long tournamentId);

  /**
   * Get the tournaments of the last 12 months.
   *
   * @param startDate this tournament's start date
   * @return the tournaments of the last 12 months, excluding the given start date
   * @throws NotFoundException if no tournaments could be found
   */
  List<Tournament> getLast12MonthsTournaments(LocalDate startDate) throws NotFoundException;
}
