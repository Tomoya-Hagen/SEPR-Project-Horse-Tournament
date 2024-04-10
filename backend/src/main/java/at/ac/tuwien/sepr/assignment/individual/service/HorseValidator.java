package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private void validateHelper(HorseDetailDto horse, List<String> validationErrors) {
    if (horse.name() == null) {
      validationErrors.add("No name given");
    }
    if (horse.name() != null && horse.name().length() > 100) {
      validationErrors.add("Name too long");
    }
    if (horse.sex() == null) {
      validationErrors.add("No sex given");
    }
    if (horse.dateOfBirth() == null) {
      validationErrors.add("No date of birth given");
    }
    if (horse.dateOfBirth() != null && (horse.dateOfBirth().isAfter(LocalDate.now()) || horse.dateOfBirth().isBefore(LocalDate.of(1970, 1, 1)))) {
      validationErrors.add("Invalid date of birth given");
    }
    if (horse.height() <= 0 || horse.height() >= 100) {
      validationErrors.add("Invalid height given");
    }
    if (horse.weight() <= 0 || horse.weight() >= 100000) {
      validationErrors.add("Invalid weight given");
    }
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validateHelper(horse, validationErrors);

    if (!validationErrors.isEmpty()) {
      LOG.warn("Invalid horse for update: {}", validationErrors);
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validateHelper(horse, validationErrors);

    if (!validationErrors.isEmpty()) {
      LOG.warn("Invalid horse for create: {}", validationErrors);
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }
}
