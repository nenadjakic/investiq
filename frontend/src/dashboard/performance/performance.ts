import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { EChartsCoreOption } from 'echarts/core';
import {
  MonthlyHoldingEntry,
  MonthlyInvestedResponse,
  MonthlyPlResponse,
  PortfolioChartResponse,
  PortfolioControllerService, PortfolioHoldingMonthlyResponse,
} from '../../app/core/api';
import { PlatformService } from '../../app/core/platform.service';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-performance',
  standalone: true,
  templateUrl: './performance.html',
  imports: [CommonModule, FormsModule, NgxEchartsDirective],
})
export class Performance implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);
  private platformService = inject(PlatformService);

  chartData = signal<PortfolioChartResponse | null>(null);
  monthlyInvested = signal<MonthlyInvestedResponse | null>(null);
  monthlyDividends = signal<any | null>(null);
  monthlyPl = signal<MonthlyPlResponse | null>(null);
  chartError = signal(false);
  monthlyError = signal(false);
  monthlyDividendsError = signal(false);
  monthlyPlError = signal(false);
  chartOption = signal<EChartsCoreOption>({});
  monthlyOption = signal<EChartsCoreOption>({});
  monthlyDividendsOption = signal<EChartsCoreOption>({});
  monthlyPlOption = signal<EChartsCoreOption>({});
  selectedPeriod = signal<'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y'>('ALL');
  selectedMonths = signal<'6M' | '1Y' | '3Y' | 'ALL'>('ALL');
  selectedDividendsMonths = signal<'6M' | '1Y' | '3Y' | 'ALL'>('ALL');
  selectedYear = signal<string>('');
  availableYears = signal<string[]>([]);
  hoveredYear = signal<string | null>(null);
  topHoldingsByMonth = signal<PortfolioHoldingMonthlyResponse[] | null>(null);
  topHoldingsByMonthError = signal(false);
  topHoldingsOption = signal<EChartsCoreOption>({});
  private topHoldingsChart: any = null;

  onTopHoldingsChartInit(chart: any): void {
    this.topHoldingsChart = chart;
  }

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
          rawSeries = Object.entries(monthlyDivs).map(([k, v]) => ({
            yearMonth: k,
            dividends: Number(v) || 0,
          }));
        }

        if (rawSeries && rawSeries.length > 0) {
          const categories = rawSeries.map(
            (item: any) => item.yearMonth ?? item.month ?? item.date ?? '',
          );
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

    effect(() => {
      const monthlyPlData = this.monthlyPl();
      if (!this.monthlyPlError() && monthlyPlData?.series) {
        const years = Object.keys(monthlyPlData.series).sort();
        this.availableYears.set(years);

        if (!this.selectedYear() && years.length > 0) {
          const currentYear = new Date().getFullYear().toString();
          const defaultYear = years.includes(currentYear) ? currentYear : years[years.length - 1];
          this.selectedYear.set(defaultYear);
        }
      }
    });

    effect(() => {
      const monthlyPlData = this.monthlyPl();
      const selectedYr = this.selectedYear();
      const hovered = this.hoveredYear();

      if (!this.monthlyPlError() && monthlyPlData?.series && selectedYr) {
        const monthNames = [
          'Jan',
          'Feb',
          'Mar',
          'Apr',
          'May',
          'Jun',
          'Jul',
          'Aug',
          'Sep',
          'Oct',
          'Nov',
          'Dec',
        ];

        const filledMonths: (number | null)[] = new Array(12).fill(null);
        const monthData = monthlyPlData.series[selectedYr] ?? [];
        monthData.forEach((item: any) => {
          const idx = (Number(item.month) || 1) - 1;
          if (idx >= 0 && idx < 12) filledMonths[idx] = item.plPercent ?? 0;
        });

        this.monthlyPlOption.set({
          xAxis: { type: 'category', data: monthNames },
          yAxis: { type: 'value' },
          series: [
            {
              name: 'Monthly P/L %',
              data: filledMonths.map((val) => ({
                value: val,
                itemStyle: {
                  color: val === null ? '#E5E7EB' : val >= 0 ? '#10B981' : '#EF4444',
                  opacity: hovered && hovered !== selectedYr ? 0.3 : 1,
                },
              })),
              type: 'bar',
            },
          ],
          tooltip: {
            trigger: 'axis',
            formatter: (params: any) => {
              const item = params[0];
              if (item.value === null || item.value === undefined) return `${item.axisValue}: N/A`;
              return `${item.axisValue}<br/>${item.marker} ${item.seriesName}: ${Number(item.value).toFixed(2)}%`;
            },
          },
          grid: { left: '3%', right: '4%', containLabel: true },
        });
      }
    });

    // reload when platform changes
    effect(() => {
      const p = this.platformService.platform();
      try {
        console.debug('[Performance] platform effect ->', p);
      } catch (e) {}
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
    this.loadMonthlyPl();
    this.loadTopHoldingsByMonth();
  }

  private loadPerformanceChart(days: number | undefined): void {
    this.portfolioControllerService
      .getPortfolioPerformanceChart(days, this.platformService.getPlatformValue())
      .subscribe({
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

    this.portfolioControllerService
      .getMonthlyInvested(months, this.platformService.getPlatformValue())
      .subscribe({
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

    this.portfolioControllerService
      .getMonthlyDividends(months, this.platformService.getPlatformValue())
      .subscribe({
        next: (data) => this.monthlyDividends.set(data ?? null),
        error: (err) => {
          this.monthlyDividendsError.set(true);
          this.toastService.error('Failed to load monthly dividends', 'Error');
          console.error('Error loading monthly dividends:', err);
        },
      });
  }

  private loadMonthlyPl(): void {
    this.monthlyPlError.set(false);
    this.monthlyPl.set(null);
    this.selectedYear.set('');
    this.availableYears.set([]);

    this.portfolioControllerService
      .getMonthlyPl(this.platformService.getPlatformValue())
      .subscribe({
        next: (data) => this.monthlyPl.set(data ?? null),
        error: (err) => {
          this.monthlyPlError.set(true);
          this.toastService.error('Failed to load monthly P/L data', 'Error');
          console.error('Error loading monthly P/L data:', err);
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

  setYear(year: string) {
    this.selectedYear.set(year);
  }


  getMonthlyPlByYearTableData(): Array<{
    year: string;
    months: (number | null)[];
    total: number | null;
  }> {
    const monthlyPl = this.monthlyPl();
    if (!monthlyPl?.series) return [];

    const result = Object.keys(monthlyPl.series)
      .sort((a, b) => Number(b) - Number(a))
      .map((yearKey) => {
        const arr = monthlyPl.series?.[yearKey];
        const months: (number | null)[] = new Array(12).fill(null);

        if (Array.isArray(arr)) {
          arr.forEach((entry: any) => {
            const idx = (Number(entry.month) || 1) - 1;
            if (idx >= 0 && idx < 12) {
              months[idx] = Number(entry.plPercent ?? 0);
            }
          });
        }

        const valid = months.filter((v): v is number => v !== null);
        const total = valid.length ? valid.reduce((a, b) => a + b, 0) : null;

        return { year: yearKey, months, total };
      });

    return result;
  }

  private loadTopHoldingsByMonth(): void {
    this.topHoldingsByMonthError.set(false);
    this.topHoldingsByMonth.set(null);

    this.portfolioControllerService
      .getTopConsolidatedHoldingsByMonth(this.platformService.getPlatformValue(), 10)
      .subscribe({
        next: (data: PortfolioHoldingMonthlyResponse[]) => {
          this.topHoldingsByMonth.set(data ?? null);
          this.buildTopHoldingsChart(data);
        },
        error: (err) => {
          this.topHoldingsByMonthError.set(true);
          this.toastService.error('Failed to load top holdings data', 'Error');
          console.error('Error loading top holdings by month:', err);
        },
      });
  }

  private buildTopHoldingsChart(data: PortfolioHoldingMonthlyResponse[]): void {
    if (!data?.length) return;

    const updateFrequency = 1000;

    const allNames: string[] = [
      ...new Set(
        data.flatMap((d: PortfolioHoldingMonthlyResponse) =>
          d.holdings?.map((h: MonthlyHoldingEntry) => h.name ?? '') ?? []
        )
      ),
    ];

    const colorPalette = [
      '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
      '#06B6D4', '#F97316', '#84CC16', '#EC4899', '#6B7280',
    ];

    const maxValue = Math.ceil(
      Math.max(
        ...data.flatMap((d) =>
          d.holdings?.map((h) => Number(h.portfolioPercentage ?? 0)) ?? []
        )
      ) * 1.15
    );

    const colorMap: Record<string, string> = {};
    allNames.forEach((name, i) => {
      colorMap[name] = colorPalette[i % colorPalette.length];
    });

    const months: string[] = data.map((d) => d.yearMonth ?? '');

    const getDataForMonth = (yearMonth: string) => {
      const month = data.find((d) => d.yearMonth === yearMonth);
      return (month?.holdings ?? [])
        .map((h: MonthlyHoldingEntry) => ({
          name: h.name ?? '',
          value: Number(h.portfolioPercentage ?? 0),
        }))
        .sort((a, b) => a.value - b.value);
    };

    const initialData = getDataForMonth(months[0]);

    const option: any = {
      grid: { top: 10, bottom: 30, left: 160, right: 100 },
      xAxis: {
        max: maxValue,
        axisLabel: { formatter: (val: number) => `${val}%` },
      },
      yAxis: {
        type: 'category',
        animationDuration: 300,
        animationDurationUpdate: updateFrequency,
        data: initialData.map((d) => d.name),
      },
      series: [
        {
          realtimeSort: true,
          type: 'bar',
          data: initialData.map((d) => ({
            value: d.value,
            itemStyle: { color: colorMap[d.name] ?? '#5470c6' },
          })),
          label: {
            show: true,
            position: 'right',
            valueAnimation: true,
            formatter: (params: any) => `${Number(params.value).toFixed(1)}%`,
            fontFamily: 'monospace',
          },
        },
      ],
      animationDuration: 0,
      animationDurationUpdate: updateFrequency,
      animationEasing: 'linear',
      animationEasingUpdate: 'linear',
      graphic: {
        elements: [
          {
            type: 'text',
            right: 60,
            bottom: 60,
            style: {
              text: months[0],
              font: 'bolder 36px monospace',
              fill: 'rgba(100, 100, 100, 0.25)',
            },
            z: 100,
          },
        ],
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params: any) => {
          const item = params[0];
          return `${item.name}<br/>${item.marker} <strong>${Number(item.value).toFixed(2)}%</strong>`;
        },
      },
    };

    this.topHoldingsOption.set(option);

    months.forEach((ym, i) => {
      if (i === 0) return;
      setTimeout(() => {
        const monthData = getDataForMonth(ym);
        if (!this.topHoldingsChart) return;

        this.topHoldingsChart.setOption({
          xAxis: { max: maxValue },
          yAxis: { data: monthData.map((d) => d.name) },
          series: [
            {
              data: monthData.map((d) => ({
                value: d.value,
                itemStyle: { color: colorMap[d.name] ?? '#5470c6' },
              })),
            },
          ],
          graphic: {
            elements: [
              {
                type: 'text',
                right: 60,
                bottom: 60,
                style: {
                  text: ym,
                  font: 'bolder 36px monospace',
                  fill: 'rgba(100, 100, 100, 0.25)',
                },
                z: 100,
              },
            ],
          },
        });
      }, i * updateFrequency);
    });
  }

  private mapPeriodToDays(
    period: 'ALL' | 'MTD' | 'YTD' | '1M' | '3M' | '6M' | '1Y',
  ): number | undefined {
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
