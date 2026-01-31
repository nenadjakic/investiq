import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  CountryControllerService,
  CurrencyControllerService,
  ExchangeControllerService,
} from '../app/core/api';
import { CountryResponse } from '../app/core/api/model/country-response';
import { CurrencyResponse } from '../app/core/api/model/currency-response';
import { ExchangeResponse } from '../app/core/api/model/exchange-response';
import { ToastService } from '../shared/toast.service';
import { Industry } from './industry/industry';
import { Sector } from './sector/sector';
import { Company } from './company/company';

type ReferenceStat = {
  label: string;
  value: string;
};

@Component({
  selector: 'app-reference-data',
  standalone: true,
  imports: [CommonModule, FormsModule, Industry, Sector, Company],
  templateUrl: './reference-data.html',
})
export class ReferenceData {
  private countryControllerService = inject(CountryControllerService);
  private currencyControllerService = inject(CurrencyControllerService);
  private exchangeControllerService = inject(ExchangeControllerService);
  private toast = inject(ToastService);
  countries = signal<CountryResponse[]>([]);
  countriesCurrentPage = signal<number>(1);
  countriesPerPage = 5;
  countriesSearchTerm = signal<string>('');
  currencies = signal<CurrencyResponse[]>([]);
  currenciesCurrentPage = signal<number>(1);
  currenciesPerPage = 5;
  currenciesSearchTerm = signal<string>('');
  exchanges = signal<ExchangeResponse[]>([]);
  exchangesCurrentPage = signal<number>(1);
  exchangesPerPage = 5;
  exchangesSearchTerm = signal<string>('');

  stats = signal<ReferenceStat[]>([
    { label: 'Countries', value: '0' },
    { label: 'Currencies', value: '0' },
    { label: 'Exchanges', value: '0' },
    { label: 'Companies', value: '0' },
    { label: 'Sectors', value: '0' },
    { label: 'Industries', value: '0' },
  ]);

  get filteredCountries(): CountryResponse[] {
    const term = this.countriesSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.countries();
    }
    return this.countries().filter(
      (country) =>
        country.name?.toLowerCase().includes(term) || country.code?.toLowerCase().includes(term),
    );
  }

  get paginatedCountries(): CountryResponse[] {
    const start = (this.countriesCurrentPage() - 1) * this.countriesPerPage;
    const end = start + this.countriesPerPage;
    return this.filteredCountries.slice(start, end);
  }

  get totalCountriesPages(): number {
    return Math.ceil(this.filteredCountries.length / this.countriesPerPage);
  }

  get canGoToPrevCountriesPage(): boolean {
    return this.countriesCurrentPage() > 1;
  }

  get canGoToNextCountriesPage(): boolean {
    return this.countriesCurrentPage() < this.totalCountriesPages;
  }

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

  get filteredExchanges(): ExchangeResponse[] {
    const term = this.exchangesSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.exchanges();
    }
    return this.exchanges().filter(
      (exchange) =>
        exchange.name?.toLowerCase().includes(term) ||
        exchange.mic?.toLowerCase().includes(term) ||
        exchange.symbol?.toLowerCase().includes(term),
    );
  }

  get paginatedExchanges(): ExchangeResponse[] {
    const start = (this.exchangesCurrentPage() - 1) * this.exchangesPerPage;
    const end = start + this.exchangesPerPage;
    return this.filteredExchanges.slice(start, end);
  }

  get totalExchangesPages(): number {
    return Math.ceil(this.filteredExchanges.length / this.exchangesPerPage);
  }

  get canGoToPrevExchangesPage(): boolean {
    return this.exchangesCurrentPage() > 1;
  }

  get canGoToNextExchangesPage(): boolean {
    return this.exchangesCurrentPage() < this.totalExchangesPages;
  }

  prevCountriesPage() {
    if (this.canGoToPrevCountriesPage) {
      this.countriesCurrentPage.set(this.countriesCurrentPage() - 1);
    }
  }

  nextCountriesPage() {
    if (this.canGoToNextCountriesPage) {
      this.countriesCurrentPage.set(this.countriesCurrentPage() + 1);
    }
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

  onCountriesSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.countriesSearchTerm.set(value);
    this.countriesCurrentPage.set(1); // Reset to first page on search
  }

  onCurrenciesSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.currenciesSearchTerm.set(value);
    this.currenciesCurrentPage.set(1);
  }

  prevExchangesPage() {
    if (this.canGoToPrevExchangesPage) {
      this.exchangesCurrentPage.set(this.exchangesCurrentPage() - 1);
    }
  }

  nextExchangesPage() {
    if (this.canGoToNextExchangesPage) {
      this.exchangesCurrentPage.set(this.exchangesCurrentPage() + 1);
    }
  }

  onExchangesSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.exchangesSearchTerm.set(value);
    this.exchangesCurrentPage.set(1);
  }



  ngOnInit(): void {
    this.loadCountries();
    this.loadCurrencies();
    this.loadExchanges();
  }

  private loadCountries() {
    this.countryControllerService.findAllCountries().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.countries.set(list);
        // Update stats with actual countries count
        const currentStats = this.stats();
        const updatedStats = currentStats.map((stat) =>
          stat.label === 'Countries' ? { ...stat, value: list.length.toString() } : stat,
        );
        this.stats.set(updatedStats);
      },
      error: (err) => {
        console.error('Failed to load countries', err);
      },
    });
  }

  private loadCurrencies() {
    this.currencyControllerService.findAllCurrencies().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.currencies.set(list);
        const currentStats = this.stats();
        const updatedStats = currentStats.map((stat) =>
          stat.label === 'Currencies' ? { ...stat, value: list.length.toString() } : stat,
        );
        this.stats.set(updatedStats);
      },
      error: (err) => {
        console.error('Failed to load currencies', err);
      },
    });
  }

  private loadExchanges() {
    this.exchangeControllerService.findAllExchanges().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.exchanges.set(list);
        const currentStats = this.stats();
        const updatedStats = currentStats.map((stat) =>
          stat.label === 'Exchanges' ? { ...stat, value: list.length.toString() } : stat,
        );
        this.stats.set(updatedStats);
      },
      error: (err) => {
        console.error('Failed to load exchanges', err);
      },
    });
  }

  onSectorsCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Sectors' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
  }

  onIndustriesCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Industries' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
  }

  onCompaniesCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Companies' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
  }
}
