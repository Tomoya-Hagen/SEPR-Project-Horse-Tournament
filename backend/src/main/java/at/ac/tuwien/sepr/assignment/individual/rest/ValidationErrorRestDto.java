package at.ac.tuwien.sepr.assignment.individual.rest;

import java.util.List;

/**
 * A REST-DTO for a validation error.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
