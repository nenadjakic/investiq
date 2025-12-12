import { Injectable } from "@angular/core";
import { Subject } from "rxjs";

export interface Toast {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
  title?: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject = new Subject<Toast>();
  toasts$ = this.toastSubject.asObservable();
  private idCounter = 0;

  show(toast: Omit<Toast, 'id'>) {
    const id = ++this.idCounter;
    this.toastSubject.next({ ...toast, id });
  }

  success(message: string, title: string = 'Success', duration: number = 3000) {
    this.show({ type: 'success', message, title, duration });
  }

  error(message: string, title: string = 'Error', duration: number = 5000) {
    this.show({ type: 'error', message, title, duration });
  }

  info(message: string, title: string = 'Info', duration: number = 3000) {
    this.show({ type: 'info', message, title, duration });
  }

  warning(message: string, title: string = 'Warning', duration: number = 4000) {
    this.show({ type: 'warning', message, title, duration });
  }
}