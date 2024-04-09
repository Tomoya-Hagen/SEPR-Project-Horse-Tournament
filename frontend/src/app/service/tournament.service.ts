import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError, Observable, throwError} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentCreateDto, TournamentDetailDto, TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentStandingsTreeDto
} from "../dto/tournament";
const baseUri = environment.backendUrl + '/tournaments';

class ErrorDto {
  constructor(public message: String) {}
}
/**
 * Service for handling tournaments.
 * It operates on the REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }


  /**
   * Search for tournaments in the system
   *
   * @param searchParams the search parameters
   * @returns an Observable for the list of tournaments
   */
  public search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.startDate) {
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }
    return this.http.get<TournamentListDto[]>(baseUri, { params });
  }


  /**
   * Gets all tournaments in the system
   *
   * @returns an Observable for the list of all tournaments
   * @throws an error if the request fails
   */
  public getAll(): Observable<TournamentListDto[]> {
    return this.http.get<TournamentListDto[]>(baseUri)
    .pipe(
      catchError((error) => {
        console.error('Error fetching tournaments', error);
        throw error;
    }));
  }


  /**
   * Create a new tournament in the system.
   *
   * @param tournament the tournament that should be created
   * @returns an Observable for the created tournament
   * @throws throws an error if the tournament was not created
   */
  public create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    if (!tournament) {
      return throwError(() => new ErrorDto("No tournament provided"));
    }
    return this.http.post<TournamentDetailDto>(
      baseUri,
      tournament
    ).pipe(
      catchError((error) => {
        console.error('Error creating tournament:', error);
        throw error;
      })
    )
  }

  /**
   * Gets the standings of the tournament
   *
   * @param id the id of the tournament
   * @returns an Observable for the tournament
   * @throws an error if the tournament standings were not found
   */
  public getStandings(id: number): Observable<TournamentStandingsDto> {
    if (!id) {
      return throwError(() => new ErrorDto("No tournament id provided"));
    }
    return this.http.get<TournamentStandingsDto>(`${baseUri}/standings/${id}`)
    .pipe(
      catchError((error) => {
        console.error('Error fetching tournament standings:', error);
        throw error;
      })
    );
  }

  /**
   * Updates and saves the standings of the tournament
   *
   * @param tournamentStandings the tournament standings to save
   * @returns an Observable for the saved tournament standings
   * @throws an error if the tournament standings were not saved
   */
  public saveStandings(tournamentStandings: TournamentStandingsDto): Observable<TournamentStandingsDto> {
    if(!tournamentStandings) {
      return throwError(() => new ErrorDto("No tournament standing provided"));
    }
    return this.http.patch<TournamentStandingsDto>(
      `${baseUri}/standings/${tournamentStandings.id}`,
      tournamentStandings
    ).pipe(
      catchError((error) => {
        console.error('Error saving tournament standings:', error);
        throw error;
      })
    )
  }

  /**
   * generates the first round of the tournament
   *
   * @param tournamentStandings the tournament standings to generate the first round for
   * @returns an Observable for the generated tournament standings
   * @throws an error if the first round was not generated
   */
  public generateFirstRound(tournamentStandings: TournamentStandingsDto): Observable<TournamentStandingsDto> {
    if(!tournamentStandings) {
      return throwError(() => new ErrorDto("No tournament id provided"));
    }
    return this.http.patch<TournamentStandingsDto>(
      `${baseUri}/standings/${tournamentStandings.id}/first`,
      tournamentStandings
    ).pipe(
      catchError((error) => {
        console.error('Error generating first round:', error);
        throw error;
      })
    )
  }

}
