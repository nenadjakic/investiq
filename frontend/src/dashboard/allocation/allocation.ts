import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import {
  IndustrySectorValueResponse,
  PortfolioChartResponse,
  PortfolioControllerService,
  PortfolioSummaryResponse,
} from '../../app/core/api';
import { CommonModule } from '@angular/common';
import { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';

@Component({
  selector: 'app-allocation',
  standalone: true,
  templateUrl: './allocation.html',
  imports: [CommonModule, NgxEchartsDirective],
})
export class Allocation implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  summary = input<PortfolioSummaryResponse | null>();
  chartData = input<PortfolioChartResponse | null>();

  sectorData = signal<IndustrySectorValueResponse[]>([]);
  sectorOption = signal<EChartsCoreOption>({});
  sectorIndustryOption = signal<EChartsCoreOption>({});

  constructor() {
    effect(() => {
      const data = this.sectorData();
      if (data && data.length > 0) {
        const groupedBySector = data.reduce((acc, item) => {
          const sector = item.sector || 'Unknown';
          if (!acc[sector]) {
            acc[sector] = [];
          }
          acc[sector].push(item);
          return acc;
        }, {} as Record<string, IndustrySectorValueResponse[]>);

        const sunburstData = Object.entries(groupedBySector).map(([sector, industries]) => ({
          name: sector,
          children: industries.map((industry) => ({
            name: industry.industry || 'Unknown',
            value: industry.valueEur || 0,
          })),
        }));

        const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
        const sectorTotals: Record<string, number> = Object.entries(groupedBySector).reduce(
          (acc, [sector, items]) => {
            acc[sector] = items.reduce((s, it) => s + (it.valueEur || 0), 0);
            return acc;
          },
          {} as Record<string, number>
        );

        const pieData = Object.entries(sectorTotals)
          .map(([name, value]) => ({ name, value }))
          .sort((a, b) => b.value - a.value);

        this.sectorOption.set({
          legend: {            
            orient: 'horizontal',
            bottom: 30,
            type: 'scroll'
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const pct = totalValue > 0 ? (val / totalValue) * 100 : 0;
              return `${params.name}<br/>€ ${val.toFixed(2)}<br/>${pct.toFixed(2)} %`;
            },
          },
          series: [
            {
              name: 'Sector Allocation',
              type: 'pie',
              radius: ['60%', '90%'],
              startAngle: 180,
              endAngle: 360,
              label: {
                show: false,
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: '{b}: {d}%',
                },
              },
              data: pieData,
            },
          ],
        });

        this.sectorIndustryOption.set({
          series: [
            {
              type: 'sunburst',
              data: sunburstData,
              radius: ['0', '90%'],
              center: ['50%', '45%'],
              label: {
                rotate: 'radial',
                minAngle: 20,
                fontSize: 10,
              },
            },
          ],

          tooltip: {
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const name = params.name as string;
              const totalPct = totalValue > 0 ? (val / totalValue) * 100 : 0;

              const path = params.treePathInfo as Array<{ name: string }> | undefined;
              const parentName = path && path.length >= 2 ? path[path.length - 2].name : undefined;
              const parentTotal = parentName ? sectorTotals[parentName] ?? 0 : 0;
              const sectorPct = parentTotal > 0 ? (val / parentTotal) * 100 : undefined;

              const lines: string[] = [];
              lines.push(`${name}`);
              lines.push(`€ ${val.toFixed(2)}`);
              lines.push(`Total: ${totalPct.toFixed(2)} %`);
              if (sectorPct !== undefined) {
                lines.push(`Of sector ${parentName}: ${sectorPct.toFixed(2)} %`);
              }
              return lines.join('<br/>');
            },
          },
        });
      }
    });
  }

  ngOnInit(): void {
    this.loadSectorAllocation();
  }

  loadSectorAllocation(): void {
    this.portfolioControllerService.getIndustrySectorAllocation().subscribe({
      next: (data) => {
        this.sectorData.set(Array.isArray(data) ? data : [data]);
      },
      error: (err) => {
        console.error('Error loading sector allocation:', err);
      },
    });
  }
}
