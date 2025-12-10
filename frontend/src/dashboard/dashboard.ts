import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
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

  summary = signal<PortfolioSummaryResponse | null>(null);
  isLoading = signal(true);

  ngOnInit(): void {
    this.load();
    this.loadChartData();
  }

  chartOption = signal<EChartsCoreOption>({});

  loadChartData(): void {
    this.portfolioControllerService.getPortfolioPerformanceChart(365).subscribe({
      next: (data) => {
        this.chartOption.set({
          xAxis: {
            type: 'category',
            data: data.dates,
          },
          yAxis: {
            type: 'value',
          },
          series: [
            {
              name: 'Market Value',
              data: data.marketValue,
              type: 'line',
              smooth: true,
            },
            {
              name: 'Invested',
              data: data.invested,
              type: 'line',
              smooth: true,
            },
          ],
          tooltip: {
            trigger: 'axis',
          },
          legend: {
            data: ['Market Value', 'Invested'],
            left: 'center',
          },
          grid: {
            left: '3%',
            right: '4%',
            containLabel: true,
          },
        });
      },
      error: (err) => {
        this.toastService.error('Failed to load chart data', 'Error');
        console.error('Error loading chart:', err);
      },
    });
  }

  load(): void {
    this.portfolioControllerService.getPortfolioSummary().subscribe({
      next: (data) => {
        this.summary.set(data);
      },
      error: (err) => {
        this.toastService.error('Failed to load portfolio summary. Please try again.', 'Error');
        console.error('Error loading summary:', err);
      },
      complete: () => {
        this.isLoading.set(false);
      },
    });
  }
}
