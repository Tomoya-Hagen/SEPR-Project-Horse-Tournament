import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseListDto} from '../../dto/horse';
import {HorseSearch} from '../../dto/horse';
import {debounceTime, map, Observable, of, Subject} from 'rxjs';
import {BreedService} from "../../service/breed.service";

/**
 * Component for handling operations on horses.
 */
@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
/**
 * class for handling operations on horses.
 */
export class HorseComponent implements OnInit {
  search = false;
  horses: HorseListDto[] = [];
  bannerError: string | null = null;
  searchParams: HorseSearch = {};
  searchBornEarliest: string | null = null;
  searchBornLatest: string | null = null;
  horseForDeletion: Horse | undefined;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadHorses()});
  }

  /**
   * Reloads the horses
   */
  reloadHorses() {
    if (this.searchBornEarliest == null || this.searchBornEarliest === "") {
      delete this.searchParams.bornEarliest;
    } else {
      this.searchParams.bornEarliest = new Date(this.searchBornEarliest);
    }
    if (this.searchBornLatest == null || this.searchBornLatest === "") {
      delete this.searchParams.bornLastest;
    } else {
      this.searchParams.bornLastest = new Date(this.searchBornLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  /**
    * Notifies subscribers that the search criteria has changed.
    * This method is typically called when the user updates the search criteria.
    */
  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  /**
   * Suggests breed names based on the input string.
   *
   * @param input name of the horse
   * @returns an observable for the list of suggested breed names
   */
  breedSuggestions = (input: string): Observable<string[]> =>
    this.breedService.breedsByName(input, 5)
      .pipe(map(bs =>
        bs.map(b => b.name)));

  formatBreedName = (name: string) => name; // It is already the breed name, we just have to give a function to the component

  /**
   * deletes a horse
   *
   * @param horse the horse to delete
   * @throws an error if the horse has no id or the horse is not defined
   */
  deleteHorse(horse: Horse): void {
    this.horseForDeletion = horse;
    if (!this.horseForDeletion) {
      throw Error("HorseForDeletion is null or undefined");
    }
    if (!this.horseForDeletion.id) {
      throw Error("HorseForDeletion has no id");
    }
    this.service.delete(this.horseForDeletion.id).subscribe({
      next: () => {
        this.horses = this.horses.filter(h => h.id !== this.horseForDeletion?.id);
        this.notification.success(`Horse ${this.horseForDeletion?.name} deleted`, 'Horse Deleted');
        this.horseForDeletion = undefined;
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error('Could not delete horse: Horse is participating in a tournament', 'Error');
      }
    })
  }
}
