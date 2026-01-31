import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CurrencyControllerService } from '../../app/core/api';
import { CurrencyResponse } from '../../app/core/api/model/currency-response';

@Component({
  selector: 'app-currency',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './currency.html',
})
export class Currency implements OnInit {
  private currencyControllerService = inject(CurrencyControllerService);

  currencies = signal<CurrencyResponse[]>([]);
  currenciesCurrentPage = signal<number>(1);
  currenciesPerPage = 5;
  currenciesSearchTerm = signal<string>('');

  @Output() currenciesCountChanged = new EventEmitter<number>();

  get filteredCurrencies(): CurrencyResponse[] {
    const term = this.currenciesSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.currencies();
    }
    return this.currencies().filter(
      (currency) =>
        currency.name?.toLowerCase().includes(term) ||
        currency.code?.toLowerCase().includes(term) ||
        currency.symbol?.toLowerCase().includes(term),
    );
  }

  get paginatedCurrencies(): CurrencyResponse[] {
    const start = (this.currenciesCurrentPage() - 1) * this.currenciesPerPage;
    const end = start + this.currenciesPerPage;
    return this.filteredCurrencies.slice(start, end);
  }

  get totalCurrenciesPages(): number {
    return Math.ceil(this.filteredCurrencies.length / this.currenciesPerPage);
  }

  get canGoToPrevCurrenciesPage(): boolean {
    return this.currenciesCurrentPage() > 1;
  }

  get canGoToNextCurrenciesPage(): boolean {
    return this.currenciesCurrentPage() < this.totalCurrenciesPages;
  }

  prevCurrenciesPage() {
    if (this.canGoToPrevCurrenciesPage) {
      this.currenciesCurrentPage.set(this.currenciesCurrentPage() - 1);
    }
  }

  nextCurrenciesPage() {
    if (this.canGoToNextCurrenciesPage) {
      this.currenciesCurrentPage.set(this.currenciesCurrentPage() + 1);
    }
  }

  onCurrenciesSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.currenciesSearchTerm.set(value);
    this.currenciesCurrentPage.set(1);
  }

  ngOnInit(): void {
    this.loadCurrencies();
  }

  private loadCurrencies() {
    this.currencyControllerService.findAllCurrencies().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.currencies.set(list);
        this.currenciesCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load currencies', err);
      },
    });
  }
}
