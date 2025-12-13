import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Overview } from './overview/overview';
import { Allocation } from './allocation/allocation';
import { Performance } from './performance/performance';
import { Holdings } from "./holdings/holdings";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, Overview, Allocation, Performance, Holdings],
  templateUrl: './dashboard.html',
})
export class DashboardPage {
  activeTab = signal<string>('overview');
  ngOnInit(): void {}
}
