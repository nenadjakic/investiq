import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { CountryControllerService, CurrencyControllerService, ExchangeControllerService, CompanyControllerService, SectorControllerService, IndustryControllerService } from '../core/api';
import { CountryResponse } from '../core/api/model/country-response';
import { CurrencyResponse } from '../core/api/model/currency-response';
import { ExchangeResponse } from '../core/api/model/exchange-response';
import { CompanyResponse } from '../core/api/model/company-response';
import { SectorResponse } from '../core/api/model/sector-response';
import { IndustryResponse } from '../core/api/model/industry-response';
import { ToastService } from '../../shared/toast.service';

type ReferenceItem = {
	name: string;
	code?: string;
	note?: string;
};

type ReferenceSection = {
	key: 'countries' | 'currencies' | 'exchanges' | 'companies' | 'sectors' | 'industries';
	title: string;
	description: string;
	ctaLabel: string;
	items: ReferenceItem[];
};

type ReferenceStat = {
	label: string;
	value: string;
	hint: string;
};

@Component({
	selector: 'app-reference-data',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './reference-data.html',
})
export class ReferenceData {
private countryControllerService = inject(CountryControllerService)
private currencyControllerService = inject(CurrencyControllerService)
private exchangeControllerService = inject(ExchangeControllerService)
private companyControllerService = inject(CompanyControllerService)
private sectorControllerService = inject(SectorControllerService)
private industryControllerService = inject(IndustryControllerService)
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
	companies = signal<CompanyResponse[]>([]);
	companiesCurrentPage = signal<number>(1);
	companiesPerPage = 5;
	companiesSearchTerm = signal<string>('');
	sectors = signal<SectorResponse[]>([]);
	sectorsCurrentPage = signal<number>(1);
	sectorsPerPage = 5;
	sectorsSearchTerm = signal<string>('');
	industries = signal<IndustryResponse[]>([]);
	industriesCurrentPage = signal<number>(1);
	industriesPerPage = 5;
	industriesSearchTerm = signal<string>('');


	stats = signal<ReferenceStat[]>([
		{ label: 'Countries', value: '0', hint: '' },
		{ label: 'Currencies', value: '0', hint: 'Major + exotics' },
		{ label: 'Exchanges', value: '0', hint: 'Primary + alt markets' },
		{ label: 'Companies', value: '1,240', hint: 'Tickers and ISINs' },
		{ label: 'Sectors', value: '11', hint: 'Top-level sector view' },
		{ label: 'Industries', value: '27', hint: 'GICS inspired mapping' },
	]);


	get filteredCountries(): CountryResponse[] {
		const term = this.countriesSearchTerm().toLowerCase().trim();
		if (!term) {
			return this.countries();
		}
		return this.countries().filter(country => 
			country.name?.toLowerCase().includes(term) || 
			country.code?.toLowerCase().includes(term)
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
		return this.currencies().filter(currency =>
			currency.name?.toLowerCase().includes(term) ||
			currency.code?.toLowerCase().includes(term) ||
			currency.symbol?.toLowerCase().includes(term)
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
		return this.exchanges().filter(exchange =>
			exchange.name?.toLowerCase().includes(term) ||
			exchange.mic?.toLowerCase().includes(term) ||
			exchange.symbol?.toLowerCase().includes(term)
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

	get filteredCompanies(): CompanyResponse[] {
		const term = this.companiesSearchTerm().toLowerCase().trim();
		if (!term) {
			return this.companies();
		}
		return this.companies().filter(company =>
			company.name?.toLowerCase().includes(term) ||
			company.country?.code?.toLowerCase().includes(term) ||
			company.country?.name?.toLowerCase().includes(term) ||
			company.industry?.name?.toLowerCase().includes(term)
		);
	}

	get paginatedCompanies(): CompanyResponse[] {
		const start = (this.companiesCurrentPage() - 1) * this.companiesPerPage;
		const end = start + this.companiesPerPage;
		return this.filteredCompanies.slice(start, end);
	}

	get totalCompaniesPages(): number {
		return Math.ceil(this.filteredCompanies.length / this.companiesPerPage);
	}

	get canGoToPrevCompaniesPage(): boolean {
		return this.companiesCurrentPage() > 1;
	}

	get canGoToNextCompaniesPage(): boolean {
		return this.companiesCurrentPage() < this.totalCompaniesPages;
	}

	prevCompaniesPage() {
		if (this.canGoToPrevCompaniesPage) {
			this.companiesCurrentPage.set(this.companiesCurrentPage() - 1);
		}
	}

	nextCompaniesPage() {
		if (this.canGoToNextCompaniesPage) {
			this.companiesCurrentPage.set(this.companiesCurrentPage() + 1);
		}
	}

	onCompaniesSearch(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		this.companiesSearchTerm.set(value);
		this.companiesCurrentPage.set(1);
	}

	get filteredSectors(): SectorResponse[] {
		const term = this.sectorsSearchTerm().toLowerCase().trim();
		if (!term) {
			return this.sectors();
		}
		return this.sectors().filter(sector =>
			sector.name?.toLowerCase().includes(term)
		);
	}

	get paginatedSectors(): SectorResponse[] {
		const start = (this.sectorsCurrentPage() - 1) * this.sectorsPerPage;
		const end = start + this.sectorsPerPage;
		return this.filteredSectors.slice(start, end);
	}

	get totalSectorsPages(): number {
		return Math.ceil(this.filteredSectors.length / this.sectorsPerPage);
	}

	get canGoToPrevSectorsPage(): boolean {
		return this.sectorsCurrentPage() > 1;
	}

	get canGoToNextSectorsPage(): boolean {
		return this.sectorsCurrentPage() < this.totalSectorsPages;
	}

	prevSectorsPage() {
		if (this.canGoToPrevSectorsPage) {
			this.sectorsCurrentPage.set(this.sectorsCurrentPage() - 1);
		}
	}

	nextSectorsPage() {
		if (this.canGoToNextSectorsPage) {
			this.sectorsCurrentPage.set(this.sectorsCurrentPage() + 1);
		}
	}

	onSectorsSearch(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		this.sectorsSearchTerm.set(value);
		this.sectorsCurrentPage.set(1);
	}

	get filteredIndustries(): IndustryResponse[] {
		const term = this.industriesSearchTerm().toLowerCase().trim();
		if (!term) {
			return this.industries();
		}
		return this.industries().filter(industry =>
			industry.name?.toLowerCase().includes(term) ||
			industry.sector?.name?.toLowerCase().includes(term)
		);
	}

	get paginatedIndustries(): IndustryResponse[] {
		const start = (this.industriesCurrentPage() - 1) * this.industriesPerPage;
		const end = start + this.industriesPerPage;
		return this.filteredIndustries.slice(start, end);
	}

	get totalIndustriesPages(): number {
		return Math.ceil(this.filteredIndustries.length / this.industriesPerPage);
	}

	get canGoToPrevIndustriesPage(): boolean {
		return this.industriesCurrentPage() > 1;
	}

	get canGoToNextIndustriesPage(): boolean {
		return this.industriesCurrentPage() < this.totalIndustriesPages;
	}

	prevIndustriesPage() {
		if (this.canGoToPrevIndustriesPage) {
			this.industriesCurrentPage.set(this.industriesCurrentPage() - 1);
		}
	}

	nextIndustriesPage() {
		if (this.canGoToNextIndustriesPage) {
			this.industriesCurrentPage.set(this.industriesCurrentPage() + 1);
		}
	}

	onIndustriesSearch(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		this.industriesSearchTerm.set(value);
		this.industriesCurrentPage.set(1);
	}

	ngOnInit(): void {
		this.loadCountries();
		this.loadCurrencies();
		this.loadExchanges();
		this.loadCompanies();
		this.loadSectors();
		this.loadIndustries();
	}

	private loadCountries() {
		this.countryControllerService.findAllCountries().subscribe({
			next: (response) => {
				const list = Array.isArray(response) ? response : [];
				this.countries.set(list);
				// Update stats with actual countries count
				const currentStats = this.stats();
				const updatedStats = currentStats.map(stat => 
					stat.label === 'Countries' 
						? { ...stat, value: list.length.toString() }
						: stat
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
				const updatedStats = currentStats.map(stat =>
					stat.label === 'Currencies'
						? { ...stat, value: list.length.toString() }
						: stat
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
				const updatedStats = currentStats.map(stat =>
					stat.label === 'Exchanges'
						? { ...stat, value: list.length.toString() }
						: stat
				);
				this.stats.set(updatedStats);
			},
			error: (err) => {
				console.error('Failed to load exchanges', err);
			},
		});
	}

	private loadCompanies() {
		this.companyControllerService.findAllCompanies().subscribe({
			next: (response) => {
				const list = Array.isArray(response) ? response : [];
				this.companies.set(list);
				const currentStats = this.stats();
				const updatedStats = currentStats.map(stat =>
					stat.label === 'Companies'
						? { ...stat, value: list.length.toString() }
						: stat
				);
				this.stats.set(updatedStats);
			},
			error: (err) => {
				console.error('Failed to load companies', err);
			},
		});
	}

	private loadSectors() {
		this.sectorControllerService.findAllSectors().subscribe({
			next: (response) => {
				const list = Array.isArray(response) ? response : [];
				this.sectors.set(list);
				const currentStats = this.stats();
				const updatedStats = currentStats.map(stat =>
					stat.label === 'Sectors'
						? { ...stat, value: list.length.toString() }
						: stat
				);
				this.stats.set(updatedStats);
			},
			error: (err) => {
				console.error('Failed to load sectors', err);
			},
		});
	}

	private loadIndustries() {
		this.industryControllerService.findAllIndustries().subscribe({
			next: (response) => {
				const list = Array.isArray(response) ? response : [];
				this.industries.set(list);
				const currentStats = this.stats();
				const updatedStats = currentStats.map(stat =>
					stat.label === 'Industries'
						? { ...stat, value: list.length.toString() }
						: stat
				);
				this.stats.set(updatedStats);
			},
			error: (err) => {
				console.error('Failed to load industries', err);
			},
		});
	}
}
