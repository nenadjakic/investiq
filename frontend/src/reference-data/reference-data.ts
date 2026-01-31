import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../shared/toast.service';
import { Industry } from './industry/industry';
import { Sector } from './sector/sector';
import { Company } from './company/company';
import { Exchange } from './exchange/exchange';
import { Country } from './country/country';
import { Currency } from './currency/currency';

type ReferenceStat = {
  label: string;
  value: string;
};

@Component({
  selector: 'app-reference-data',
  standalone: true,
  imports: [CommonModule, FormsModule, Industry, Sector, Company, Exchange, Country, Currency],
  templateUrl: './reference-data.html',
})
export class ReferenceData {
  private toast = inject(ToastService);

  stats = signal<ReferenceStat[]>([
    { label: 'Countries', value: '0' },
    { label: 'Currencies', value: '0' },
    { label: 'Exchanges', value: '0' },
    { label: 'Companies', value: '0' },
    { label: 'Sectors', value: '0' },
    { label: 'Industries', value: '0' },
  ]);

  ngOnInit(): void {
  }

  onCountriesCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Countries' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
  }

  onCurrenciesCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Currencies' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
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

  onExchangesCountChange(count: number): void {
    const currentStats = this.stats();
    const updatedStats = currentStats.map((stat) =>
      stat.label === 'Exchanges' ? { ...stat, value: count.toString() } : stat,
    );
    this.stats.set(updatedStats);
  }
}
