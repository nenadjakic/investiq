import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssetControllerService } from '../app/core/api';
import { AssetResponse } from '../app/core/api/model/asset-response';
import { AssetAddRequest } from '../app/core/api/model/asset-add-request';
import { ToastService } from '../shared/toast.service';
import { PlatformService, type Platform } from '../app/core/platform.service';

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assets.html',
})
export class AssetPage implements OnInit {
  private assetControllerService = inject(AssetControllerService);
  private toast = inject(ToastService);
  private platformService = inject(PlatformService);

  // Available platforms for aliases
  platforms: Platform[] = ['TRADING212', 'ETORO', 'IBKR', 'REVOLUT'];

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

  // Form state
  showAddModal = signal<boolean>(false);
  addFormLoading = signal<boolean>(false);
  formErrors = signal<{ [key: string]: string }>({});
  formTouched = signal<{ [key: string]: boolean }>({});
  newAsset = signal<AssetAddRequest>({
    assetType: 'STOCK',
    symbol: '',
    name: '',
    currencyCode: '',
    exchangeId: '',
    companyId: '',
    fundManager: '',
    assetClass: 'EQUITY',
    trackedIndexId: '',
    aliases: {},
  });

  isFormValid = computed(() => {
    const errors = this.formErrors();
    const asset = this.newAsset();
    
    // Base required fields
    const hasBaseFields = asset.name.trim() !== '' && asset.currencyCode.trim() !== '';
    
    // Additional requirements based on asset type
    if (asset.assetType === 'STOCK') {
      return Object.keys(errors).length === 0 && 
             hasBaseFields && 
             asset.symbol.trim() !== '' &&
             asset.exchangeId?.trim() !== '' &&
             asset.companyId?.trim() !== '';
    } else if (asset.assetType === 'ETF') {
      return Object.keys(errors).length === 0 && 
             hasBaseFields && 
             asset.symbol.trim() !== '';
    } else if (asset.assetType === 'INDEX') {
      return Object.keys(errors).length === 0 && 
             hasBaseFields && 
             asset.symbol.trim() !== '';
    }
    
    return false;
  });

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

  openAddModal() {
    this.showAddModal.set(true);
    this.formErrors.set({});
    this.formTouched.set({});
    this.newAsset.set({
      assetType: 'STOCK',
      symbol: '',
      name: '',
      currencyCode: '',
      exchangeId: '',
      companyId: '',
      fundManager: '',
      assetClass: 'EQUITY',
      trackedIndexId: '',
      aliases: {},
    });
  }

  closeAddModal() {
    this.showAddModal.set(false);
    this.formErrors.set({});
    this.formTouched.set({});
  }

  updateAssetField(field: keyof AssetAddRequest, value: any) {
    this.newAsset.set({ ...this.newAsset(), [field]: value });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field as string]) {
      const errors = { ...this.formErrors() };
      delete errors[field as string];
      this.formErrors.set(errors);
    }
  }

  updateAssetAlias(platform: Platform, value: string) {
    const aliases = this.newAsset().aliases || {};
    if (value.trim()) {
      aliases[platform] = value;
    } else {
      delete aliases[platform];
    }
    this.newAsset.set({ ...this.newAsset(), aliases });
  }

  onFieldBlur(field: keyof AssetAddRequest) {
    // Mark field as touched
    const touched = { ...this.formTouched() };
    touched[field as string] = true;
    this.formTouched.set(touched);

    // Validate the specific field
    this.validateField(field);
  }

  validateField(field: keyof AssetAddRequest) {
    const errors: { [key: string]: string } = { ...this.formErrors() };
    const asset = this.newAsset();

    switch (field) {
      case 'symbol':
        if (!asset.symbol || !asset.symbol.trim()) {
          errors['symbol'] = 'Symbol is required';
        } else if (asset.symbol.trim().length > 20) {
          errors['symbol'] = 'Symbol must be 20 characters or less';
        } else {
          delete errors['symbol'];
        }
        break;

      case 'name':
        if (!asset.name || !asset.name.trim()) {
          errors['name'] = 'Name is required';
        } else if (asset.name.trim().length < 2) {
          errors['name'] = 'Name must be at least 2 characters';
        } else if (asset.name.trim().length > 255) {
          errors['name'] = 'Name must be 255 characters or less';
        } else {
          delete errors['name'];
        }
        break;

      case 'currencyCode':
        if (!asset.currencyCode || !asset.currencyCode.trim()) {
          errors['currencyCode'] = 'Currency Code is required';
        } else if (asset.currencyCode.trim().length !== 3) {
          errors['currencyCode'] = 'Currency Code must be exactly 3 characters (e.g., USD, EUR)';
        } else if (!/^[A-Z]{3}$/.test(asset.currencyCode.trim())) {
          errors['currencyCode'] = 'Currency Code must be 3 uppercase letters';
        } else {
          delete errors['currencyCode'];
        }
        break;

      case 'exchangeId':
        if (asset.assetType === 'STOCK') {
          if (!asset.exchangeId || !asset.exchangeId.trim()) {
            errors['exchangeId'] = 'Exchange is required for Stock';
          } else {
            delete errors['exchangeId'];
          }
        }
        break;

      case 'companyId':
        if (asset.assetType === 'STOCK') {
          if (!asset.companyId || !asset.companyId.trim()) {
            errors['companyId'] = 'Company is required for Stock';
          } else {
            delete errors['companyId'];
          }
        }
        break;
    }

    this.formErrors.set(errors);
  }

  submitAddAsset() {
    this.addFormLoading.set(true);
    this.assetControllerService.createAsset(this.newAsset()).subscribe({
      next: () => {
        this.toast.success('Asset created successfully');
        this.closeAddModal();
        this.currentPage.set(1);
        this.loadAssets();
      },
      error: (err) => {
        console.error('Failed to create asset', err);
        this.toast.error('Failed to create asset');
        this.addFormLoading.set(false);
      },
      complete: () => this.addFormLoading.set(false),
    });
  }
}
