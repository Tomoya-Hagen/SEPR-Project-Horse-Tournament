package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
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
   * @return ResponseEntity with status 200, along with the horses if there are results, 204 otherwise.
   */
  @GetMapping
  public ResponseEntity<Stream<HorseListDto>> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters:\n{}", searchParameters);
    Stream<HorseListDto> result = service.search(searchParameters);
    return (result == null || result.findAny().isEmpty())
        ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        : ResponseEntity.ok(service.search(searchParameters));
  }

  /**
   * Gets a horse by id.
   *
   * @param id The id of the horse to get
   * @return ResponseEntity with status 200 with the horse if it is obtained, 404 otherwise.
   */
  @GetMapping("{id}")
  public ResponseEntity<HorseDetailDto> getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    LOG.debug("request parameters: {}", id);
    try {
      return ResponseEntity.ok(service.getById(id));
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }


  /**
   * Updates a horse.
   *
   * @param toUpdate The horse to update
   * @return ResponseEntity with status 204, along with the horse if the horse was updated, 422 otherwise
   */
  @PutMapping("{id}")
  public ResponseEntity<HorseDetailDto> update(@RequestBody HorseDetailDto toUpdate) {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.update(toUpdate));
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      return ResponseEntity.status(status).build();
    }
  }


  /**
   * Creates a new horse with the given parameter.
   *
   * @param toCreate The horse to create
   * @return ResponseEntity with status 201, along with the horse if the horse was created, 422 if validation failed
   */
  @PostMapping
  public ResponseEntity<HorseDetailDto> create(@RequestBody HorseDetailDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", toCreate);
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(service.create(toCreate));
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      return ResponseEntity.status(status).build();
    }
  }

  /**
   * Deletes a horse by id.
   *
   * @param id the id of the horse to be deleted
   * @return ResponseEntity with status 204
   */
  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    LOG.debug("request parameters: {}", id);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
