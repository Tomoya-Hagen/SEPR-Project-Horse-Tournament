import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, mergeMap, Observable, tap, throwError} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseListDto} from '../dto/horse';
import {HorseSearch} from '../dto/horse';
import {formatIsoDate} from '../util/date-helper';


const baseUri = environment.backendUrl + '/horses';

/**
 * Service for handling horses.
 * It operates on the REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Gets a horse by its id.
   *
   * @param id the id of the horse
   * @returns An observable for the horse
   * @throws an error if the horse was not found
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`)
    .pipe(
      catchError((error) => {
        console.error('Error fetching horse', error);
        throw error;
      })
    );
  }

  /**
   * searches for horses with the given parameters
   *
   * @param searchParams the search parameters
   * @returns An observable for the list of horses
   */
  search(searchParams: HorseSearch): Observable<HorseListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.sex) {
      params = params.append('sex', searchParams.sex);
    }
    if (searchParams.bornEarliest) {
      params = params.append('bornEarliest', formatIsoDate(searchParams.bornEarliest));
    }
    if (searchParams.bornLastest) {
      params = params.append('bornLatest', formatIsoDate(searchParams.bornLastest));
    }
    if (searchParams.breedName) {
      params = params.append('breed', searchParams.breedName);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.http.get<HorseListDto[]>(baseUri, { params })
      .pipe(tap(horses => horses.map(h => {
        h.dateOfBirth = new Date(h.dateOfBirth); // Parse date string
      })));
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   * @throws throws an error if the horse was not created
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    ).pipe(
      catchError((error) => {
        console.error('Error creating horse:', error);
        throw error;
      })
    );
  }

  /**
   * Update an existing horse in the system.
   *
   * @param horse the data for the horse that should be updated
   * @return an Observable for the updated horse
   * @throws an error if the horse has no id
   */
  update(horse: Horse): Observable<Horse> {
    if (horse.id) {
      return this.getById(horse.id).pipe(
        mergeMap(() => { //using mergeMap enables us to chain the asychnronous operations in a sequence
          return this.http.put<Horse>(
            `${baseUri}/${horse.id}`,
            horse
          );
        })
      );
    } else {
      return throwError(() => ({message: "Horse has no id"}));
    }
  }

  /**
   *
   * @param id the id of the horse that should be deleted
   * @returns an Observable for the deleted horse
   * @throws an error if the horse has no id
   */
  delete(id: number): Observable<void> {
    if (!id) {
      return throwError(() => ({message: "Horse has no id"}));
    }
    return this.http.delete<void>(`${baseUri}/${id}`)
    .pipe(
      catchError((error) => {
        console.error('Error deleting horse', error);
        throw error;
      })
    );
  }
}
