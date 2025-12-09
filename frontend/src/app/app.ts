import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header';
import { ToastComponent } from '../shared/toast/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('investiq');
}
