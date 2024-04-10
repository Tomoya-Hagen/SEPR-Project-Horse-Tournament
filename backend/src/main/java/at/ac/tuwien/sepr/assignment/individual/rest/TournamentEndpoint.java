package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

/**
 * Class for handling REST requests for Tournaments.
 */
@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";
  private final TournamentService service;

  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }

  /**
   * Searches for tournaments with the given search parameters.
   *
   * @param searchParameters The search parameters
   * @return ResponseEntity with status 200, along with the tournaments found
   */
  @GetMapping
  public ResponseEntity<Stream<TournamentListDto>> searchTournaments(TournamentSearchParamsDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return ResponseEntity.ok(service.search(searchParameters));
  }

  /**
   * Creates a new tournament with the given parameter. It catches validation exceptions.
   *
   * @param tournament the tournament to create
   * @return ResponseEntity with status 201 if the tournament was created, along with the tournament
   * @throws ResponseStatusException 400 if validation failed
   */
  @PostMapping
  public ResponseEntity<TournamentDetailDto> create(@RequestBody TournamentCreateDto tournament) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", tournament);
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tournament));
    } catch (ValidationException | ConflictException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      LOG.warn("Error creating tournament", e);
      throw new ResponseStatusException(status, e.getMessage());
    }
  }

  /**
   * Gets the standings for the tournament with the given ID. It catches NotFoundExceptions.
   *
   * @param tournamentId the id of the tournament
   * @return ResponseEntity with status 200, along with the tournament standings if the tournament is obtained
   * @throws ResponseStatusException 404 if no tournament or standings was found
   */
  @GetMapping("standings/{id}")
  public ResponseEntity<TournamentStandingsDto> getStandings(@PathVariable("id") Long tournamentId) {
    LOG.info("GET " + BASE_PATH + "/standings" + "/{}", tournamentId);
    LOG.debug("request parameters: {}", tournamentId);
    try {
      return ResponseEntity.ok(service.getStandingsByTournamentId(tournamentId));
    } catch (NotFoundException e) {
      LOG.warn("An error occurred while getting the standings for the tournament with id {}", tournamentId);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }

  /**
   * Updates the standings for a given tournament. It catches validation exceptions and NotFoundExceptions.
   *
   * @param tournamentId the id of the tournament
   * @param standings the new standings
   * @return ResponseEntity with status 204 if the standings were updated
   * @throws ResponseStatusException 404 if no tournament/standings were found and 422 if validation failed
   */
  @PatchMapping("standings/{id}")
  public ResponseEntity<TournamentStandingsDto> updateStandings(@PathVariable("id") Long tournamentId,
                                                                @RequestBody TournamentStandingsDto standings) {
    LOG.info("PATCH " + BASE_PATH + "/standings" + "/{}", tournamentId);
    LOG.debug("request parameters: {}", tournamentId);
    try {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.updateStandings(standings));
    } catch (ValidationException | NotFoundException | ConflictException e) {
      HttpStatus status;
      if (e instanceof ValidationException) {
        status = HttpStatus.UNPROCESSABLE_ENTITY;
      } else if (e instanceof ConflictException) {
        status = HttpStatus.CONFLICT;
      } else {
        status = HttpStatus.NOT_FOUND;
      }
      LOG.warn("An error occurred while updating the standings for the tournament with id {}", tournamentId);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Generates the first round for the tournament with the given ID. It catches NotFoundExceptions.
   *
   * @param tournamentId ID of the tournament to generate the first round for
   * @param standings The standings for the tournament
   * @return ResponseEntity with status 204, along with the standings if the standings were updated
   * @throws ResponseStatusException 404 if no tournament/standings were found
   */
  @PatchMapping("standings/{id}/first")
  public ResponseEntity<TournamentStandingsDto> generateFirstRound(@PathVariable("id") Long tournamentId,
                                                                   @RequestBody TournamentStandingsDto standings) {
    LOG.info("PATCH " + BASE_PATH + "/standings" + "/{}/first", tournamentId);
    LOG.debug("request parameters: {}", tournamentId);
    try {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.generateFirstRound(standings));
    } catch (NotFoundException | ConflictException e) {
      HttpStatus status =  HttpStatus.BAD_REQUEST;
      LOG.warn("An error occurred while generating the first round for the tournament with id {}", tournamentId);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }



}
