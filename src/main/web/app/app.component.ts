import {Component, OnInit} from '@angular/core';
import {MenuItem, PrimeNGConfig} from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Битва';
  items: MenuItem[];
  activeItem: MenuItem;

  constructor(private primengConfig: PrimeNGConfig) {
    this.items = [
      {label: 'Сегодня', icon: 'pi pi-fw pi-chart-bar', routerLink: ['/today']},
      {label: 'Неделя', icon: 'pi pi-fw pi-calendar', routerLink: ['/week']},
      {label: 'Игроки', icon: 'pi pi-fw pi-users', routerLink: ['/players']},
      {label: 'Человеки', icon: 'pi pi-fw pi-users', routerLink: ['/persons']},
      {label: 'Ивенты', icon: 'pi pi-fw pi-users', routerLink: ['/events']}
    ];
    this.activeItem = this.items[0];
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;

  }
}
