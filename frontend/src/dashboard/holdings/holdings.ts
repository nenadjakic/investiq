import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AssetHoldingResponse, CompanyAssetHoldingResponse, PortfolioControllerService } from '../../app/core/api';
import { PlatformService } from '../../app/core/platform.service';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-holdings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './holdings.html',
})
export class Holdings implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private platformService = inject(PlatformService);
  private toastService = inject(ToastService);

  holdings = signal<AssetHoldingResponse[]>([]);
  loading = signal<boolean>(false);
  loadError = signal<boolean>(false);
  searchTerm = signal<string>('');
  sortState = signal<{ column: SortColumn; direction: SortDirection }>({
    column: 'portfolioPercentage',
    direction: 'desc',
  });

  titleLabel = signal<string>('Current holdings — by asset');
  assetsCount = computed(() => this.filteredHoldings().length);
  collapsed = signal<boolean>(false);

  // Consolidated holdings (by company/ETF)
  consolidatedHoldings = signal<CompanyAssetHoldingResponse[]>([]);
  consolidatedLoading = signal<boolean>(false);
  consolidatedLoadError = signal<boolean>(false);
  consolidatedCollapsed = signal<boolean>(false);

  // Filtered and sorted consolidated list
  filteredConsolidatedHoldings = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) {
      return this.consolidatedHoldings();
    }
    return this.consolidatedHoldings().filter((c) => {
      const name = (c.name ?? '').toLowerCase();
      const tickers = (c.tickers ?? []).join(' ').toLowerCase();
      return name.includes(term) || tickers.includes(term);
    });
  });
  consolidatedCount = computed(() => this.filteredConsolidatedHoldings().length);

  // Consolidated sort state
  consolidatedSortState = signal<{ column: ConsolidatedSortColumn; direction: SortDirection }>({
    column: 'portfolioPercentage',
    direction: 'desc',
  });

  sortedConsolidatedHoldings = computed(() => {
    const { column, direction } = this.consolidatedSortState();
    const dir = direction === 'asc' ? 1 : -1;
    return [...this.filteredConsolidatedHoldings()].sort((a, b) => {
      switch (column) {
        case 'name':
          return (a.name ?? '').localeCompare(b.name ?? '') * dir;
        case 'tickers':
          return ((a.tickers ?? []).join(', ') ?? '').localeCompare((b.tickers ?? []).join(', ') ?? '') * dir;
        case 'profitLoss':
          return (this.toNumber(a.profitLoss) - this.toNumber(b.profitLoss)) * dir;
        case 'profitLossPercentage':
          return (this.toNumber(a.profitLossPercentage) - this.toNumber(b.profitLossPercentage)) * dir;
        case 'dividendCostYield':
          return (this.toNumber(a.dividendCostYield) - this.toNumber(b.dividendCostYield)) * dir;
        case 'portfolioPercentage':
          return (this.toNumber(a.portfolioPercentage) - this.toNumber(b.portfolioPercentage)) * dir;
        default:
          return 0;
      }
    });
  });

  toggleCollapsed(): void {
    this.collapsed.update((c) => !c);
  }

  toggleConsolidatedCollapsed(): void {
    this.consolidatedCollapsed.update((c) => !c);
  }

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

  constructor() {
    effect(() => {
      this.loadHoldings();
      this.loadConsolidatedHoldings();
    });
  }

  ngOnInit(): void {
    this.loadHoldings();
    this.loadConsolidatedHoldings();
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
      .getHoldings(this.platformService.getPlatformValue())
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

  private loadConsolidatedHoldings(): void {
    this.consolidatedLoading.set(true);
    this.consolidatedLoadError.set(false);
    this.portfolioControllerService
      .getConsolidatedHoldings(this.platformService.getPlatformValue())
      .pipe(finalize(() => this.consolidatedLoading.set(false)))
      .subscribe({
        next: (data) => {
          const items = Array.isArray(data as any) ? (data as CompanyAssetHoldingResponse[]) : [];
          this.consolidatedHoldings.set(items);
        },
        error: (err) => {
          this.consolidatedLoadError.set(true);
          this.toastService.error('Failed to load consolidated holdings', 'Error');
          console.error('Error loading consolidated holdings:', err);
        },
      });
  }

  toggleConsolidatedSort(column: ConsolidatedSortColumn): void {
    const current = this.consolidatedSortState();
    const nextDirection = current.column === column && current.direction === 'asc' ? 'desc' : 'asc';
    this.consolidatedSortState.set({ column, direction: nextDirection });
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

type ConsolidatedSortColumn =
  | 'name'
  | 'tickers'
  | 'profitLoss'
  | 'profitLossPercentage'
  | 'dividendCostYield'
  | 'portfolioPercentage';

