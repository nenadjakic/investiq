import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { ToastService, Toast } from "../toast.service";

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-4 right-4 z-50 space-y-2">
      @for (toast of toasts; track toast.id) {
        <div
          [ngClass]="getToastClass(toast.type)"
          class="min-w-80 max-w-md rounded-lg shadow-lg p-4 flex items-start gap-3 
                 animate-slide-in-right"
        >
        <!-- Icon -->
        <div class="shrink-0">
          @if (toast.type === 'success') {
            <svg class="w-6 h-6 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
          @if (toast.type === 'error') {
            <svg class="w-6 h-6 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
          @if (toast.type === 'warning') {
            <svg class="w-6 h-6 text-yellow-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          }
          @if (toast.type === 'info') {
            <svg class="w-6 h-6 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          }
        </div>

        <!-- Content -->
        <div class="flex-1">
          @if (toast.title) {
            <h4 class="text-sm font-semibold text-white mb-1">
              {{ toast.title }}
            </h4>
          }
          <p class="text-sm text-white opacity-90">{{ toast.message }}</p>
        </div>

        <!-- Close Button -->
        <button
          (click)="removeToast(toast.id)"
          class="shrink-0 text-white opacity-70 hover:opacity-100 transition-opacity"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes slideInRight {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .animate-slide-in-right {
      animation: slideInRight 0.3s ease-out;
    }
  `]
})
export class ToastComponent implements OnInit {
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef); 
  toasts: Toast[] = [];

  ngOnInit() {
    this.toastService.toasts$.subscribe(toast => {
      this.toasts.push(toast);
      this.cdr.detectChanges();
      
      setTimeout(() => {
        this.removeToast(toast.id);
      }, toast.duration || 3000);
    });
  }

  removeToast(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.cdr.detectChanges();
  }

  getToastClass(type: string): string {
    const baseClasses = 'border-l-4';
    switch (type) {
      case 'success':
        return `${baseClasses} bg-green-600 border-green-400`;
      case 'error':
        return `${baseClasses} bg-red-600 border-red-400`;
      case 'warning':
        return `${baseClasses} bg-yellow-600 border-yellow-400`;
      case 'info':
        return `${baseClasses} bg-blue-600 border-blue-400`;
      default:
        return `${baseClasses} bg-gray-600 border-gray-400`;
    }
  }
}