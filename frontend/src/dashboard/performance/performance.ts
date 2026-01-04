import { CommonModule } from '@angular/common';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { EChartsCoreOption } from 'echarts/core';
import {
  MonthlyInvestedResponse,
  PortfolioChartResponse,
  PortfolioControllerService,
} from '../../app/core/api';
import { PlatformService } from '../../app/core/platform.service';
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
  private platformService = inject(PlatformService);

  chartData = signal<PortfolioChartResponse | null>(null);
  monthlyInvested = signal<MonthlyInvestedResponse | null>(null);
  monthlyDividends = signal<any | null>(null);
  chartError = signal(false);
  monthlyError = signal(false);
  monthlyDividendsError = signal(false);
  chartOption = signal<EChartsCoreOption>({});
  monthlyOption = signal<EChartsCoreOption>({});
  monthlyDividendsOption = signal<EChartsCoreOption>({});
  selectedPeriod = signal<'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y'>('ALL');
  selectedMonths = signal<'6M' | '1Y' | '3Y' | 'ALL'>('ALL');
  selectedDividendsMonths = signal<'6M' | '1Y' | '3Y' | 'ALL'>('ALL');

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

    effect(() => {
      const monthly = this.monthlyInvested();
      if (monthly?.series && monthly.series.length > 0 && !this.monthlyError()) {
        const categories = monthly.series.map((item) => item.yearMonth ?? '');
        const values = monthly.series.map((item) => item.invested ?? 0);

        this.monthlyOption.set({
          xAxis: {
            type: 'category',
            data: categories,
          },
          yAxis: {
            type: 'value',
          },
          series: [
            {
              name: 'Invested',
              data: values,
              type: 'bar',
              itemStyle: {
                color: '#3B82F6',
              },
            },
          ],
          tooltip: {
            trigger: 'axis',
            formatter: (params: any) => {
              const item = params[0];
              const value = `€ ${Number(item.value).toFixed(2)}`;
              return `${item.axisValue}<br/>${item.marker} ${item.seriesName}: ${value}`;
            },
          },
          grid: {
            left: '3%',
            right: '4%',
            containLabel: true,
          },
        });
      }
    });

    effect(() => {
      const monthlyDivs = this.monthlyDividends();
      if (!this.monthlyDividendsError() && monthlyDivs) {
        // Normalize different possible response shapes
        let rawSeries: any[] | undefined;
        if (Array.isArray(monthlyDivs)) {
          rawSeries = monthlyDivs as any[];
        } else if (Array.isArray(monthlyDivs?.series)) {
          rawSeries = monthlyDivs.series as any[];
        } else if (typeof monthlyDivs === 'object') {
          // Possibly an object map { 'YYYY-MM': amount }
          rawSeries = Object.entries(monthlyDivs).map(([k, v]) => ({ yearMonth: k, dividends: Number(v) || 0 }));
        }

        if (rawSeries && rawSeries.length > 0) {
          const categories = rawSeries.map((item: any) => item.yearMonth ?? item.month ?? item.date ?? '');
          const values = rawSeries.map((item: any) => {
            const val = item.dividends ?? item.amount ?? item.value ?? item.invested ?? 0;
            return Number(val) || 0;
          });

          this.monthlyDividendsOption.set({
            xAxis: {
              type: 'category',
              data: categories,
            },
            yAxis: {
              type: 'value',
            },
            series: [
              {
                name: 'Dividends',
                data: values,
                type: 'bar',
                itemStyle: {
                  color: '#10B981',
                },
              },
            ],
            tooltip: {
              trigger: 'axis',
              formatter: (params: any) => {
                const item = params[0];
                const value = `€ ${Number(item.value).toFixed(2)}`;
                return `${item.axisValue}<br/>${item.marker} ${item.seriesName}: ${value}`;
              },
            },
            grid: {
              left: '3%',
              right: '4%',
              containLabel: true,
            },
          });
        }
      }
    });
    // reload when platform changes
    effect(() => {
      const p = this.platformService.platform();
      try { console.debug('[Performance] platform effect ->', p); } catch (e) {}
      const days = this.mapPeriodToDays(this.selectedPeriod());
      this.loadChartData(days);
    });
  }

  ngOnInit(): void {
    this.loadChartData();
  }

  loadChartData(days: number | undefined = undefined): void {
    this.chartError.set(false);
    this.chartData.set(null);
    this.loadPerformanceChart(days);
    this.loadMonthlyInvested(this.mapMonthsToNumber(this.selectedMonths()));
    this.loadMonthlyDividends(this.mapMonthsToNumber(this.selectedDividendsMonths()));
  }

  private loadPerformanceChart(days: number | undefined): void {
    this.portfolioControllerService.getPortfolioPerformanceChart(days, this.platformService.getPlatformValue()).subscribe({
      next: (data) => this.chartData.set(data ?? null),
      error: (err) => {
        this.chartError.set(true);
        this.toastService.error('Failed to load performance data', 'Error');
        console.error('Error loading performance data:', err);
      },
    });
  }

  private loadMonthlyInvested(months: number | undefined): void {
    this.monthlyError.set(false);
    this.monthlyInvested.set(null);

    this.portfolioControllerService.getMonthlyInvested(months, this.platformService.getPlatformValue()).subscribe({
      next: (data) => this.monthlyInvested.set(data ?? null),
      error: (err) => {
        this.monthlyError.set(true);
        this.toastService.error('Failed to load monthly investments', 'Error');
        console.error('Error loading monthly investments:', err);
      },
    });
  }

  private loadMonthlyDividends(months: number | undefined): void {
    this.monthlyDividendsError.set(false);
    this.monthlyDividends.set(null);

    this.portfolioControllerService.getMonthlyDividends(months, this.platformService.getPlatformValue()).subscribe({
      next: (data) => this.monthlyDividends.set(data ?? null),
      error: (err) => {
        this.monthlyDividendsError.set(true);
        this.toastService.error('Failed to load monthly dividends', 'Error');
        console.error('Error loading monthly dividends:', err);
      },
    });
  }

  setPeriod(period: 'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y') {
    this.selectedPeriod.set(period);
    const days = this.mapPeriodToDays(period);
    this.loadChartData(days);
  }

  setMonthsPeriod(period: '6M' | '1Y' | '3Y' | 'ALL') {
    this.selectedMonths.set(period);
    const months = this.mapMonthsToNumber(period);
    this.loadMonthlyInvested(months);
  }

  setDividendsMonthsPeriod(period: '6M' | '1Y' | '3Y' | 'ALL') {
    this.selectedDividendsMonths.set(period);
    const months = this.mapMonthsToNumber(period);
    this.loadMonthlyDividends(months);
  }

  private mapPeriodToDays(period: 'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y'): number | undefined {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const startOfYear = new Date(today.getFullYear(), 0, 1);

    switch (period) {
      case 'ALL':
        return undefined; // backend returns all by default
      case 'MTD': {
        const diff = Math.ceil((today.getTime() - startOfMonth.getTime()) / (1000 * 60 * 60 * 24));
        return Math.max(diff, 1);
      }
      case 'YTD': {
        const diff = Math.ceil((today.getTime() - startOfYear.getTime()) / (1000 * 60 * 60 * 24));
        return Math.max(diff, 1);
      }
      case '1M':
        return 30;
      case '3M':
        return 90;
      case '6M':
        return 180;
      case '1Y':
        return 365;
      default:
        return undefined;
    }
  }

  private mapMonthsToNumber(period: '6M' | '1Y' | '3Y' | 'ALL'): number | undefined {
    switch (period) {
      case '6M':
        return 6;
      case '1Y':
        return 12;
      case '3Y':
        return 36;
      case 'ALL':
      default:
        return undefined;
    }
  }
}
