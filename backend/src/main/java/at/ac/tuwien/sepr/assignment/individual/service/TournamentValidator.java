package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private void validateHelper(TournamentCreateDto tournament, List<String> validationErrors) throws ValidationException {
    if (tournament == null) {
      throw new ValidationException("No tournament given", validationErrors);
    }
    if (tournament.name() == null) {
      validationErrors.add("No name given");
    }
    if (tournament.startDate() == null) {
      validationErrors.add("No date given");
    }
    if (tournament.endDate() == null) {
      validationErrors.add("No date given");
    }
    if (tournament.participants() == null || tournament.participants().size() != 8) {
      validationErrors.add("No 8 horses given");
    }

  }

  public void validateForCreate(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    validateHelper(tournament, validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }
}
