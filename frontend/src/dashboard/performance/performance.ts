import { CommonModule } from '@angular/common';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { EChartsCoreOption } from 'echarts/core';
import { PortfolioChartResponse, PortfolioControllerService } from '../../app/core/api';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-performance',
  standalone: true,
  templateUrl: './performance.html',
  imports: [CommonModule, NgxEchartsDirective],
})
export class Performance implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);

  chartData = signal<PortfolioChartResponse | null>(null);
  chartError = signal(false);
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
            formatter: (params: any) => {
              let result = `${params[0].axisValue}<br/>`;
              params.forEach((item: any) => {
                const value = `€ ${Number(item.value).toFixed(2)}`;
                result += `${item.marker} ${item.seriesName}: ${value}<br/>`;
              });
              return result;
            },
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
      }
    });
  }

  ngOnInit(): void {
    this.loadChartData();
  }

  loadChartData(days: number | undefined = undefined): void {
    this.chartError.set(false);
    this.portfolioControllerService.getPortfolioPerformanceChart(days).subscribe({
      next: (data) => this.chartData.set(data),
      error: (err) => {
        this.chartError.set(true);
        this.toastService.error('Failed to load performance data', 'Error');
        console.error('Error loading performance data:', err);
      },
    });
  }
}