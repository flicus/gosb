import { Component, OnInit } from '@angular/core';
import { MessageService } from '../message.service';
import { Person } from '../model/person';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-persons',
  templateUrl: './persons.component.html',
  styleUrls: ['./persons.component.scss']
})
export class PersonsComponent implements OnInit {

  persons: Person[] = []

  constructor(private personService: PersonService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.getPersons();
  }

  getPersons(): void {
    this.personService.getPersons()
      .subscribe(persons => this.persons = persons);
  }

  addPerson(element: HTMLInputElement): void {
    let name: string = element.value.trim();
    if (!name) {
      return;
    }
    this.personService.addPerson({name} as Person)
      .subscribe(person => {
        console.error(person);
        this.persons.push(person);
        element.value = '';
      });
  }

  checkAndAdd(event: KeyboardEvent, element: HTMLInputElement): void {
    if (event.key == "Enter") {
      this.addPerson(element);
    }
  }

  deletePerson(id: string): void {
    id = id.trim();
    if (!id) {
      return;
    }
    this.personService.deletePerson(id)
      .subscribe(value => {
        if (value) {
          this.persons = this.persons.filter(p => {
            return p.id != id
          });
        }
      })
  }
}
