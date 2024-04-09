import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Breed} from "../dto/breed";
import {catchError, Observable} from 'rxjs';

const baseUri = environment.backendUrl + "/breeds";

/**
 * Service for handling requests to the backend.
 * Specifically, it handles the GET requests to the backend.
 */
@Injectable({
  providedIn: 'root'
})
export class BreedService {


  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * gets the breeds from the backend
   * @param name name of the breed
   * @param limit limit of the results
   * @returns An obersvable that contains an array of breeds
   * @throws an error if the request fails
   */
  public breedsByName(name: string, limit: number | undefined): Observable<Breed[]> {
    let params = new HttpParams();
    params = params.append("name", name);
    if (limit != null) {
      params = params.append("limit", limit);
    }
    return this.http.get<Breed[]>(baseUri, { params })
    .pipe(
      catchError((error) => {
        console.error('Error searching for breeds', error);
        throw error;
      })
    );
  }
}
