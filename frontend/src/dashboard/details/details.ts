import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { ActivePositionResponse, PortfolioControllerService } from '../../app/core/api';
import { PlatformService } from '../../app/core/platform.service';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './details.html',
})
export class Details implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);
  private platformService = inject(PlatformService);

  positions = signal<ActivePositionResponse[]>([]);
  loading = signal<boolean>(false);
  loadError = signal<boolean>(false);
  searchTerm = signal<string>('');
  sortState = signal<{ column: SortColumn; direction: SortDirection }>({
    column: 'marketValuePercentage',
    direction: 'desc',
  });

  filteredPositions = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) {
      return this.positions();
    }
    return this.positions().filter((p) => {
      const ticker = (p.ticker ?? '').toLowerCase();
      const name = (p.name ?? '').toLowerCase();
      return ticker.includes(term) || name.includes(term);
    });
  });

  sortedPositions = computed(() => {
    const { column, direction } = this.sortState();
    const dir = direction === 'asc' ? 1 : -1;
    return [...this.filteredPositions()].sort((a, b) => {
      switch (column) {
        case 'ticker':
          return (a.ticker ?? '').localeCompare(b.ticker ?? '') * dir;
        case 'investedEur':
          return (this.toNumber(a.investedEur) - this.toNumber(b.investedEur)) * dir;
        case 'investedPercentage':
          return (this.toNumber(a.investedPercentage) - this.toNumber(b.investedPercentage)) * dir;
        case 'unrealizedProfitLossEur':
          return (
            (this.toNumber(a.unrealizedProfitLossEur) - this.toNumber(b.unrealizedProfitLossEur)) *
            dir
          );
        case 'unrealizedProfitLossPercentage':
          return (
            (this.toNumber(a.unrealizedProfitLossPercentage) -
              this.toNumber(b.unrealizedProfitLossPercentage)) *
            dir
          );
        case 'marketValueEur':
          return (this.toNumber(a.marketValueEur) - this.toNumber(b.marketValueEur)) * dir;
        case 'marketValuePercentage':
          return (
            (this.toNumber(a.marketValuePercentage) - this.toNumber(b.marketValuePercentage)) * dir
          );
        case 'totalProfitLossEur':
          return (this.toNumber(a.totalProfitLossEur) - this.toNumber(b.totalProfitLossEur)) * dir;
        case 'delta':
          return (this.getDelta(a) - this.getDelta(b)) * dir;
        default:
          return 0;
      }
    });
  });

  totals = computed(() => {
    const list = this.filteredPositions();
    const sum = (selector: (p: ActivePositionResponse) => number | undefined) =>
      list.reduce((acc, item) => acc + this.toNumber(selector(item)), 0);

    const investedEur = sum((p) => p.investedEur);
    const investedPercentage = sum((p) => p.investedPercentage);
    const unrealizedProfitLossEur = sum((p) => p.unrealizedProfitLossEur);
    const marketValueEur = sum((p) => p.marketValueEur);
    const marketValuePercentage = sum((p) => p.marketValuePercentage);

    const unrealizedProfitLossPercentage =
      investedEur !== 0 ? ((marketValueEur - investedEur) / investedEur) * 100 : 0;

    const totalProfitLossEur = sum((p) => p.totalProfitLossEur);
    const delta = marketValuePercentage - investedPercentage;

    return {
      investedEur,
      investedPercentage,
      unrealizedProfitLossEur,
      unrealizedProfitLossPercentage,
      marketValueEur,
      marketValuePercentage,
      totalProfitLossEur,
      delta,
    };
  });

  constructor() {
    effect(() => {
      this.loadPositions();
    });
  }

  ngOnInit(): void {
    this.loadPositions();
  }

  onSearch(term: string): void {
    this.searchTerm.set(term);
  }

  toggleSort(column: SortColumn): void {
    const current = this.sortState();
    const nextDirection = current.column === column && current.direction === 'asc' ? 'desc' : 'asc';
    this.sortState.set({ column, direction: nextDirection });
  }

  retry(): void {
    this.loadPositions();
  }

  private loadPositions(): void {
    this.loading.set(true);
    this.loadError.set(false);
    this.portfolioControllerService
      .getActivePositions(this.platformService.getPlatformValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (data) => {
          const items = Array.isArray(data as any) ? (data as ActivePositionResponse[]) : [];
          this.positions.set(items);
        },
        error: (err) => {
          this.loadError.set(true);
          this.toastService.error('Failed to load active positions', 'Error');
          console.error('Error loading active positions:', err);
        },
      });
  }

  private toNumber(value: number | undefined): number {
    return Number.isFinite(value) ? (value as number) : 0;
  }

  getDelta(position: ActivePositionResponse): number {
    const invested = this.toNumber(position.investedPercentage);
    const market = this.toNumber(position.marketValuePercentage);
    return market - invested;
  }

  exportPositions(): void {
    const positions = this.sortedPositions();

    const deltaIndicator = (delta: number): string => {
      if (delta > 0) return '▲';
      if (delta < 0) return '▼';
      return '–';
    };

    const headers = [
      'Ticker',
      'Name',
      'Shares',
      'Avg Price (EUR)',
      'Invested (EUR)',
      'Invested (%)',
      'Unrealized P/L (EUR)',
      'Unrealized P/L (%)',
      'Market Value (EUR)',
      'Market Value (%)',
      'Total P/L (EUR)',
      'Δ vs Invested',
    ];

    const rows = positions.map((p) => [
      p.ticker ?? '',
      p.name ?? '',
      this.toNumber(p.shares),
      this.toNumber(p.avgPriceEur).toFixed(2),
      this.toNumber(p.investedEur).toFixed(2),
      this.toNumber(p.investedPercentage).toFixed(2),
      this.toNumber(p.unrealizedProfitLossEur).toFixed(2),
      this.toNumber(p.unrealizedProfitLossPercentage).toFixed(2),
      this.toNumber(p.marketValueEur).toFixed(2),
      this.toNumber(p.marketValuePercentage).toFixed(2),
      this.toNumber(p.totalProfitLossEur).toFixed(2),
      deltaIndicator(this.getDelta(p)),
    ]);

    const t = this.totals();
    const totalsRow = [
      'TOTAL',
      '',
      '',
      '',
      t.investedEur.toFixed(2),
      '',
      t.unrealizedProfitLossEur.toFixed(2),
      t.unrealizedProfitLossPercentage.toFixed(2),
      t.marketValueEur.toFixed(2),
      '',
      t.totalProfitLossEur.toFixed(2),
      deltaIndicator(t.delta),
    ];

    const escape = (val: string) => `"${String(val).replace(/"/g, '""')}"`;

    const csv = [
      headers.map(escape).join(','),
      ...rows.map((r) => r.map((v) => escape(String(v))).join(',')),
      //totalsRow.map((v) => escape(String(v))).join(','),
    ].join('\n');

    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `details_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }
}

type SortColumn =
  | 'ticker'
  | 'shares'
  | 'avgPriceEur'
  | 'investedEur'
  | 'investedPercentage'
  | 'unrealizedProfitLossEur'
  | 'unrealizedProfitLossPercentage'
  | 'marketValueEur'
  | 'marketValuePercentage'
  | 'totalProfitLossEur'
  | 'delta';

type SortDirection = 'asc' | 'desc';
