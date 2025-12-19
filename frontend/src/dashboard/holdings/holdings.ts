import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AssetHoldingResponse, PortfolioControllerService } from '../../app/core/api';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-holdings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './holdings.html',
})
export class Holdings implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);

  holdings = signal<AssetHoldingResponse[]>([]);
  loading = signal<boolean>(false);
  loadError = signal<boolean>(false);
  searchTerm = signal<string>('');
  sortState = signal<{ column: SortColumn; direction: SortDirection }>({
    column: 'portfolioPercentage',
    direction: 'desc',
  });

  filteredHoldings = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) {
      return this.holdings();
    }
    return this.holdings().filter((h) => {
      const ticker = (h.ticker ?? '').toLowerCase();
      const name = (h.name ?? '').toLowerCase();
      return ticker.includes(term) || name.includes(term);
    });
  });

  sortedHoldings = computed(() => {
    const { column, direction } = this.sortState();
    const dir = direction === 'asc' ? 1 : -1;
    return [...this.filteredHoldings()].sort((a, b) => {
      switch (column) {
        case 'ticker':
          return (a.ticker ?? '').localeCompare(b.ticker ?? '') * dir;
        case 'shares':
          return (this.toNumber(a.shares) - this.toNumber(b.shares)) * dir;
        case 'avgPrice':
          return (this.toNumber(a.avgPrice) - this.toNumber(b.avgPrice)) * dir;
        case 'currentPrice':
          return (this.toNumber(a.currentPrice) - this.toNumber(b.currentPrice)) * dir;
        case 'profitLoss':
          return (this.toNumber(a.profitLoss) - this.toNumber(b.profitLoss)) * dir;
        case 'dividendCostYield':
          return (this.toNumber(a.dividendCostYield) - this.toNumber(b.dividendCostYield)) * dir;
        case 'portfolioPercentage':
          return (
            this.toNumber(a.portfolioPercentage) - this.toNumber(b.portfolioPercentage)
          ) * dir;
        default:
          return 0;
      }
    });
  });

  ngOnInit(): void {
    this.loadHoldings();
  }

  onSearch(term: string): void {
    this.searchTerm.set(term);
  }

  toggleSort(column: SortColumn): void {
    const current = this.sortState();
    const nextDirection =
      current.column === column && current.direction === 'asc' ? 'desc' : 'asc';
    this.sortState.set({ column, direction: nextDirection });
  }

  retry(): void {
    this.loadHoldings();
  }

  private loadHoldings(): void {
    this.loading.set(true);
    this.loadError.set(false);
    this.portfolioControllerService
      .getHoldings()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (data) => {
          const items = Array.isArray(data as any) ? (data as AssetHoldingResponse[]) : [];
          this.holdings.set(items);
        },
        error: (err) => {
          this.loadError.set(true);
          this.toastService.error('Failed to load holdings', 'Error');
          console.error('Error loading holdings:', err);
        },
      });
  }

  private toNumber(value: number | undefined): number {
    return Number.isFinite(value) ? (value as number) : 0;
  }
}

type SortColumn =
  | 'ticker'
  | 'shares'
  | 'avgPrice'
  | 'currentPrice'
  | 'profitLoss'
  | 'dividendCostYield'
  | 'portfolioPercentage';

type SortDirection = 'asc' | 'desc';
