import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';
import { MessageService } from './message.service';
import { GosEvent } from './model/event';
import { EventRecord } from './model/eventRecord';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  private eventsUrl = '/api/event';

  constructor(private http: HttpClient,
    private messageService: MessageService) { }

  getEvents(): Observable<GosEvent[]> {
    return this.http.get<GosEvent[]>(this.eventsUrl)
      .pipe(
        tap(_ => this.log('fetched events')),
        catchError(this.handleError<GosEvent[]>('getEvents', []))
      );
  }

  addEvent(event: GosEvent): Observable<GosEvent> {
    return this.http.post<GosEvent>(this.eventsUrl, event, this.httpOptions)
      .pipe(tap((newEvent: GosEvent) => this.log(`added event ${newEvent.name}`)),
        catchError(this.handleError<GosEvent>('add person')));
  }

  getAllRecords(eventId: string): Observable<EventRecord[]> {
    return this.http.get<EventRecord[]>(`${this.eventsUrl}/record/${eventId}`)
      .pipe(tap(_ => this.log('fetched records')),
        catchError(this.handleError<EventRecord[]>('getRecords', []))
      );
  }

  getRecords(eventId: string, count: number): Observable<EventRecord[]> {
    return this.http.get<EventRecord[]>(`${this.eventsUrl}/record/${eventId}/${count}`)
      .pipe(tap(_ => this.log('fetched records')),
        catchError(this.handleError<EventRecord[]>('getRecords', []))
      );
  }

  addRecord(eventId: string, record: EventRecord): Observable<EventRecord> {
    return this.http.post<EventRecord>(`${this.eventsUrl}/record/${eventId}`, record, this.httpOptions)
      .pipe(tap((newRecord: EventRecord) => this.log(`added record ${newRecord.value}`)),
        catchError(this.handleError<EventRecord>('add record')));
  }

  private log(message: string) {
    this.messageService.add(`EventService: ${message}`);
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
