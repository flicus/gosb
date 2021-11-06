import {Injectable} from '@angular/core';
import {Player} from "./model/player";
import {Observable, of} from 'rxjs';
import {MessageService} from './message.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {

  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };
  private playersUrl = 'http://127.0.0.1:9090/api/player';

  constructor(private http: HttpClient,
              private messageService: MessageService) {
  }

  getPlayers(): Observable<Player[]> {
    return this.http.get<Player[]>(this.playersUrl)
      .pipe(
        tap(_ => this.log('fetched players')),
        catchError(this.handleError<Player[]>('getPlayers', []))
      );
  }

  getPlayer(id: string): Observable<Player> {
    const url = `${this.playersUrl}/${id}`;
    return this.http.get<Player>(url).pipe(
      tap(_ => this.log(`fetched player id=${id}`)),
      catchError(this.handleError<Player>(`getHero id=${id}`))
    );
  }

  addPlayer(player: Player): Observable<Player> {
    return this.http.post<Player>(this.playersUrl, player, this.httpOptions)
      .pipe(tap((newPlayer: Player) => this.log(`added player ${newPlayer.name}`)),
        catchError(this.handleError<Player>('add player')));
  }

  deletePlayer(id: string): Observable<boolean> {
    const url = `${this.playersUrl}/${id}`;
    return this.http.delete<boolean>(url).pipe(
      tap((result: boolean) => this.log(`player ${id} deleted: ${result}`)),
      catchError(this.handleError<boolean>(`delete player id=${id}`))
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
