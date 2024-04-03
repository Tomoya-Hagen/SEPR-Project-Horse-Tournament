package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchParamsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
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

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";
  private final TournamentService service;

  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchParamsDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  @PostMapping
  public ResponseEntity<TournamentDetailDto> create(@RequestBody TournamentCreateDto tournament) throws ValidationException, NotFoundException {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", tournament);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tournament));
  }

  @GetMapping("standings/{id}")
  public ResponseEntity<TournamentStandingsDto> getStandings(@PathVariable("id") Long tournamentId) throws NotFoundException {
    LOG.info("GET " + BASE_PATH + "/standings" + "/{}", tournamentId);
    LOG.debug("request parameters: {}", tournamentId);
    return ResponseEntity.ok(service.getStandingsByTournamentId(tournamentId));
  }

  @PatchMapping("standings/{id}")
  public ResponseEntity<TournamentStandingsDto> updateStandings(@PathVariable("id") Long tournamentId,
                                                                @RequestBody TournamentStandingsDto standings) throws NotFoundException, ValidationException {
    LOG.info("PATCH " + BASE_PATH + "/standings" + "/{}", tournamentId);
    LOG.debug("request parameters: {}", tournamentId);
    return ResponseEntity.ok(service.updateStandings(standings));
  }

}
