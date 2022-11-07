import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';
import { MessageService } from './message.service';
import { Person } from './model/person';

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  private personsUrl = '/api/person';
  private testPersons: Person[] = [
    {id: "123", name: "test1", date: "11/11/1990"},
    {id: "124", name: "test1 test2", date: "16/01/1996"},
    {id: "153", name: "test2", date: "11/11/1993"},
  ];

  constructor(private http: HttpClient,
    private messageService: MessageService) { }

    // getPersons(): Observable<Person[]> {
      // return of(this.testPersons);
    // }

  getPersons(): Observable<Person[]> {
    return this.http.get<Person[]>(this.personsUrl)
      .pipe(
        tap(_ => this.log('fetched persons')),
        catchError(this.handleError<Person[]>('getPersons', []))
      );
  }

  addPerson(person: Person): Observable<Person> {
    return this.http.post<Person>(this.personsUrl, person, this.httpOptions)
      .pipe(tap((newPerson: Person) => this.log(`added person ${newPerson.name}`)),
        catchError(this.handleError<Person>('add person')));
  }

  deletePerson(id: string): Observable<boolean> {
    const url = `${this.personsUrl}/${id}`;
    return this.http.delete<boolean>(url).pipe(
      tap((result: boolean) => this.log(`person ${id} deleted: ${result}`)),
      catchError(this.handleError<boolean>(`delete person id=${id}`))
    );
  }

  updatePerson(person: Person): Observable<Person> {
    const url = `${this.personsUrl}/${person.id}`;
    return this.http.put<Person>(url, person, this.httpOptions).pipe(
      tap((result: Person) => this.log(`person ${person.id} updated: ${result}`)),
      catchError(this.handleError<Person>(`update person id=${person.id}`))
    );
  }

  private log(message: string) {
    this.messageService.add(`PlayerService: ${message}`);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
