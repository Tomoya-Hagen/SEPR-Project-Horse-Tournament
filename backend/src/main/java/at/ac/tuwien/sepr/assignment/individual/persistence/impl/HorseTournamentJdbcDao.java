package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.entity.HorseTournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseTournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO for working with horses in a tournament.
 * Implements access functionality to the application's persistent data store regarding horses in a tournament.'
 */
@Repository
public class HorseTournamentJdbcDao implements HorseTournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament_horses";

  private static final String SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS = "SELECT "
      + " horse_id, entry_number, round_reached, tournament_id"
      + " FROM " + TABLE_NAME
      + " WHERE tournament_id = ? ";

  private static final String SQL_SET_ENTRY_NUMBER_AND_REACHED_ROUND = "UPDATE "
      + " tournament_horses " + " SET entry_number = ?, round_reached = ? WHERE horse_id = ? AND tournament_id = ?";

  private final JdbcTemplate jdbcTemplate;

  public HorseTournamentJdbcDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  private HorseTournament mapHorseRow(ResultSet rs, int rowNum) throws SQLException {
    return new HorseTournament(
        rs.getLong("horse_id"),
        rs.getLong("tournament_id"),
        rs.getInt("entry_number"),
        rs.getInt("round_reached")
    );
  }

  @Override
  public Collection<HorseTournament> getHorsesByIDTournament(Long tournamentId) {
    LOG.trace("getHorseByIDTournaments({})", tournamentId);
    return jdbcTemplate.query(SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS, this::mapHorseRow, tournamentId);
  }

  @Override
  public Map<Long, List<HorseTournament>> getHorsesByIDsTournaments(Set<Long> tournamentIds) {
    LOG.trace("getHorseByIDTournaments({})", tournamentIds);
    Map<Long, List<HorseTournament>> results = new HashMap<>();
    for (Long tournamentId : tournamentIds) {
      List<HorseTournament> horses = jdbcTemplate.query(SQL_SEARCH_HORSE_BY_ID_TOURNAMENTS, this::mapHorseRow, tournamentId);
      results.put(tournamentId, horses);
    }

    return results;
  }

  @Override
  public void updateStandings(Long tournamentId, Long horseId, int entryNumber, int roundReached) {
    LOG.trace("updateStandings({}, {}, {}, {})", tournamentId, horseId, entryNumber, roundReached);
    int updatedRows = jdbcTemplate.update(
        SQL_SET_ENTRY_NUMBER_AND_REACHED_ROUND,
        entryNumber,
        roundReached,
        horseId,
        tournamentId
    );
    if (updatedRows != 1) {
      LOG.warn("Standings could not be updated");
      throw new FatalException("Standings could not be updated");
    }
  }

}
