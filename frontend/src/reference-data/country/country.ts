import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CountryControllerService } from '../../app/core/api';
import { CountryResponse } from '../../app/core/api/model/country-response';

@Component({
  selector: 'app-country',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './country.html',
})
export class Country implements OnInit {
  private countryControllerService = inject(CountryControllerService);

  countries = signal<CountryResponse[]>([]);
  countriesCurrentPage = signal<number>(1);
  countriesPerPage = 5;
  countriesSearchTerm = signal<string>('');

  @Output() countriesCountChanged = new EventEmitter<number>();

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

  onCountriesSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.countriesSearchTerm.set(value);
    this.countriesCurrentPage.set(1);
  }

  ngOnInit(): void {
    this.loadCountries();
  }

  private loadCountries() {
    this.countryControllerService.findAllCountries().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.countries.set(list);
        this.countriesCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load countries', err);
      },
    });
  }
}
