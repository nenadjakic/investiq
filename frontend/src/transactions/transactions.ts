import { CommonModule } from "@angular/common";
import { Component, inject, OnInit, signal, computed } from "@angular/core";
import { PageTransactionResponse, TransactionControllerService } from "../app/core/api";
import { ToastService } from "../shared/toast.service";

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.html',
  standalone: true,
  imports: [CommonModule],
})
export class Transactions implements OnInit {
    private transactionControllerService = inject(TransactionControllerService);
    private toast = inject(ToastService);
    
    // Data
    pageData = signal<PageTransactionResponse>({});
    currentPage = signal(0);
    pageSize = 25;
    loading = signal(false);
    error = signal<string | null>(null);
    
    // Computed signals
    paginatedTransactions = computed(() => this.pageData().content || []);
    totalPages = computed(() => this.pageData().totalPages || 0);
    totalElements = computed(() => this.pageData().totalElements || 0);
    pageNumbers = computed(() => {
        const total = this.totalPages();
        const current = this.currentPage();
        if (total === 0) {
            return [] as number[];
        }
        const window = 2; // show current ±2
        const start = Math.max(0, current - window);
        const end = Math.min(total - 1, current + window);
        const pages: number[] = [];
        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    });
    hasLeadingGap = computed(() => {
        const pages = this.pageNumbers();
        return pages.length > 0 && pages[0] > 0;
    });
    hasTrailingGap = computed(() => {
        const pages = this.pageNumbers();
        const last = pages[pages.length - 1];
        return pages.length > 0 && last < this.totalPages() - 1;
    });
    
    ngOnInit() {
        this.loadTransactions();
    }
    
    loadTransactions(page: number = 0) {
        this.loading.set(true);
        this.error.set(null);
        this.currentPage.set(page);
        
        this.transactionControllerService.findAllTransactions(page, this.pageSize)
            .subscribe({
                next: (response) => {
                    this.pageData.set(response);
                    this.loading.set(false);
                },
                error: (err) => {
                    this.error.set('Failed to load transactions');
                    this.toast.error('Failed to load transactions', 'Error');
                    this.loading.set(false);
                    console.error(err);
                }
            });
    }
    
    goToPage(page: number) {
        if (page >= 0 && page < this.totalPages()) {
            this.loadTransactions(page);
        }
    }
    
    nextPage() {
        if (this.currentPage() < this.totalPages() - 1) {
            this.goToPage(this.currentPage() + 1);
        }
    }
    
    previousPage() {
        if (this.currentPage() > 0) {
            this.goToPage(this.currentPage() - 1);
        }
    }

    firstPage() {
        if (this.totalPages() > 0 && this.currentPage() !== 0) {
            this.goToPage(0);
        }
    }

    lastPage() {
        const last = this.totalPages() - 1;
        if (last >= 0 && this.currentPage() !== last) {
            this.goToPage(last);
        }
    }
}