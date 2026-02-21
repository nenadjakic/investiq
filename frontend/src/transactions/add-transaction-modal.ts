import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  Output,
  EventEmitter,
  inject,
  signal,
  computed,
  OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  TransactionControllerService,
  AssetControllerService,
  CurrencyControllerService,
} from '../app/core/api';
import { BuyRequest } from '../app/core/api/model/buy-request';
import { SellRequest } from '../app/core/api/model/sell-request';
import { DepositRequest } from '../app/core/api/model/deposit-request';
import { WithdrawalRequest } from '../app/core/api/model/withdrawal-request';
import { DividendRequest } from '../app/core/api/model/dividend-request';
import { AssetResponse } from '../app/core/api/model/asset-response';
import { CurrencyResponse } from '../app/core/api/model/currency-response';
import { ToastService } from '../shared/toast.service';

type TransactionType = 'BUY' | 'SELL' | 'DEPOSIT' | 'WITHDRAWAL' | 'DIVIDEND';
type Platform = 'TRADING212' | 'ETORO' | 'IBKR' | 'REVOLUT';

interface FormErrors {
  [key: string]: string;
}

interface FormTouched {
  [key: string]: boolean;
}

@Component({
  selector: 'app-add-transaction-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-transaction-modal.html',
})
export class AddTransactionModalComponent implements OnInit {
  private transactionControllerService = inject(TransactionControllerService);
  private assetControllerService = inject(AssetControllerService);
  private currencyControllerService = inject(CurrencyControllerService);
  private toast = inject(ToastService);

  @Output() transactionCreated = new EventEmitter<void>();

  // Modal state
  isOpen = signal<boolean>(false);

  // Reference data
  assets = signal<AssetResponse[]>([]);
  currencies = signal<CurrencyResponse[]>([]);
  loadingRefData = signal<boolean>(false);

  // Form state
  addFormLoading = signal<boolean>(false);
  formErrors = signal<FormErrors>({});
  formTouched = signal<FormTouched>({});

  transactionType = signal<TransactionType>('BUY');

  // Base form data
  baseFormData = signal({
    transactionId: '',
    platform: 'TRADING212' as Platform,
    transactionDate: new Date().toISOString().split('T')[0],
    currency: 'EUR',
  });

  // Type-specific form data
  buyFormData = signal({
    assetId: '',
    quantity: 0,
    price: 0,
    fee: 0,
  });

  sellFormData = signal({
    assetId: '',
    quantity: 0,
    price: 0,
    fee: 0,
  });

  depositFormData = signal({
    amount: 0,
    fee: 0,
  });

  withdrawalFormData = signal({
    amount: 0,
    fee: 0,
  });

  dividendFormData = signal({
    assetId: '',
    grossAmount: 0,
    netAmount: 0,
    taxAmount: 0,
    taxPercentage: 0,
  });

  platforms: Platform[] = ['TRADING212', 'ETORO', 'IBKR', 'REVOLUT'];

  isFormValid = computed(() => {
    const errors = this.formErrors();

    // If there are any errors, form is invalid
    if (Object.keys(errors).length > 0) {
      return false;
    }

    const type = this.transactionType();
    const base = this.baseFormData();

    // Base fields validation
    if (!base.transactionDate?.trim() || !base.platform?.trim() || !base.currency?.trim()) {
      return false;
    }

    switch (type) {
      case 'BUY':
      case 'SELL': {
        const data = type === 'BUY' ? this.buyFormData() : this.sellFormData();
        return data.assetId?.trim() !== '' && data.quantity > 0 && data.price > 0;
      }
      case 'DEPOSIT':
      case 'WITHDRAWAL': {
        const data = type === 'DEPOSIT' ? this.depositFormData() : this.withdrawalFormData();
        return data.amount > 0;
      }
      case 'DIVIDEND': {
        const data = this.dividendFormData();
        return data.assetId?.trim() !== '' && (data.grossAmount > 0 || data.netAmount > 0);
      }
      default:
        return false;
    }
  });

  ngOnInit(): void {
    this.loadReferenceData();
  }

  loadReferenceData(): void {
    this.loadingRefData.set(true);

    Promise.all([
      new Promise<void>((resolve) => {
        this.assetControllerService
          .findAllAssets(undefined, undefined, undefined, undefined, 0, 1000, ['symbol'])
          .subscribe({
            next: (pageResponse) => {
              this.assets.set(pageResponse.content || []);
              resolve();
            },
            error: (err) => {
              console.error('Failed to load assets', err);
              resolve();
            },
          });
      }),
      new Promise<void>((resolve) => {
        this.currencyControllerService.findAllCurrencies().subscribe({
          next: (currencies) => {
            const sorted = [...currencies].sort((a, b) => {
              const codeA = a.code?.toLowerCase() || '';
              const codeB = b.code?.toLowerCase() || '';
              return codeA.localeCompare(codeB);
            });
            this.currencies.set(sorted);
            resolve();
          },
          error: (err) => {
            console.error('Failed to load currencies', err);
            resolve();
          },
        });
      }),
    ]).finally(() => {
      this.loadingRefData.set(false);
    });
  }

  closeModal(): void {
    this.isOpen.set(false);
    this.resetForm();
  }

  openModal(): void {
    this.isOpen.set(true);
    this.resetForm();
  }

  resetForm(): void {
    this.transactionType.set('BUY');
    this.baseFormData.set({
      transactionId: '',
      platform: 'TRADING212',
      transactionDate: new Date().toISOString(),
      currency: 'EUR',
    });
    this.buyFormData.set({ assetId: '', quantity: 0, price: 0, fee: 0 });
    this.sellFormData.set({ assetId: '', quantity: 0, price: 0, fee: 0 });
    this.depositFormData.set({ amount: 0, fee: 0 });
    this.withdrawalFormData.set({ amount: 0, fee: 0 });
    this.dividendFormData.set({
      assetId: '',
      grossAmount: 0,
      netAmount: 0,
      taxAmount: 0,
      taxPercentage: 0,
    });
    this.formErrors.set({});
    this.formTouched.set({});
  }

  selectTransactionType(type: string): void {
    this.transactionType.set(type as TransactionType);
    this.formErrors.set({});
    this.formTouched.set({});
  }

  updateBaseField(field: string, value: any): void {
    const data = this.baseFormData();
    (data as any)[field] = value;
    this.baseFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  updateBuyField(field: string, value: any): void {
    const data = this.buyFormData();
    (data as any)[field] = field === 'assetId' ? value : parseFloat(value) || 0;
    this.buyFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  updateSellField(field: string, value: any): void {
    const data = this.sellFormData();
    (data as any)[field] = field === 'assetId' ? value : parseFloat(value) || 0;
    this.sellFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  updateDepositField(field: string, value: any): void {
    const data = this.depositFormData();
    (data as any)[field] = parseFloat(value) || 0;
    this.depositFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  updateWithdrawalField(field: string, value: any): void {
    const data = this.withdrawalFormData();
    (data as any)[field] = parseFloat(value) || 0;
    this.withdrawalFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  updateDividendField(field: string, value: any): void {
    const data = this.dividendFormData();
    (data as any)[field] = field === 'assetId' ? value : parseFloat(value) || 0;
    this.dividendFormData.set({ ...data });
    // Clear error for this field when user starts typing
    if (this.formErrors()[field]) {
      const errors = { ...this.formErrors() };
      delete errors[field];
      this.formErrors.set(errors);
    }
  }

  validateField(field: string, value: any): void {
    const errors = this.formErrors();

    if (
      field === 'quantity' ||
      field === 'price' ||
      field === 'amount' ||
      field === 'grossAmount' ||
      field === 'netAmount'
    ) {
      if (value < 0) {
        errors[field] = 'Value cannot be negative';
      } else {
        delete errors[field];
      }
    } else if (field === 'taxPercentage') {
      if (value < 0 || value > 100) {
        errors[field] = 'Tax percentage must be between 0 and 100';
      } else {
        delete errors[field];
      }
    }

    this.formErrors.set({ ...errors });
  }

  onFieldBlur(field: string): void {
    // Mark field as touched
    const touched = { ...this.formTouched() };
    touched[field] = true;
    this.formTouched.set(touched);

    // Validate the specific field
    this.validateFieldOnBlur(field);
  }

  validateFieldOnBlur(field: string): void {
    const errors: { [key: string]: string } = { ...this.formErrors() };
    const type = this.transactionType();
    const base = this.baseFormData();

    switch (field) {
      case 'transactionDate':
        if (!base.transactionDate || !base.transactionDate.trim()) {
          errors['transactionDate'] = 'Date is required';
        } else {
          delete errors['transactionDate'];
        }
        break;

      case 'platform':
        if (!base.platform || !base.platform.trim()) {
          errors['platform'] = 'Platform is required';
        } else {
          delete errors['platform'];
        }
        break;

      case 'currency':
        if (!base.currency || !base.currency.trim()) {
          errors['currency'] = 'Currency is required';
        } else {
          delete errors['currency'];
        }
        break;

      case 'assetId': {
        const assetId =
          type === 'BUY'
            ? this.buyFormData().assetId
            : type === 'SELL'
              ? this.sellFormData().assetId
              : type === 'DIVIDEND'
                ? this.dividendFormData().assetId
                : '';
        if (
          (type === 'BUY' || type === 'SELL' || type === 'DIVIDEND') &&
          (!assetId || !assetId.trim())
        ) {
          errors['assetId'] = 'Asset is required';
        } else {
          delete errors['assetId'];
        }
        break;
      }

      case 'quantity': {
        const quantity =
          type === 'BUY' ? this.buyFormData().quantity : this.sellFormData().quantity;
        if ((type === 'BUY' || type === 'SELL') && quantity <= 0) {
          errors['quantity'] = 'Quantity must be greater than 0';
        } else {
          delete errors['quantity'];
        }
        break;
      }

      case 'price': {
        const price = type === 'BUY' ? this.buyFormData().price : this.sellFormData().price;
        if ((type === 'BUY' || type === 'SELL') && price <= 0) {
          errors['price'] = 'Price must be greater than 0';
        } else {
          delete errors['price'];
        }
        break;
      }

      case 'amount': {
        const amount =
          type === 'DEPOSIT' ? this.depositFormData().amount : this.withdrawalFormData().amount;
        if ((type === 'DEPOSIT' || type === 'WITHDRAWAL') && amount <= 0) {
          errors['amount'] = 'Amount must be greater than 0';
        } else {
          delete errors['amount'];
        }
        break;
      }

      case 'grossAmount':
      case 'netAmount': {
        const data = this.dividendFormData();
        const hasAmount = data.grossAmount > 0 || data.netAmount > 0;
        if (type === 'DIVIDEND' && !hasAmount) {
          errors['grossAmount'] = 'Gross or Net amount must be provided';
          errors['netAmount'] = 'Gross or Net amount must be provided';
        } else {
          delete errors['grossAmount'];
          delete errors['netAmount'];
        }
        break;
      }

      case 'taxPercentage': {
        const taxPercentage = this.dividendFormData().taxPercentage;
        if (taxPercentage < 0 || taxPercentage > 100) {
          errors['taxPercentage'] = 'Tax percentage must be between 0 and 100';
        } else {
          delete errors['taxPercentage'];
        }
        break;
      }
    }

    this.formErrors.set(errors);
  }

  submitForm(): void {
    // Mark all fields as touched and validate them
    const allFields = this.getAllFormFields();
    const touched: FormTouched = {};
    allFields.forEach((field) => {
      touched[field] = true;
      this.validateFieldOnBlur(field);
    });
    this.formTouched.set(touched);

    if (!this.isFormValid()) {
      this.toast.error('Please fix form errors', 'Validation Error');
      return;
    }

    this.addFormLoading.set(true);

    const type = this.transactionType();
    const base = this.baseFormData();

    switch (type) {
      case 'BUY':
        this.submitBuyTransaction();
        break;
      case 'SELL':
        this.submitSellTransaction();
        break;
      case 'DEPOSIT':
        this.submitDepositTransaction();
        break;
      case 'WITHDRAWAL':
        this.submitWithdrawalTransaction();
        break;
      case 'DIVIDEND':
        this.submitDividendTransaction();
        break;
    }
  }

  private submitBuyTransaction(): void {
    const base = this.baseFormData();
    const data = this.buyFormData();

    const request: BuyRequest = {
      transactionId: base.transactionId || undefined,
      platform: base.platform,
      transactionDate: this.toOffsetDateTime(base.transactionDate),
      assetId: data.assetId,
      quantity: data.quantity,
      price: data.price,
      currency: base.currency,
      fee: data.fee || undefined,
    };

    this.transactionControllerService.addBuyTransaction(request).subscribe({
      next: () => {
        this.toast.success('Buy transaction added successfully', 'Success');
        this.resetForm();
        this.addFormLoading.set(false);
        this.transactionCreated.emit();
      },
      error: (err) => {
        console.error('Failed to add buy transaction', err);
        this.toast.error(err.error?.message || 'Failed to add buy transaction', 'Error');
        this.addFormLoading.set(false);
      },
    });
  }

  private submitSellTransaction(): void {
    const base = this.baseFormData();
    const data = this.sellFormData();

    const request: SellRequest = {
      transactionId: base.transactionId || undefined,
      platform: base.platform,
      transactionDate: this.toOffsetDateTime(base.transactionDate),
      assetId: data.assetId,
      quantity: data.quantity,
      price: data.price,
      currency: base.currency,
      fee: data.fee || undefined,
    };

    this.transactionControllerService.addSellTransaction(request).subscribe({
      next: () => {
        this.toast.success('Sell transaction added successfully', 'Success');
        this.resetForm();
        this.addFormLoading.set(false);
        this.transactionCreated.emit();
      },
      error: (err) => {
        console.error('Failed to add sell transaction', err);
        this.toast.error(err.error?.message || 'Failed to add sell transaction', 'Error');
        this.addFormLoading.set(false);
      },
    });
  }

  private submitDepositTransaction(): void {
    const base = this.baseFormData();
    const data = this.depositFormData();

    const request: DepositRequest = {
      transactionId: base.transactionId || undefined,
      platform: base.platform,
      transactionDate: this.toOffsetDateTime(base.transactionDate),
      amount: data.amount,
      currency: base.currency,
      fee: data.fee || undefined,
    };

    this.transactionControllerService.addDepositTransaction(request).subscribe({
      next: () => {
        this.toast.success('Deposit transaction added successfully', 'Success');
        this.resetForm();
        this.addFormLoading.set(false);
        this.transactionCreated.emit();
      },
      error: (err) => {
        console.error('Failed to add deposit transaction', err);
        this.toast.error(err.error?.message || 'Failed to add deposit transaction', 'Error');
        this.addFormLoading.set(false);
      },
    });
  }

  private submitWithdrawalTransaction(): void {
    const base = this.baseFormData();
    const data = this.withdrawalFormData();

    const request: WithdrawalRequest = {
      transactionId: base.transactionId || undefined,
      platform: base.platform,
      transactionDate: this.toOffsetDateTime(base.transactionDate),
      amount: data.amount,
      currency: base.currency,
      fee: data.fee || undefined,
    };

    this.transactionControllerService.addWithdrawalTransaction(request).subscribe({
      next: () => {
        this.toast.success('Withdrawal transaction added successfully', 'Success');
        this.resetForm();
        this.addFormLoading.set(false);
        this.transactionCreated.emit();
      },
      error: (err) => {
        console.error('Failed to add withdrawal transaction', err);
        this.toast.error(err.error?.message || 'Failed to add withdrawal transaction', 'Error');
        this.addFormLoading.set(false);
      },
    });
  }

  private submitDividendTransaction(): void {
    const base = this.baseFormData();
    const data = this.dividendFormData();

    const request: DividendRequest = {
      transactionId: base.transactionId || undefined,
      platform: base.platform,
      transactionDate: this.toOffsetDateTime(base.transactionDate),
      assetId: data.assetId,
      grossAmount: data.grossAmount || undefined,
      netAmount: data.netAmount || undefined,
      taxAmount: data.taxAmount || undefined,
      taxPercentage: data.taxPercentage || undefined,
      currency: base.currency,
    };

    this.transactionControllerService.addDividendTransaction(request).subscribe({
      next: () => {
        this.toast.success('Dividend transaction added successfully', 'Success');
        this.resetForm();
        this.addFormLoading.set(false);
        this.transactionCreated.emit();
      },
      error: (err) => {
        console.error('Failed to add dividend transaction', err);
        this.toast.error(err.error?.message || 'Failed to add dividend transaction', 'Error');
        this.addFormLoading.set(false);
      },
    });
  }

  private getAllFormFields(): string[] {
    const type = this.transactionType();
    const baseFields = ['transactionDate', 'platform', 'currency'];

    switch (type) {
      case 'BUY':
      case 'SELL':
        return [...baseFields, 'assetId', 'quantity', 'price', 'fee'];
      case 'DEPOSIT':
      case 'WITHDRAWAL':
        return [...baseFields, 'amount', 'fee'];
      case 'DIVIDEND':
        return [...baseFields, 'assetId', 'grossAmount', 'netAmount', 'taxAmount', 'taxPercentage'];
      default:
        return baseFields;
    }
  }

  private toOffsetDateTime(dateStr: string): string {
    if (!dateStr) return '';
    const datePart = dateStr.includes('T') ? dateStr.split('T')[0] : dateStr;
    return new Date(`${datePart}T00:00:00.000Z`).toISOString();
  }
}
