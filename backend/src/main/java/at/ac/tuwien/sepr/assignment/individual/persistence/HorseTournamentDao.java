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


}
