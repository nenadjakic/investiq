import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { PortfolioControllerService, PortfolioSummaryResponse } from '../app/core/api';
import { ToastService } from '../shared/toast.service';
import { EChartsCoreOption } from 'echarts/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
})
export class DashboardPage implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  summary: PortfolioSummaryResponse | null = null;
  isLoading = true;

  ngOnInit(): void {
    this.load();
  }


  load(): void {
    this.portfolioControllerService.getPortfolioSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toastService.error(
          'Failed to load portfolio summary. Please try again.',
          'Error'
        );        
        console.error('Error loading summary:', err);
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }
}
