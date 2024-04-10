package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * DAO for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

  private static final String SQL_SELECT_BY_IDS = "SELECT * FROM " + TABLE_NAME + " WHERE id IN (:ids)";
  private static final String SQL_SELECT_SEARCH = "SELECT  "
          + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
          + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME + " h LEFT OUTER JOIN breed b ON (h.breed_id = b.id)"
          + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:sex IS NULL OR :sex = sex)"
          + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
          + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
          + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_MAX_ID = " SELECT MAX(id) FROM " + TABLE_NAME;
  private static final String SQL_MIN_ID = " SELECT MIN(id) FROM " + TABLE_NAME;


  private static final String SQL_UPDATE = "UPDATE "
      + TABLE_NAME
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private static final String SQL_CREATE = "INSERT INTO "
      + TABLE_NAME + "(name, sex, date_of_birth, height, weight, breed_id) "
      + "VALUES (?, ?, ?, ?, ?, ?)";

  private static final String SQL_DELETE = "DELETE FROM "
      + TABLE_NAME
      + " WHERE id = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  private HorseTournamentJdbcDao horseTournamentDao;


  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate,
      HorseTournamentJdbcDao horseTournamentDao) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
    this.horseTournamentDao = horseTournamentDao;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      LOG.warn("Could not find horse with ID {}", id);
      throw new NotFoundException("Could not find horse with ID " + id);
    }

    return horses.get(0);
  }

  @Override
  public List<Horse> getByIds(Set<Long> ids) throws NotFoundException {
    LOG.trace("getByIds({})", ids);
    List<Horse> horses;
    horses = jdbcNamed.query(SQL_SELECT_BY_IDS, Map.of("ids", ids), this::mapRow);

    if (horses.isEmpty()) {
      LOG.warn("Could not find horse with ID {}", ids);
      throw new NotFoundException("Could not find horse with ID " + ids);
    }

    return horses;
  }


  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);
    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws ConflictException {
    LOG.trace("update({})", horse);

    Long minId = jdbcTemplate.queryForObject(SQL_MIN_ID, Long.class);
    Long maxId = jdbcTemplate.queryForObject(SQL_MAX_ID, Long.class);

    // Check if the horse ID is outside the range of minimum and maximum IDs
    if (minId != null && maxId != null) {
      if (horse.id() < minId || horse.id() > maxId) {
        LOG.warn("Update of horse failed. Horse ID is outside the range of IDs stored in the database.");
        List<String> errors = new ArrayList<>();
        errors.add("Update of horse failed. Horse ID is outside the range of IDs stored in the database.");
        throw new ConflictException("Horse ID is outside the range of IDs stored in the database", errors);
      }
    }

    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        horse.breed() == null ? null : horse.breed().id(),
        horse.id());

    if (updated == 0) {
      LOG.error("Update of horse failed.");
      throw new FatalException("Could not update horse with ID " + horse.id());
    }

    Horse updatedHorse = new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        ;

    if (horse.breed() != null) {
      updatedHorse.setBreedId(horse.breed().id());
    }

    return updatedHorse;
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getObject("breed_id", Long.class))
        ;
  }


  @Override
  public Horse create(HorseDetailDto horse) {
    LOG.trace("create({})", horse);

    int created = jdbcTemplate.update(SQL_CREATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        horse.breed() == null ? null : horse.breed().id())
        ;

    if (created == 0) { //update returns the number of rows updated
      LOG.error("Creation of horse failed.");
      throw new FatalException("Could not create horse");
    }

    Horse newHorse = new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        ;

    if (horse.breed() != null) {
      newHorse.setBreedId(horse.breed().id());
    }

    return newHorse;
  }

  @Override
  public void delete(long id) throws ConflictException {
    LOG.trace("delete({})", id);

    if (!horseTournamentDao.getParticipatingHorse(id).isEmpty()) {
      LOG.warn("Deletion of horse with ID {} failed. Horse is participating in a tournament.", id);
      List<String> errors = new ArrayList<>();
      errors.add("Horse is participating in a tournament.");
      throw new ConflictException("Could not delete horse with ID " + id, errors);
    }

    int deleted = jdbcTemplate.update(SQL_DELETE, id);

    if (deleted == 0) {
      LOG.error("Deletion of horse with ID {} failed.", id);
      throw new FatalException("Could not delete horse with ID " + id);
    }
  }

}
