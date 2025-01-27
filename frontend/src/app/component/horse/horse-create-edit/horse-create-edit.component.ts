import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, retry} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {Breed} from "../../../dto/breed";
import {BreedService} from "../../../service/breed.service";

/**
 * enum for the different modes that the horse create edit component can be in
 */
export enum HorseCreateEditMode {
  create,
  edit,
}

/**
 * Component for creating and editing horses
 */
@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: null,
    height: null,
    weight: null,
  };

  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;


  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    if (value <= 0 || value >= 100) {
      return;
    }
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    if (value <= 0 || value >= 100000) {
      return;
    }
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = value;
  }


  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) { }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }

  get modeIsEdit(): boolean {
    return this.mode === HorseCreateEditMode.edit;
  }

  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  /**
   * Loads the horses to create or edit.
   */
  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.service.getById(id).subscribe(horse => {
        this.horse = horse;
        this.mode = HorseCreateEditMode.edit;
      });
    } else {
      this.mode = HorseCreateEditMode.create;
    }
  }

  /**
   * Returns boolean whether the input is invalid
   * @param input to input to check
   * @returns true if the input is invalid, flase otherwise
   */
  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  /**
   * formats the breed name
   *
   * @param breed the breed to format
   * @returns string representation of the breed
   */
  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    :  this.breedService.breedsByName(input, 5);

    /**
     * This method is called when the form is submitted.
     * It creates or updates the horse.
     *
     * @param form the NgForm
     */
  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          if(this.mode === HorseCreateEditMode.create) {
            console.error('Error creating horse', error);
            this.notification.error('Could not create horse: ' + error.message);
          }
          if(this.mode === HorseCreateEditMode.edit) {
            console.error('Error updating horse', error);
            this.notification.error('Could not update horse: ' + error.message);
          }
        }
      });
    }
  }

  /**
   * deletes the given horse
   *
   * @param horse the horse to delete
   * @throw an error if the horse has no id or the horse is not defined
   */
  deleteHorse(horse: Horse): void {
    if (!horse) {
      throw Error("HorseForDeletion is null or undefined");
    }
    if (!horse.id) {
      throw Error("HorseForDeletion has no id");
    }
    this.service.delete(horse.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${horse.name} deleted`, 'Horse Deleted');
        this.router.navigate(['/horses']);
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error('Could not delete horse: ' + error.message, 'Error');
      }
    })
  }

}
