import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, retry} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import { Breed } from 'src/app/dto/breed';
import { BreedService } from 'src/app/service/breed.service';

@Component({
  selector: 'app-horse-details',
  templateUrl: './horse-details.component.html',
  styleUrl: './horse-details.component.scss'
})
export class HorseDetailsComponent {
  horse: Horse | null = null;
  // horse!: Horse;

  constructor(
    private service: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.service.getById(id).subscribe(
        horse => {
          this.horse = horse;
      },
        error => {
          console.error('Error loading horse', error);
          this.notification.error('Could not load horse: ' + error.message, 'Error');
        }
      );
    } else {
      this.router.navigate(['/horses']);
    }
  }


  deleteHorse(horse: Horse): void {
    if (!horse) {
      throw Error("HorseForDeletion is null or undefined");
    }
    if (!horse.id) { //TODO: add condition for invalid id
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
