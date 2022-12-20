import { Component, OnInit } from '@angular/core';
import { MessageService } from '../message.service';
import { EventService } from '../event.service';
import { GosEvent } from '../model/event';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  events: GosEvent[] = [];

  constructor(private eventService: EventService, private messageService: MessageService) { }

  ngOnInit(): void {
  }

  getEvents(): void {
    this.eventService.getEvents()
      .subscribe(events => this.events = events);
  }

}
