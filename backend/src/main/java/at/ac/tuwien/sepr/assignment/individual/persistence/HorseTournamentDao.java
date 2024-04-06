package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;

import java.util.Collection;

/**
 * DAO for working with horses in a tournament.
 * Implements access functionality to the application's persistent data store regarding horses in a tournament.'
 */
public interface HorseTournamentDao {

  /**
  * Get the horses participating in the tournament with the given id.
  *
  * @param tournamentId the id of the tournament
  * @return A collection of the horses participating in the tournament
  */
  Collection<HorseTournament> getHorsesByIDTournament(Long tournamentId);

  /**
   * Update the tournament with the given data.
   *
   * @param tournamentId the id of the tournament to update
   * @param horseId the id of the horse to update
   * @param entryNumber the entry number of the horse to update
   * @param roundReached the number of the round reached by the horse to update
   */
  void updateStandings(Long tournamentId, Long horseId, int entryNumber, int roundReached);


}
