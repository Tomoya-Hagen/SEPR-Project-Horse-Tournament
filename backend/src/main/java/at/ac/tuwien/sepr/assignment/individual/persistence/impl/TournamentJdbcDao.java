package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";
  private static final String SQL_SELECT_SEARCH_TOURNAMENT = "SELECT "
      + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\", t.end_date as \"end_date\""
      + " FROM " + TABLE_NAME + " t"
      + " WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%'))"
      + "  AND ("
      + "(:startDate IS NULL OR :startDate <= t.start_date) AND (:endDate IS NULL OR :endDate >= t.end_date)"
      + " OR "
      + "(:startDate IS NULL OR :startDate <= t.start_date) AND (:endDate IS NULL OR :endDate >= t.start_date)"
      + " OR "
      + "(:startDate IS NULL OR :startDate <= t.end_date) AND (:endDate IS NULL OR :endDate >= t.end_date)"
      + " OR "
      + "(:startDate IS NULL OR :startDate >= t.start_date) AND (:endDate IS NULL OR :endDate <= t.end_date)"
      + ")"
      + " ORDER BY t.start_date";
  private static final String SQL_CREATE = "INSERT INTO "
      + TABLE_NAME + "(name, start_date, end_date) "
      + "VALUES (?, ?, ?)";

  private static final String SQL_FIND_TOURNAMENT_ID = "SELECT "
      + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\", t.end_date as \"end_date\""
      + " FROM " + TABLE_NAME + " t"
      + " WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%'))"
      + "  AND (:startDate IS NULL OR t.start_date = :startDate)"
      + "  AND (:endDate IS NULL OR t.end_date = :endDate)";

  private static final String SQL_SEARCH_TOURNAMENT_BY_ID = "SELECT "
      + "    t.id as \"id\", t.name as \"name\", t.start_date as \"start_date\", t.end_date as \"end_date\""
      + " FROM " + TABLE_NAME + " t"
      + " WHERE id = ?";

  private static final String SQL_ASSOCIATE_HORSE_WITH_TOURNAMENT =
      "INSERT INTO " + " tournament_horses " + " (tournament_id, horse_id) VALUES (?, ?)";
  private static final String SQL_SET_ENTRY_NUMBER_AND_REACHED_ROUND = "UPDATE "
      + " tournament_horses " + " SET entry_number = ?, round_reached = ? WHERE horse_id = ? AND tournament_id = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(
      JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }


  private Tournament mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Tournament()
        .setId(rs.getLong("id"))
        .setName(rs.getString("name"))
        .setStartDate(rs.getDate("start_date").toLocalDate())
        .setEndDate(rs.getDate("end_date").toLocalDate());
  }


  @Override
  public Collection<Tournament> search(TournamentSearchParamsDto searchParams) {
    LOG.trace("search({})", searchParams);
    String query = SQL_SELECT_SEARCH_TOURNAMENT;
    var params = new BeanPropertySqlParameterSource(searchParams);
    params.registerSqlType("name", Types.VARCHAR);
    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Tournament create(TournamentCreateDto tournament) throws FatalException {
    LOG.trace("create({})", tournament);
    if (tournament == null) {
      throw new IllegalArgumentException("tournament must not be null");
    }
    List<HorseSelectionDto> participants = tournament.participants();
    if (participants == null || participants.size() != 8) {
      throw new IllegalArgumentException("horseIDs must not be null and must contain exactly 8 elements");
    }

    int created = jdbcTemplate.update(
        SQL_CREATE,
        tournament.name(),
        tournament.startDate(),
        tournament.endDate()
    );

    if (created == 0) {
      throw new FatalException("Tournament could not be created");
    }

    String query = SQL_FIND_TOURNAMENT_ID;
    var params = new BeanPropertySqlParameterSource(new TournamentSearchParamsDto(
        tournament.name(),
        tournament.startDate(),
        tournament.endDate()
    ));
    params.registerSqlType("name", Types.VARCHAR);
    Long tournamentID =  jdbcNamed.query(query, params, this::mapRow).get(0).getId();

    for (HorseSelectionDto participantDto : participants) {
      int rowAffected = jdbcTemplate.update(
          SQL_ASSOCIATE_HORSE_WITH_TOURNAMENT,
          tournamentID,
          participantDto.id()
      );
      if (rowAffected == 0) {
        throw new FatalException("Horse could not be associated with tournament");
      }
      updateStandings(tournamentID, participantDto.id(), 0, 0);
    }

    return new Tournament()
        .setId(tournamentID)
        .setName(tournament.name())
        .setStartDate(tournament.startDate())
        .setEndDate(tournament.endDate());
  }




  @Override
  public Tournament getById(Long tournamentId) {
    LOG.trace("getById({})", tournamentId);
    return jdbcTemplate.query(SQL_SEARCH_TOURNAMENT_BY_ID, this::mapRow, tournamentId).get(0);
  }

  @Override
  public void updateStandings(Long tournamentId, Long horseId, int entryNumber, int roundReached) throws FatalException {
    LOG.trace("updateStandings({}, {}, {}, {})", tournamentId, horseId, entryNumber, roundReached);
    int updatedRows = jdbcTemplate.update(
        SQL_SET_ENTRY_NUMBER_AND_REACHED_ROUND,
        entryNumber,
        roundReached,
        horseId,
        tournamentId
    );
    if (updatedRows != 1) {
      throw new FatalException("Entry number and round reached could not be set for horse");
    }

  }

}
