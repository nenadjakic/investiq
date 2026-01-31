import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  SectorControllerService,
  IndustryControllerService,
} from '../../app/core/api';
import { SectorResponse } from '../../app/core/api/model/sector-response';
import { IndustryResponse } from '../../app/core/api/model/industry-response';
import { IndustryAddRequest } from '../../app/core/api/model/industry-add-request';
import { ToastService } from '../../shared/toast.service';

@Component({
  selector: 'app-industry',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './industry.html',
})
export class Industry implements OnInit {
  private sectorControllerService = inject(SectorControllerService);
  private industryControllerService = inject(IndustryControllerService);
  private toast = inject(ToastService);

  sectors = signal<SectorResponse[]>([]);
  @Output() industriesCountChanged = new EventEmitter<number>();

  industries = signal<IndustryResponse[]>([]);
  industriesCurrentPage = signal<number>(1);
  industriesPerPage = 5;
  industriesSearchTerm = signal<string>('');

  showIndustryModal = signal<boolean>(false);
  industryForm = signal<{ name: string; sectorId: string }>({ name: '', sectorId: '' });
  isAddingIndustry = signal<boolean>(false);

  get filteredIndustries(): IndustryResponse[] {
    const term = this.industriesSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.industries();
    }
    return this.industries().filter(
      (industry) =>
        industry.name?.toLowerCase().includes(term) ||
        industry.sector?.name?.toLowerCase().includes(term),
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

  ngOnInit(): void {
    this.loadSectors();
    this.loadIndustries();
  }

  private loadSectors() {
    this.sectorControllerService.findAllSectors().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.sectors.set(list);
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
        this.industriesCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load industries', err);
      },
    });
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

  openIndustryModal(): void {
    this.industryForm.set({ name: '', sectorId: '' });
    this.showIndustryModal.set(true);
  }

  closeIndustryModal(): void {
    this.showIndustryModal.set(false);
    this.industryForm.set({ name: '', sectorId: '' });
  }

  submitAddIndustry(): void {
    const form = this.industryForm();

    if (!form.name.trim() || !form.sectorId.trim()) {
      this.toast.error('Please fill in all fields');
      return;
    }

    this.isAddingIndustry.set(true);

    const request: IndustryAddRequest = {
      name: form.name.trim(),
      sectorId: form.sectorId.trim(),
    };

    this.industryControllerService.createIndustry(request).subscribe({
      next: () => {
        this.toast.success('Industry added successfully');
        this.closeIndustryModal();
        this.loadIndustries();
        this.isAddingIndustry.set(false);
      },
      error: (err) => {
        console.error('Failed to add industry', err);
        this.toast.error('Failed to add industry');
        this.isAddingIndustry.set(false);
      },
    });
  }
} 