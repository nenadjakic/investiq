import { CommonModule, NgComponentOutlet } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, signal, Type, inject } from '@angular/core';
import { PlatformService, Platform } from '../app/core/platform.service';

type PlatformOption = { value: Platform; label: string };

@Component({
  selector: 'app-dashboard',
  standalone: true,

  imports: [CommonModule, NgComponentOutlet, FormsModule],
  templateUrl: './dashboard.html',
})
export class Dashboard {
  activeTab = signal<string>('overview');
  activeComponent = signal<Type<any> | null>(null);

  platforms: PlatformOption[] = [
    { value: 'TRADING212', label: 'Trading212' },
    { value: 'ETORO', label: 'eToro' },
    { value: 'IBKR', label: 'IBKR' },
    { value: 'REVOLUT', label: 'Revolut' },
  ];
  selectedPlatform = signal<PlatformOption | null>(null);

  get selectedPlatformModel(): PlatformOption | null {
    return this.selectedPlatform();
  }
  set selectedPlatformModel(v: PlatformOption | null) {
    this.selectedPlatform.set(v);
  }

  private platformService = inject(PlatformService);

  async ngOnInit(): Promise<void> {
    await this.loadComponent('overview');
  }

  async setActiveTab(tab: string): Promise<void> {
    this.activeTab.set(tab);
    await this.loadComponent(tab);
  }

  onPlatformChange(event: PlatformOption | null): void {
    this.selectedPlatformModel = event;
    this.platformService.setPlatform(event);
    console.log('Platform changed to', event);
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
      case 'analysis':
        component = (await import('./analysis/analysis')).Analysis;
        break;
      case 'details':
        component = (await import('./details/details')).Details;
        break;
    }

    this.activeComponent.set(component);
  }
}
