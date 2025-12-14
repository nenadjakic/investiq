import { CommonModule } from "@angular/common";
import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { finalize } from "rxjs/operators";
import {
  PortfolioControllerService,
  TopBottomPerformersResponse,
  TransactionControllerService,
  TransactionResponse,
} from "../../app/core/api";
import { ToastService } from "../../shared/toast.service";

@Component({
  selector: 'app-insights',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './insights.html',
})
export class Insights implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private transactionControllerService = inject(TransactionControllerService);
  private toast = inject(ToastService);

  recentTransactions = signal<TransactionResponse[]>([]);
  txLoading = signal<boolean>(false);
  txError = signal<string | null>(null);

  performers = signal<TopBottomPerformersResponse | null>(null);
  perfLoading = signal<boolean>(false);
  perfError = signal<string | null>(null);

  topPerformers = computed(() => this.performers()?.top ?? []);
  bottomPerformers = computed(() => this.performers()?.bottom ?? []);

  ngOnInit(): void {
    this.loadRecentTransactions();
    this.loadPerformers();
  }

  loadRecentTransactions(limit: number = 5): void {
    this.txLoading.set(true);
    this.txError.set(null);
    this.transactionControllerService
      .findLastTransactions(limit)
      .pipe(finalize(() => this.txLoading.set(false)))
      .subscribe({
        next: (data) => {
          this.recentTransactions.set(data ?? []);
        },
        error: (err) => {
          this.txError.set('Failed to load recent transactions');
          this.toast.error('Failed to load recent transactions', 'Error');
          console.error('Error loading recent transactions:', err);
        },
      });
  }

  loadPerformers(): void {
    this.perfLoading.set(true);
    this.perfError.set(null);
    this.portfolioControllerService
      .getTopBottomPerformers()
      .pipe(finalize(() => this.perfLoading.set(false)))
      .subscribe({
        next: (data) => {
          this.performers.set(data ?? { top: [], bottom: [] });
        },
        error: (err) => {
          this.perfError.set('Failed to load performers');
          this.toast.error('Failed to load performers', 'Error');
          console.error('Error loading performers:', err);
        },
      });
  }
}
