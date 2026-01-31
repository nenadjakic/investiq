import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SectorControllerService } from '../../app/core/api';
import { SectorResponse } from '../../app/core/api/model/sector-response';

@Component({
  selector: 'app-sector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sector.html',
})
export class Sector implements OnInit {
  private sectorControllerService = inject(SectorControllerService);

  sectors = signal<SectorResponse[]>([]);
  sectorsCurrentPage = signal<number>(1);
  sectorsPerPage = 5;
  sectorsSearchTerm = signal<string>('');

  @Output() sectorsCountChanged = new EventEmitter<number>();

  get filteredSectors(): SectorResponse[] {
    const term = this.sectorsSearchTerm().toLowerCase().trim();
    if (!term) {
      return this.sectors();
    }
    return this.sectors().filter((sector) => sector.name?.toLowerCase().includes(term));
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

  ngOnInit(): void {
    this.loadSectors();
  }

  private loadSectors() {
    this.sectorControllerService.findAllSectors().subscribe({
      next: (response) => {
        const list = Array.isArray(response) ? response : [];
        this.sectors.set(list);
        this.sectorsCountChanged.emit(list.length);
      },
      error: (err) => {
        console.error('Failed to load sectors', err);
      },
    });
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
}
