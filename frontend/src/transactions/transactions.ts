import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, computed, ViewChild } from '@angular/core';
import { PageTransactionResponse, TransactionControllerService } from '../app/core/api';
import { ToastService } from '../shared/toast.service';
import { AddTransactionModalComponent } from './add-transaction-modal';
import { Platform } from '../app/core/platform.service';
import { FormsModule } from '@angular/forms';

interface TransactionFilters {
  platform: Platform | null;
  transactionType:
    | 'BUY'
    | 'FEE'
    | 'SELL'
    | 'DEPOSIT'
    | 'WITHDRAWAL'
    | 'DIVIDEND'
    | 'DIVIDEND_ADJUSTMENT'
    | 'UNKNOWN'
    | null;
  assetType: 'STOCK' | 'ETF' | 'INDEX' | null;
  assetSymbol: string | null;
  currency: string | null;
  dateFrom: string | null;
  dateTo: string | null;
}

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.html',
  standalone: true,
  imports: [CommonModule, FormsModule, AddTransactionModalComponent],
})
export class Transactions implements OnInit {
  private transactionControllerService = inject(TransactionControllerService);
  private toast = inject(ToastService);

  @ViewChild(AddTransactionModalComponent) addTransactionModal!: AddTransactionModalComponent;

  // Data
  pageData = signal<PageTransactionResponse>({});
  currentPage = signal(0);
  pageSize = 25;
  loading = signal(false);
  error = signal<string | null>(null);

  activeFilterCount = computed(
    () => Object.values(this.filters()).filter((v) => v !== null && v !== '').length,
  );

  onFilterChange(key: string, value: string | null) {
    this.filters.update((f) => ({ ...f, [key]: value || null }));
    this.loadTransactions(0);
  }
  
  onDateFilterChange(key: 'dateFrom' | 'dateTo', value: string) {
    if (!value) {
      this.onFilterChange(key, null);
      return;
    }
    // yyyy-MM-dd -> OffsetDateTime (početak dana za dateFrom, kraj dana za dateTo)
    const iso = key === 'dateFrom' ? `${value}T00:00:00+00:00` : `${value}T23:59:59+00:00`;
    this.onFilterChange(key, iso);
  }

  filters = signal<TransactionFilters>({
    platform: null,
    transactionType: null,
    assetType: null,
    assetSymbol: null,
    currency: null,
    dateFrom: null,
    dateTo: null,
  });

  // Computed signals
  paginatedTransactions = computed(() => this.pageData().content || []);
  totalPages = computed(() => this.pageData().totalPages || 0);
  totalElements = computed(() => this.pageData().totalElements || 0);
  pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    if (total === 0) {
      return [] as number[];
    }
    const window = 2; // show current ±2
    const start = Math.max(0, current - window);
    const end = Math.min(total - 1, current + window);
    const pages: number[] = [];
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  });
  hasLeadingGap = computed(() => {
    const pages = this.pageNumbers();
    return pages.length > 0 && pages[0] > 0;
  });
  hasTrailingGap = computed(() => {
    const pages = this.pageNumbers();
    const last = pages[pages.length - 1];
    return pages.length > 0 && last < this.totalPages() - 1;
  });

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions(page: number = 0) {
    this.loading.set(true);
    this.error.set(null);
    this.currentPage.set(page);

    const f = this.filters();

    this.transactionControllerService
      .findAllTransactions(
        f.platform ?? undefined,
        f.transactionType ?? undefined,
        f.assetType ?? undefined,
        f.assetSymbol ?? undefined,
        f.currency ?? undefined,
        f.dateFrom ?? undefined,
        f.dateTo ?? undefined,
        page,
        this.pageSize,
      )
      .subscribe({
        next: (response) => {
          this.pageData.set(response);
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set('Failed to load transactions');
          this.toast.error('Failed to load transactions', 'Error');
          this.loading.set(false);
          console.error(err);
        },
      });
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.loadTransactions(page);
    }
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      this.goToPage(this.currentPage() + 1);
    }
  }

  previousPage() {
    if (this.currentPage() > 0) {
      this.goToPage(this.currentPage() - 1);
    }
  }

  firstPage() {
    if (this.totalPages() > 0 && this.currentPage() !== 0) {
      this.goToPage(0);
    }
  }

  lastPage() {
    const last = this.totalPages() - 1;
    if (last >= 0 && this.currentPage() !== last) {
      this.goToPage(last);
    }
  }

  openAddModal() {
    this.addTransactionModal.openModal();
  }

  closeAddModal() {
    this.addTransactionModal.closeModal();
  }

  onTransactionCreated() {
    this.loadTransactions(0);
  }

  onSearchChange() {
    this.currentPage.set(1);
    this.loadTransactions();
  }
}
