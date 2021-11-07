import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {MessageService} from './message.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';
import {Week} from "./model/week";
import {PlayerLayout} from "./model/playerLayout";

@Injectable({
  providedIn: 'root'
})
export class BattleService {

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };
  private weekUrl = '/api/week';

  constructor(private http: HttpClient,
              private messageService: MessageService) {
  }

  getWeek(): Observable<Week> {
    return this.http.get<Week>(this.weekUrl)
      .pipe(
        tap(_ => this.log('fetched players')),
        catchError(this.handleError<Week>('getPlayers', undefined))
      );
  }

  addPlayerLayout(playerLayout: PlayerLayout): Observable<PlayerLayout> {
    return this.http.post<PlayerLayout>(this.weekUrl, playerLayout, this.httpOptions)
      .pipe(
        tap(_ => this.log("add player layout")),
        catchError(this.handleError<PlayerLayout>('addPlayerLayout', undefined))
      );
  }

  updateWeek(week: Week): Observable<Week> {
    const url = `${this.weekUrl}/${week.date}`;
    return this.http.put<Week>(url, week, this.httpOptions)
      .pipe(
        tap(_ => this.log("update week")),
        catchError(this.handleError<Week>('update week', undefined))
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
