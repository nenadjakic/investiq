import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, inject, signal, computed, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AssetControllerService, CompanyControllerService, ExchangeControllerService, CurrencyControllerService } from '../app/core/api';
import { AssetAddRequest } from '../app/core/api/model/asset-add-request';
import { CompanyResponse } from '../app/core/api/model/company-response';
import { ExchangeResponse } from '../app/core/api/model/exchange-response';
import { CurrencyResponse } from '../app/core/api/model/currency-response';
import { ToastService } from '../shared/toast.service';
import { type Platform } from '../app/core/platform.service';

@Component({
  selector: 'app-add-asset-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-asset-modal.html',
})
export class AddAssetModalComponent implements OnInit {
  private assetControllerService = inject(AssetControllerService);
  private companyControllerService = inject(CompanyControllerService);
  private exchangeControllerService = inject(ExchangeControllerService);
  private currencyControllerService = inject(CurrencyControllerService);
  private toast = inject(ToastService);

  @Input() isOpen = signal<boolean>(false);
  @Input() platforms: Platform[] = ['TRADING212', 'ETORO', 'IBKR', 'REVOLUT'];
  @Output() close = new EventEmitter<void>();
  @Output() assetCreated = new EventEmitter<void>();

  // Reference data
  companies = signal<CompanyResponse[]>([]);
  exchanges = signal<ExchangeResponse[]>([]);
  currencies = signal<CurrencyResponse[]>([]);
  loadingRefData = signal<boolean>(false);

  // Form state
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

  ngOnInit(): void {
    this.loadReferenceData();
  }

  loadReferenceData(): void {
    this.loadingRefData.set(true);
    
    this.companyControllerService.findAllCompanies().subscribe({
      next: (companies) => {
        const sorted = [...companies].sort((a, b) => {
          const nameA = a.name?.toLowerCase() || '';
          const nameB = b.name?.toLowerCase() || '';
          return nameA.localeCompare(nameB);
        });
        this.companies.set(sorted);
      },
      error: (err) => {
        console.error('Failed to load companies', err);
        this.toast.error('Failed to load companies');
        this.loadingRefData.set(false);
      },
    });

    this.exchangeControllerService.findAllExchanges().subscribe({
      next: (exchanges) => {
        const sorted = [...exchanges].sort((a, b) => {
          const nameA = a.name?.toLowerCase() || '';
          const nameB = b.name?.toLowerCase() || '';
          return nameA.localeCompare(nameB);
        });
        this.exchanges.set(sorted);
      },
      error: (err) => {
        console.error('Failed to load exchanges', err);
        this.toast.error('Failed to load exchanges');
        this.loadingRefData.set(false);
      },
    });

    this.currencyControllerService.findAllCurrencies().subscribe({
      next: (currencies) => {
        const sorted = [...currencies].sort((a, b) => {
          const codeA = a.code?.toLowerCase() || '';
          const codeB = b.code?.toLowerCase() || '';
          return codeA.localeCompare(codeB);
        });
        this.currencies.set(sorted);
        this.loadingRefData.set(false);
      },
      error: (err) => {
        console.error('Failed to load currencies', err);
        this.toast.error('Failed to load currencies');
        this.loadingRefData.set(false);
      },
    });
  }

  closeModal() {
    this.isOpen.set(false);
    this.formErrors.set({});
    this.formTouched.set({});
    this.close.emit();
  }

  openModal() {
    this.isOpen.set(true);
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
        this.closeModal();
        this.assetCreated.emit();
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
