package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Class for handling REST requests for Horses
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Searches for horses based on the provided search parameters.
   *
   * @param searchParameters The search parameters
   * @return ResponseEntity with status 200, along with the horses found
   */
  @GetMapping
  public ResponseEntity<Stream<HorseListDto>> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters:\n{}", searchParameters);
    return ResponseEntity.ok(service.search(searchParameters));
  }

  /**
   * Gets a horse by id. It catches the NotFoundException and returns a 404 status code.
   *
   * @param id The id of the horse to get
   * @return ResponseEntity with status 200 with the horse if it is obtained
   * @throws ResponseStatusException 404 if no horse was found
   */
  @GetMapping("{id}")
  public ResponseEntity<HorseDetailDto> getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    LOG.debug("request parameters: {}", id);
    try {
      return ResponseEntity.ok(service.getById(id));
    } catch (NotFoundException e) {
      LOG.warn("An error occurred while getting the horse with id {}", id);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
  }


  /**
   * Updates a horse. It catches the Validation exceptions and returns a 422 status code.
   *
   * @param toUpdate The horse to update
   * @return ResponseEntity with status 204, along with the horse if the horse was updated.
   * @throws ResponseStatusException 422 if validation fails
   */
  @PutMapping("{id}")
  public ResponseEntity<HorseDetailDto> update(@RequestBody HorseDetailDto toUpdate) {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.update(toUpdate));
    } catch (ValidationException | ConflictException e) {
      HttpStatus status = e instanceof ValidationException ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.CONFLICT;
      LOG.warn("An error occurred while updating the horse with id {}", toUpdate.id());
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  /**
   * Creates a new horse with the given parameter. It catches the Validation exceptions and returns a 422
   *
   * @param toCreate The horse to create
   * @return ResponseEntity with status 201, along with the horse if the horse was created.
   * @throws ResponseStatusException 400 if validation fails
   */
  @PostMapping
  public ResponseEntity<HorseDetailDto> create(@RequestBody HorseDetailDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", toCreate);
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(service.create(toCreate));
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      LOG.warn("An error occurred while creating the horse");
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Deletes a horse by id. It catches the ConflictException and returns a 409 status code.
   *
   * @param id the id of the horse to be deleted
   * @return ResponseEntity with status 204 if deletion was successful.
   * @throws ResponseStatusException 409 if deletion fails, due to a conflict.
   */
  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    LOG.debug("request parameters: {}", id);
    try {
      service.delete(id);
      return ResponseEntity.noContent().build();
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      LOG.warn("An error occurred while deleting the horse with id {}", id);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }
}
