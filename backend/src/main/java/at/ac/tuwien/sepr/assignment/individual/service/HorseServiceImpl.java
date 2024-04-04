package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
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
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    if (searchParameters == null) {
      String message = "Search parameters must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    var horses = dao.search(searchParameters);

    var breeds = horses.stream()
        .map(Horse::getBreedId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());

    var breedsPerId = breedMapForHorses(breeds);

    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, breedsPerId));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    if (id <= 0) {
      String message = "Id must be greater than zero";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    Horse horse = dao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    LOG.trace("breeds({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }

  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horse) {
    LOG.trace("breeds({})", horse);
    if (horse == null || horse.isEmpty()) {
      String message = "Horse must not be null or empty";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    return breedService.findBreedsByIds(horse)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, NotFoundException {
    LOG.trace("create({})", horse);
    if (horse == null) {
      String message = "Horse must not be null";
      LOG.warn(message);
      throw new IllegalArgumentException(message);
    }
    validator.validateForCreate(horse);
    Horse createdHorse = dao.create(horse);
    var breeds = breedMapForSingleHorse(createdHorse);
    return mapper.entityToDetailDto(createdHorse, breeds);
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    if (id <= 0) {
      LOG.warn("Error occurred while deleting a horse: No horse with id 0 exists");
      throw new IllegalArgumentException("id must be greater than 0");
    }
    dao.delete(id);
  }
}
