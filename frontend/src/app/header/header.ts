import { CommonModule } from '@angular/common';
import { Component, Input, signal, Signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
})
export class HeaderComponent {
  @Input() title!: Signal<string>;

  navItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Transactions', route: '/transactions' },
    { label: 'Assets', route: '/assets' },
    { label: 'Basic data', route: '/basicdata' },
  ];

  activeTab = signal('dashboard');

  selectTab(tabId: string) {
    this.activeTab.set(tabId);
  }
}
