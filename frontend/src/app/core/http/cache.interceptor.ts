import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

interface CacheEntry {
  expiry: number;
  response: HttpResponse<unknown>;
}

const CACHE_TTL_MS = 12 * 60 * 60 * 1000; // 12 hours
const cacheStore = new Map<string, CacheEntry>();

const isDashboardRequest = (url: string): boolean => url.includes('/portfolio/');

export const cacheInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.method !== 'GET' || !isDashboardRequest(req.url)) {
    return next(req);
  }

  const key = req.urlWithParams;
  const now = Date.now();
  const cached = cacheStore.get(key);

  if (cached && cached.expiry > now) {
    return of(cached.response.clone());
  }

  cacheStore.delete(key);

  return next(req).pipe(
    tap((event) => {
      if (event instanceof HttpResponse) {
        cacheStore.set(key, { response: event, expiry: now + CACHE_TTL_MS });
      }
    }),
    catchError((err) => {
      cacheStore.delete(key);
      return throwError(() => err);
    })
  );
};
