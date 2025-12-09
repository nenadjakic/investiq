import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { PortfolioControllerService, PortfolioSummaryResponse } from '../app/core/api';
import { ToastService } from '../shared/toast.service';
import { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgxEchartsDirective],
  templateUrl: './dashboard.html',
})
export class DashboardPage implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  summary: PortfolioSummaryResponse | null = null;
  isLoading = true;

  ngOnInit(): void {
    this.load();
  }

  chartOption: EChartsCoreOption = {
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      type: 'line',
    },
  ],
};

  load(): void {
    this.portfolioControllerService.getPortfolioSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toastService.error(
          'Failed to load portfolio summary. Please try again.',
          'Error'
        );        
        console.error('Error loading summary:', err);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }
}
