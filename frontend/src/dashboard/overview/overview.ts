import { Component, effect, input, signal } from '@angular/core';
import { PortfolioChartResponse, PortfolioSummaryResponse } from '../../app/core/api';
import { CommonModule } from '@angular/common';
import { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';

@Component({
  selector: 'app-overview',
  standalone: true,
  templateUrl: './overview.html',
  imports: [CommonModule, NgxEchartsDirective],
})
export class Overview {
  summary = input<PortfolioSummaryResponse | null>();
  chartData = input<PortfolioChartResponse | null>();
  
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
}