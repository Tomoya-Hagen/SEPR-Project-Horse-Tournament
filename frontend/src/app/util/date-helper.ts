import {formatDate} from "@angular/common";

/**
 * function to format a date
 *
 * @param date the date to format
 * @returns the formatted date
 */
export function formatIsoDate(date: Date): string {
  return formatDate(date, 'yyyy-MM-dd', 'en-DK');
}
