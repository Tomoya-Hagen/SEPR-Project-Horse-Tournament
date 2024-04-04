package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final BreedService breedService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, BreedService breedService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.breedService = breedService;
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) throws NotFoundException {
    LOG.trace("search({})", searchParameters);
    if (searchParameters == null) {
      String message = "Search parameters must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      var horses = dao.search(searchParameters);

      var breeds = horses.stream()
          .map(Horse::getBreedId)
          .filter(Objects::nonNull)
          .collect(Collectors.toUnmodifiableSet());

      var breedsPerId = breedMapForHorses(breeds);

      return horses.stream()
          .map(horse -> mapper.entityToListDto(horse, breedsPerId));
    } catch (NotFoundException e) {
      LOG.warn("Horses not found");
      throw e;
    }
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws ValidationException, ConflictException, FatalException, NotFoundException {
    LOG.trace("update({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      validator.validateForUpdate(horse);
      var updatedHorse = dao.update(horse);
      var breeds = breedMapForSingleHorse(updatedHorse);
      return mapper.entityToDetailDto(updatedHorse, breeds);
    } catch (ValidationException e) {
      LOG.warn("Horse validation failed");
      throw e;
    } catch (FatalException e) {
      LOG.warn("Horse not found with id: {}", horse.id());
      throw e;
    } catch (NotFoundException e) {
      LOG.warn("No breed found for horse with id: {}", horse.id());
      throw e;
    }
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException, FatalException {
    LOG.trace("getById({})", id);
    if (id <= 0) {
      String message = "Id must be greater than zero";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      Horse horse = dao.getById(id);
      var breeds = breedMapForSingleHorse(horse);
      return mapper.entityToDetailDto(horse, breeds);
    } catch (NotFoundException e) {
      LOG.warn("Horse or its breed not found with id: {}", id);
      throw e;
    } catch (FatalException e) {
      LOG.error("There must not be horses with same id: {}", id, e);
      throw e;
    }
  }

  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) throws NotFoundException {
    LOG.trace("breedsMapForSingleHorse({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      return breedMapForHorses(Collections.singleton(horse.getBreedId()));
    } catch (NotFoundException e) {
      LOG.warn("No breed map found for horse with id: {}", horse.getId());
      throw e;
    }
  }

  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horse) throws NotFoundException {
    LOG.trace("breeds({})", horse);
    if (horse == null || horse.isEmpty()) {
      String message = "Horse must not be null or empty";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      return breedService.findBreedsByIds(horse)
          .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
    } catch (NotFoundException e) {
      LOG.warn("No breed map found for horse with ids: {}", horse);
      throw e;
    }
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, FatalException, NotFoundException {
    LOG.trace("create({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    try {
      validator.validateForCreate(horse);
      Horse createdHorse = dao.create(horse);
      var breeds = breedMapForSingleHorse(createdHorse);
      return mapper.entityToDetailDto(createdHorse, breeds);
    } catch (ValidationException e) {
      LOG.warn("Horse validation failed");
      throw e;
    } catch (FatalException e) {
      LOG.error("The horse could not be created", e);
      throw e;
    } catch (NotFoundException e) {
      LOG.warn("Horse used to check redundancy of id not found with id: {}", horse.id());
      throw e;
    }
  }

  @Override
  public void delete(long id) throws FatalException {
    LOG.trace("delete({})", id);
    if (id <= 0) {
      LOG.warn("Error occurred while deleting a horse: No horse with id 0 exists");
      throw new IllegalArgumentException("id must be greater than 0");
    }
    try {
      dao.delete(id);
    } catch (FatalException e) {
      LOG.error("Horse with id could not be deleted : {}", id, e);
      throw e;
    }
  }
}
