import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { PortfolioChartResponse, PortfolioControllerService, PortfolioSummaryResponse } from '../../app/core/api';
import { CommonModule } from '@angular/common';
import { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-overview',
  standalone: true,
  templateUrl: './overview.html',
  imports: [CommonModule, NgxEchartsDirective],
})
export class Overview implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);

  summary = signal<PortfolioSummaryResponse | null>(null);
  summaryError = signal(false);
  chartData = signal<PortfolioChartResponse | null>(null);
  chartError = signal(false);
  selectedPeriod = signal<'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y'>('1Y');
  
  chartOption = signal<EChartsCoreOption>({});

  constructor() {
    effect(() => {
      const data = this.chartData();
      if (data && !this.chartError()) {
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
              name: 'P/L %',
              data: data.plPercentage,
              type: 'line',
              smooth: true,
            },
          ],
          tooltip: {
            trigger: 'axis',
            formatter: (params: any) => {
              const value = params[0].value.toFixed(2);
              return `${params[0].axisValue}<br/>${params[0].marker} P/L %: ${value} %`;
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            containLabel: true,
          },
        });
      }
    });
  }

  ngOnInit(): void {
    this.setPeriod('1Y');
    this.loadSummary();
  }

  setPeriod(period: 'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y'): void {
    this.selectedPeriod.set(period);
    const today = new Date();
    let days = 30;
    switch (period) {
      case 'ALL':
        this.loadChartData(undefined);
        return;
      case 'MTD': {
        const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
        days = Math.max(1, Math.ceil((today.getTime() - startOfMonth.getTime()) / (1000 * 60 * 60 * 24)));
        break;
      }
      case 'YTD': {
        const startOfYear = new Date(today.getFullYear(), 0, 1);
        days = Math.max(1, Math.ceil((today.getTime() - startOfYear.getTime()) / (1000 * 60 * 60 * 24)));
        break;
      }
      case '1M':
        days = 30;
        break;
      case '3M':
        days = 90;
        break;
      case '6M':
        days = 180;
        break;
      case '1Y':
        days = 365;
        break;
    }
    this.loadChartData(days);
  }

  private loadSummary(): void {
    this.summaryError.set(false);
    this.portfolioControllerService.getPortfolioSummary().subscribe({
      next: (data) => this.summary.set(data),
      error: (err) => {
        this.summaryError.set(true);
        this.toastService.error('Failed to load portfolio summary. Please try again.', 'Error');
        console.error('Error loading summary:', err);
      },
    });
  }

  private loadChartData(days: number | undefined): void {
    this.chartError.set(false);
    this.portfolioControllerService.getPortfolioPerformanceChart(days).subscribe({
      next: (data) => this.chartData.set(data),
      error: (err) => {
        this.chartError.set(true);
        this.toastService.error('Failed to load chart data', 'Error');
        console.error('Error loading chart:', err);
      },
    });
  }
}