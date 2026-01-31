import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ExchangeControllerService,
  CountryControllerService,
} from '../../app/core/api';
import { ExchangeResponse } from '../../app/core/api/model/exchange-response';
import { CountryResponse } from '../../app/core/api/model/country-response';
import { ExchangeAddRequest } from '../../app/core/api/model/exchange-add-request';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-exchange',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './exchange.html',
})
export class Exchange implements OnInit {
  private exchangeControllerService = inject(ExchangeControllerService);
  private countryControllerService = inject(CountryControllerService);
  private toast = inject(ToastService);

  exchanges = signal<ExchangeResponse[]>([]);
  exchangesCurrentPage = signal<number>(1);
  exchangesPerPage = 5;
  exchangesSearchTerm = signal<string>('');

  countries = signal<CountryResponse[]>([]);

  @Output() exchangesCountChanged = new EventEmitter<number>();

  showExchangeModal = signal<boolean>(false);
  exchangeForm = signal<{ name: string; mic: string; acronym: string; countryId: string }>({
    name: '',
    mic: '',
    acronym: '',
    countryId: '',
  });
  isAddingExchange = signal<boolean>(false);

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

  openExchangeModal(): void {
    this.exchangeForm.set({ name: '', mic: '', acronym: '', countryId: '' });
    this.showExchangeModal.set(true);
  }

  closeExchangeModal(): void {
    this.showExchangeModal.set(false);
    this.exchangeForm.set({ name: '', mic: '', acronym: '', countryId: '' });
  }

  submitAddExchange(): void {
    const form = this.exchangeForm();

    if (!form.name.trim() || !form.mic.trim() || !form.acronym.trim() || !form.countryId.trim()) {
      this.toast.error('Please fill in all fields');
      return;
    }

    this.isAddingExchange.set(true);

    const request: ExchangeAddRequest = {
      name: form.name.trim(),
      mic: form.mic.trim(),
      acronym: form.acronym.trim(),
      countryId: form.countryId.trim(),
    };

    this.exchangeControllerService.createExchange(request).subscribe({
      next: () => {
        this.toast.success('Exchange added successfully');
        this.closeExchangeModal();
        this.loadExchanges();
        this.isAddingExchange.set(false);
      },
      error: (err) => {
        console.error('Failed to add exchange', err);
        this.toast.error('Failed to add exchange');
        this.isAddingExchange.set(false);
      },
    });
  }

  ngOnInit(): void {
    this.loadCountries();
    this.loadExchanges();
  }

  private loadCountries() {
    this.countryControllerService.findAllCountries().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.countries.set(list);
      },
      error: (err) => {
        console.error('Failed to load countries', err);
      },
    });
  }

  private loadExchanges() {
    this.exchangeControllerService.findAllExchanges().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.exchanges.set(list);
        this.exchangesCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load exchanges', err);
      },
    });
  }
}
