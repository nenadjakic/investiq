import { CommonModule } from "@angular/common";
import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { FormsModule } from "@angular/forms";
import {
  PageStagingTransactionResponse,
  StagingTransactionControllerService,
  StagingTransactionResponse,
  AssetControllerService,
  AssetResponse,
} from "../app/core/api";
import { ToastService } from "../shared/toast.service";

@Component({
  selector: 'app-staging-transactions',
  templateUrl: './staging-transactions.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})
export class StagingTransactions implements OnInit {
  private stagingTransactionControllerService = inject(StagingTransactionControllerService);
  private assetControllerService = inject(AssetControllerService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  // Data
  pageData = signal<PageStagingTransactionResponse>({});
  currentPage = signal(0);
  pageSize = 25;
  loading = signal(false);
  error = signal<string | null>(null);

  // Filters
  platformFilter = signal<'TRADING212' | 'ETORO' | 'IBKR' | 'REVOLUT' | ''>('');
  statusFilter = signal<'PENDING' | 'VALIDATED' | 'FAILED' | 'IMPORTED' | ''>('');

  // Detail panel state
  selectedId = signal<string | null>(null);
  selectedDetail = signal<StagingTransactionResponse | null>(null);
  detailLoading = signal(false);
  detailError = signal<string | null>(null);
  updateLoading = signal(false);
  updateError = signal<string | null>(null);

  // Bulk selection
  selectedIds = signal<Set<string>>(new Set());
  bulkLoading = signal(false);

  // Asset search
  assets = signal<AssetResponse[]>([]);
  assetSearchQuery = signal<string>('');
  assetsLoading = signal(false);
  assetDropdownOpen = signal(false);

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
  selectedTransaction = computed(() => this.selectedDetail());
  bulkActionsVisible = computed(() => this.paginatedTransactions().length > 0);
  filteredAssets = computed(() => {
    const query = this.assetSearchQuery().toLowerCase();
    if (!query) return this.assets();
    return this.assets().filter(a => 
      a.symbol?.toLowerCase().includes(query) || 
      a.company?.name?.toLowerCase().includes(query)
    );
  });

  editForm = this.fb.group({
    asset: ['', Validators.required],
    quantity: [null as number | null],
    price: [null as number | null],
    amount: [null as number | null],
    grossAmount: [null as number | null],
    taxPercentage: [null as number | null],
    taxAmount: [null as number | null],
  });

  ngOnInit() {
    this.loadTransactions();
  }

  loadTransactions(page: number = 0) {
    this.loading.set(true);
    this.error.set(null);
    this.currentPage.set(page);

    this.stagingTransactionControllerService
      .findAllStagingTransactions(
        this.platformFilter() || undefined,
        this.statusFilter() || undefined,
        page,
        this.pageSize
      )
      .subscribe({
        next: (response) => {
          this.pageData.set(response);
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set('Failed to load staging transactions');
          this.toast.error('Failed to load staging transactions', 'Error');
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

  openDetails(transaction: StagingTransactionResponse) {
    if (!transaction?.id) {
      this.toast.error('Transaction is missing an id', 'Error');
      return;
    }

    this.selectedId.set(transaction.id);
    this.selectedDetail.set(null);
    this.detailError.set(null);
    this.detailLoading.set(true);

    // Load assets for select (only if not already loaded)
    if (this.assets().length === 0) {
      this.assetControllerService.findAllAssetsAsList().subscribe({
        next: (assetsResponse) => {
          this.assets.set(assetsResponse);
        },
        error: (err) => {
          console.error('Failed to load assets', err);
        },
      });
    }

    this.stagingTransactionControllerService
      .findStagingTransactionById(transaction.id)
      .subscribe({
        next: (detail) => {
          this.selectedDetail.set(detail);
          this.detailLoading.set(false);
          this.setFormValues(detail);
        },
        error: (err) => {
          this.detailError.set('Failed to load transaction details');
          this.toast.error('Failed to load transaction details', 'Error');
          this.detailLoading.set(false);
          console.error(err);
        },
      });
  }

  closeDetails() {
    this.selectedId.set(null);
    this.selectedDetail.set(null);
    this.detailError.set(null);
    this.detailLoading.set(false);
    this.updateError.set(null);
    this.updateLoading.set(false);
    this.editForm.reset({ asset: '', quantity: null, price: null, amount: null, grossAmount: null, taxPercentage: null, taxAmount: null });
  }

  toggleSelection(id: string | undefined) {
    if (!id) return;
    const current = new Set(this.selectedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.selectedIds.set(current);
  }

  isSelected(id: string | undefined): boolean {
    return id ? this.selectedIds().has(id) : false;
  }

  clearSelection() {
    this.selectedIds.set(new Set());
  }

  selectAll() {
    const current = new Set(this.selectedIds());
    this.paginatedTransactions().forEach((t) => {
      if (t.id) current.add(t.id);
    });
    this.selectedIds.set(current);
  }

  allSelected(): boolean {
    const transactions = this.paginatedTransactions();
    if (transactions.length === 0) return false;
    return transactions.every((t) => t.id && this.selectedIds().has(t.id));
  }

  bulkResolve() {
    const ids = Array.from(this.selectedIds());
    if (ids.length === 0) {
      this.toast.error('No transactions selected', 'Error');
      return;
    }

    this.bulkLoading.set(true);
    this.stagingTransactionControllerService
      .bulkUpdateStagingTransactionStatus(ids)
      .subscribe({
        next: () => {
          this.toast.success(`${ids.length} transaction(s) resolved`, 'Success');
          this.bulkLoading.set(false);
          this.clearSelection();
          this.loadTransactions(this.currentPage());
        },
        error: (err) => {
          this.toast.error('Bulk resolve failed', 'Error');
          this.bulkLoading.set(false);
          console.error(err);
        },
      });
  }

  selectAsset(symbol: string) {
    this.editForm.get('asset')?.setValue(symbol);
    this.assetSearchQuery.set('');
    this.assetDropdownOpen.set(false);
  }

  clearAsset() {
    this.editForm.get('asset')?.setValue('');
    this.assetSearchQuery.set('');
  }

  onAssetSearchBlur() {
    setTimeout(() => this.assetDropdownOpen.set(false), 200);
  }

  private setFormValues(detail: StagingTransactionResponse) {
    this.editForm.setValue({
      asset: detail.resolvedAsset?.symbol || '',
      quantity: detail.quantity ?? null,
      price: detail.price ?? null,
      amount: detail.amount ?? null,
      grossAmount: null,
      taxPercentage: null,
      taxAmount: null,
    });
  }

  saveChanges() {
    if (!this.selectedId()) {
      this.toast.error('No transaction selected', 'Error');
      return;
    }

    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      this.toast.error('Please fill required fields', 'Validation');
      return;
    }

    const { asset, quantity, price, amount, grossAmount, taxPercentage, taxAmount } = this.editForm.value;
    this.updateLoading.set(true);
    this.updateError.set(null);

    this.stagingTransactionControllerService
      .updateStagingTransaction({
        id: this.selectedId()!,
        asset: asset ?? '',
        quantity: quantity ?? undefined,
        price: price ?? undefined,
        amount: amount ?? undefined,
        grossAmount: grossAmount ?? undefined,
        taxPercentage: taxPercentage ?? undefined,
        taxAmount: taxAmount ?? undefined,
      })
      .subscribe({
        next: () => {
          this.toast.success('Staging transaction saved', 'Saved');
          this.updateLoading.set(false);
          // Refresh detail + list to reflect changes
          this.findAndRefreshCurrent();
        },
        error: (err) => {
          this.updateError.set('Save failed');
          this.toast.error('Save failed', 'Error');
          this.updateLoading.set(false);
          console.error(err);
        },
      });
  }

  private findAndRefreshCurrent() {
    if (!this.selectedId()) {
      return;
    }

    this.detailLoading.set(true);
    this.stagingTransactionControllerService
      .findStagingTransactionById(this.selectedId()!)
      .subscribe({
        next: (detail) => {
          this.selectedDetail.set(detail);
          this.setFormValues(detail);
          this.detailLoading.set(false);
          this.loadTransactions(this.currentPage());
        },
        error: (err) => {
          this.detailError.set('Failed to refresh details');
          this.detailLoading.set(false);
          console.error(err);
        },
      });
  }
}