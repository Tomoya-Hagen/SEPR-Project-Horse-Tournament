package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
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
    if (horse.sex() == null) {
      validationErrors.add("No sex given");
    }
    if (horse.dateOfBirth() == null) {
      validationErrors.add("No date of birth given");
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
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validateHelper(horse, validationErrors);

    if (!validationErrors.isEmpty()) {
      LOG.warn("Validation of horse for create failed: {}", validationErrors);
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }
}
