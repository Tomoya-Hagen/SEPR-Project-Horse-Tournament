import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

/**
 * Service for formatting errors.
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorFormatterService {

  constructor(
    private domSanitizer: DomSanitizer,
  ) { }

  /**
   * formats an error into a string
   *
   * @param error the error
   * @returns the formatted error
   */
  format(error: any): string {
    let message = this.domSanitizer.sanitize(SecurityContext.HTML, error.error.message) ?? '';
    if (!!error.error.errors) {
      message += ':<ul>';
      for (const e of error.error.errors) {
        /* Use Angular's DomSanitizer to strip dangerous parts out of the HTML
         * before putting it into the error message.
         * Toastr already does this, but it can't hurt to do here too,
         * in case the library every fails to do it.
         */
        const sanE = this.domSanitizer.sanitize(SecurityContext.HTML, e);
        message += `<li>${sanE}</li>`;
      }
      message += '</ul>';
    } else {
      message += '.';
    }
    return message;
  }
}
