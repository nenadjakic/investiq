import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssetControllerService } from '../app/core/api';
import { AssetResponse } from '../app/core/api/model/asset-response';
import { ToastService } from '../shared/toast.service';

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assets.html',
})
export class AssetPage implements OnInit {
  private assetControllerService = inject(AssetControllerService);
  private toast = inject(ToastService);

  assets = signal<AssetResponse[]>([]);
  currentPage = signal<number>(1); // 1-based for UI
  pageSize = 10;
  totalPages = signal<number>(1);
  totalElements = signal<number>(0);
  loading = signal<boolean>(false);

  symbolTerm = signal<string>('');
  companyTerm = signal<string>('');
  exchangeTerm = signal<string>('');
  currencyTerm = signal<string>('');

  canPrev = computed(() => this.currentPage() > 1);
  canNext = computed(() => this.currentPage() < this.totalPages());
  pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    if (total <= 0) {
      return [] as number[];
    }
    const windowSize = 2; // current ±2
    const start = Math.max(1, current - windowSize);
    const end = Math.min(total, current + windowSize);
    const pages: number[] = [];
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  });
  hasLeadingGap = computed(() => {
    const pages = this.pageNumbers();
    return pages.length > 0 && pages[0] > 1;
  });
  hasTrailingGap = computed(() => {
    const pages = this.pageNumbers();
    const last = pages[pages.length - 1];
    return pages.length > 0 && last < this.totalPages();
  });

  ngOnInit(): void {
    this.loadAssets();
  }

  loadAssets(page?: number) {
    const targetPage = Math.max(1, page ?? this.currentPage());
    this.currentPage.set(targetPage);
    this.loading.set(true);
    const pageIndex = targetPage - 1; // API is 0-based
    this.assetControllerService
      .findAllAssets(
        this.symbolTerm() || undefined,
        this.currencyTerm() || undefined,
        this.exchangeTerm() || undefined,
        this.companyTerm() || undefined,
        pageIndex,
        this.pageSize,
        ['symbol,asc']
      )
        .subscribe({
          next: (resp) => {
            const content = Array.isArray(resp.content) ? resp.content : [];
            const totalPages = Math.max(resp.totalPages ?? 1, 1);
            this.assets.set(content);
            this.totalPages.set(totalPages);
            this.totalElements.set(resp.totalElements ?? content.length);
          },
          error: (err) => {
            console.error('Failed to load assets', err);
            this.toast.error('Failed to load assets');
            this.loading.set(false);
          },
          complete: () => this.loading.set(false),
        });
  }

  prevPage() {
    if (!this.canPrev()) return;
    this.loadAssets(this.currentPage() - 1);
  }

  nextPage() {
    if (!this.canNext()) return;
    this.loadAssets(this.currentPage() + 1);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages()) {
      this.loadAssets(page);
    }
  }

  firstPage() {
    if (this.currentPage() !== 1) {
      this.loadAssets(1);
    }
  }

  lastPage() {
    const last = this.totalPages();
    if (last >= 1 && this.currentPage() !== last) {
      this.loadAssets(last);
    }
  }

  onSearchChange() {
    this.currentPage.set(1);
    this.loadAssets();
  }
}
