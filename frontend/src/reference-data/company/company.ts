import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  CompanyControllerService,
  CountryControllerService,
  IndustryControllerService,
} from '../../app/core/api';
import { CompanyResponse } from '../../app/core/api/model/company-response';
import { CountryResponse } from '../../app/core/api/model/country-response';
import { IndustryResponse } from '../../app/core/api/model/industry-response';
import { CompanyAddRequest } from '../../app/core/api/model/company-add-request';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-company',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './company.html',
})
export class Company implements OnInit {
  private companyControllerService = inject(CompanyControllerService);
  private countryControllerService = inject(CountryControllerService);
  private industryControllerService = inject(IndustryControllerService);
  private toast = inject(ToastService);

  companies = signal<CompanyResponse[]>([]);
  companiesCurrentPage = signal<number>(1);
  companiesPerPage = 5;
  companiesSearchTerm = signal<string>('');

  countries = signal<CountryResponse[]>([]);
  industries = signal<IndustryResponse[]>([]);

  @Output() companiesCountChanged = new EventEmitter<number>();

  showCompanyModal = signal<boolean>(false);
  companyForm = signal<{ name: string; countryCode: string; industryId: string }>({
    name: '',
    countryCode: '',
    industryId: '',
  });
  isAddingCompany = signal<boolean>(false);

  get filteredCompanies(): CompanyResponse[] {
    const term = this.companiesSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.companies();
    }
    return this.companies().filter(
      (company) =>
        company.name?.toLowerCase().includes(term) ||
        company.country?.code?.toLowerCase().includes(term) ||
        company.country?.name?.toLowerCase().includes(term) ||
        company.industry?.name?.toLowerCase().includes(term),
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

  openCompanyModal(): void {
    this.companyForm.set({ name: '', countryCode: '', industryId: '' });
    this.showCompanyModal.set(true);
  }

  closeCompanyModal(): void {
    this.showCompanyModal.set(false);
    this.companyForm.set({ name: '', countryCode: '', industryId: '' });
  }

  submitAddCompany(): void {
    const form = this.companyForm();

    if (!form.name.trim() || !form.countryCode.trim() || !form.industryId.trim()) {
      this.toast.error('Please fill in all fields');
      return;
    }

    this.isAddingCompany.set(true);

    const request: CompanyAddRequest = {
      name: form.name.trim(),
      countryCode: form.countryCode.trim(),
      industryId: form.industryId.trim(),
    };

    this.companyControllerService.createCompany(request).subscribe({
      next: () => {
        this.toast.success('Company added successfully');
        this.closeCompanyModal();
        this.loadCompanies();
        this.isAddingCompany.set(false);
      },
      error: (err) => {
        console.error('Failed to add company', err);
        this.toast.error('Failed to add company');
        this.isAddingCompany.set(false);
      },
    });
  }

  ngOnInit(): void {
    this.loadCountries();
    this.loadIndustries();
    this.loadCompanies();
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

  private loadIndustries() {
    this.industryControllerService.findAllIndustries().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.industries.set(list);
      },
      error: (err) => {
        console.error('Failed to load industries', err);
      },
    });
  }

  private loadCompanies() {
    this.companyControllerService.findAllCompanies().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.companies.set(list);
        this.companiesCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load companies', err);
      },
    });
  }
}
