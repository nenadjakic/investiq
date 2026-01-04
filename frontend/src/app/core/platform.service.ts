import { Injectable, signal } from '@angular/core';

export type Platform = 'TRADING212' | 'ETORO' | 'IBKR' | 'REVOLUT';

@Injectable({ providedIn: 'root' })
export class PlatformService {
  private platformSignal = signal<Platform | null>(null);
  private refreshSignal = signal<number>(0);

  get platform() {
    return this.platformSignal;
  }

  setPlatform(option: { value: Platform } | null): void {
    this.platformSignal.set(option?.value ?? null);
  }

  getPlatformValue(): Platform | undefined {
    return this.platformSignal() ?? undefined;
  }

  get refresh() {
    return this.refreshSignal;
  }
}
