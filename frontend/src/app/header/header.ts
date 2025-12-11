import { CommonModule } from '@angular/common';
import { Component, inject, Input, signal, Signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
})
export class HeaderComponent {
  @Input() title!: Signal<string>;
  private router = inject(Router);

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

  isRouteActive(route: string): boolean {
    return this.router.url === route;
  }
}
