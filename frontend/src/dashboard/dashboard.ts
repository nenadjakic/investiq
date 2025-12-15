import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Overview } from './overview/overview';
import { Allocation } from './allocation/allocation';
import { Performance } from './performance/performance';
import { Holdings } from "./holdings/holdings";
import { Insights } from "./insights/insights";
import { Details } from "./details/details";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, Overview, Allocation, Performance, Holdings, Insights, Details],
  templateUrl: './dashboard.html',
})
export class Dashboard {
  activeTab = signal<string>('overview');
  ngOnInit(): void {}
}
