import { CommonModule } from '@angular/common';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { PortfolioChartResponse, PortfolioControllerService, PortfolioSummaryResponse } from '../app/core/api';
import { ToastService } from '../shared/toast.service';
import { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Overview } from './overview/overview';
import { Allocation } from "./allocation/allocation";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgxEchartsDirective, Overview, Allocation],
  templateUrl: './dashboard.html',
})
export class DashboardPage implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);

  summary = signal<PortfolioSummaryResponse | null>(null);
  chartData = signal<PortfolioChartResponse | null>(null);
  isLoading = signal(true);
  activeTab = signal<string>('overview');
  chartOption = signal<EChartsCoreOption>({});

  constructor() {
    effect(() => {
      const data = this.chartData();
      if (data) {
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
            }
          ],
          tooltip: {
            trigger: 'axis',
            formatter: (params: any) => {
              let result = `${params[0].axisValue}<br/>`;
              params.forEach((item: any) => {
                const value = item.seriesName === 'P/L %' 
                  ? `${item.value.toFixed(2)} %`
                  : `€ ${item.value.toFixed(2)}`;
                result += `${item.marker} ${item.seriesName}: ${value}<br/>`;
              });
              return result;
            }
          },
          legend: {
            data: ['Market Value', 'Invested', 'P/L %'],
            left: 'center',
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
    this.load();
    this.loadChartData();
  }

  loadChartData(): void {
    this.portfolioControllerService.getPortfolioPerformanceChart(365).subscribe({
      next: (data) => {
        this.chartData.set(data);
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
