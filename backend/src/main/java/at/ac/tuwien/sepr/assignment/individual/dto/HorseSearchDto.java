package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name the name of the horse to search for
 * @param sex the sex of the horse to search for
 * @param bornEarliest the earliest date of birth of the horse to search for
 * @param bornLatest the latest date of birth of the horse to search for
 * @param breed the breed of the horse to search for
 * @param limit the maximum number of results to return
 */
public record HorseSearchDto(
    String name,
    Sex sex,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornEarliest,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornLatest,
    String breed,
    Integer limit
) {
}
