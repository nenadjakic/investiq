import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Asset {
  id: string;
  name: string;
  type: 'STOCK' | 'ETF';
  exchange: string;
  country: string;
  currency: string;
}

@Component({
  selector: 'app-asset',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './asset.html'
})
export class AssetPage implements OnInit {
  assets = signal<Asset[]>([]);

  protected showFilter = signal(false);


  // pagination
  currentPage = signal(1);
  pageSize = 5;

  // filteri
  selectedExchange = signal('');
  selectedCountry = signal('');
  selectedCurrency = signal('');

  filteredAssets = computed(() => {
    let filtered = this.assets();
    if (this.selectedExchange()) {
      filtered = filtered.filter((a) => a.exchange === this.selectedExchange());
    }
    if (this.selectedCountry()) {
      filtered = filtered.filter((a) => a.country === this.selectedCountry());
    }
    if (this.selectedCurrency()) {
      filtered = filtered.filter((a) => a.currency === this.selectedCurrency());
    }
    return filtered;
  });

  paginatedAssets = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize;
    return this.filteredAssets().slice(start, start + this.pageSize);
  });


  itemsPerPage = 2;

  get totalPages(): number {
    return Math.ceil(586 / this.itemsPerPage);
  }
    goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage.set(page);
    }
  }

  ngOnInit() {
    // mock podaci
    const mockAssets: Asset[] = [
      {
        id: '1',
        name: 'Apple',
        type: 'STOCK',
        exchange: 'NASDAQ',
        country: 'USA',
        currency: 'USD',
      },
      {
        id: '2',
        name: 'Tesla',
        type: 'STOCK',
        exchange: 'NASDAQ',
        country: 'USA',
        currency: 'USD',
      },
      {
        id: '3',
        name: 'Vanguard S&P 500 ETF',
        type: 'ETF',
        exchange: 'NYSE',
        country: 'USA',
        currency: 'USD',
      },
      {
        id: '4',
        name: 'Siemens',
        type: 'STOCK',
        exchange: 'XETRA',
        country: 'DE',
        currency: 'EUR',
      },
      {
        id: '5',
        name: 'iShares MSCI Germany',
        type: 'ETF',
        exchange: 'XETRA',
        country: 'DE',
        currency: 'EUR',
      },
      // ... dodaj još mock podataka da testiraš pagination
    ];
    this.assets.set(mockAssets);
  }

  nextPage() {
    if (this.currentPage() * this.pageSize < this.filteredAssets().length) {
      this.currentPage.set(this.currentPage() + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 1) {
      this.currentPage.set(this.currentPage() - 1);
    }
  }
}
