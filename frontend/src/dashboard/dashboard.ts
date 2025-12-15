import { CommonModule, NgComponentOutlet } from '@angular/common';
import { Component, signal, Type } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgComponentOutlet],
  templateUrl: './dashboard.html',
})
export class Dashboard {
  activeTab = signal<string>('overview');
  activeComponent = signal<Type<any> | null>(null);

  async ngOnInit(): Promise<void> {
    // Load initial component
    await this.loadComponent('overview');
  }

  async setActiveTab(tab: string): Promise<void> {
    this.activeTab.set(tab);
    await this.loadComponent(tab);
  }

  private async loadComponent(tab: string): Promise<void> {
    let component: Type<any> | null = null;
    
    switch (tab) {
      case 'overview':
        component = (await import('./overview/overview')).Overview;
        break;
      case 'allocation':
        component = (await import('./allocation/allocation')).Allocation;
        break;
      case 'performance':
        component = (await import('./performance/performance')).Performance;
        break;
      case 'holdings':
        component = (await import('./holdings/holdings')).Holdings;
        break;
      case 'insights':
        component = (await import('./insights/insights')).Insights;
        break;
      case 'details':
        component = (await import('./details/details')).Details;
        break;
    }

    this.activeComponent.set(component);
  }
}
