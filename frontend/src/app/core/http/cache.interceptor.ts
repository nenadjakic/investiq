import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { of, throwError, Observable } from 'rxjs';
import { catchError, tap, finalize, shareReplay } from 'rxjs/operators';

interface CacheEntry {
  expiry: number;
  response: HttpResponse<unknown>;
}

const DEFAULT_CACHE_TTL_MS = 12 * 60 * 60 * 1000; // 12 hours
const cacheStore = new Map<string, CacheEntry>();
const inFlight = new Map<string, Observable<any>>();

const isDashboardRequest = (url: string): boolean => url.includes('/portfolio/');

function makeCacheKey(req: any): string {
  // include method and full urlWithParams so different query params create different keys
  return `${req.method}:${req.urlWithParams}`;
}

export const cacheInterceptor: HttpInterceptorFn = (req, next) => {
  // Only cache GET dashboard requests by default
  if (req.method !== 'GET' || !isDashboardRequest(req.url)) {
    return next(req);
  }

  // Transfer cache flag from generated clients (default true). If explicitly false, bypass cache.
  const transferCacheFlag = (req as any).transferCache;
  const transferCache = transferCacheFlag === undefined ? true : Boolean(transferCacheFlag);
  if (!transferCache) {
    return next(req);
  }

  // Honor request header to bypass cache
  const bypass = req.headers?.get?.('x-cache-bypass') === '1' || (req.headers?.get?.('cache-control') || '').includes('no-cache');
  if (bypass) {
    // ensure we also remove stale cache for this key
    try {
      cacheStore.delete(makeCacheKey(req));
    } catch (e) {}
    return next(req);
  }

  const key = makeCacheKey(req);
  const now = Date.now();
  const cached = cacheStore.get(key);

  if (cached && cached.expiry > now) {
    // Return a cloned HttpResponse so downstream consumers can use it safely
    return of(cached.response.clone());
  }

  // If request already in flight, return the same observable
  if (inFlight.has(key)) {
    return inFlight.get(key)!;
  }

  const request$ = next(req).pipe(
    tap((event) => {
      if (event instanceof HttpResponse) {
        try {
          cacheStore.set(key, { response: event.clone(), expiry: now + DEFAULT_CACHE_TTL_MS });
        } catch (e) {
          // if clone fails, skip caching
        }
      }
    }),
    catchError((err) => {
      cacheStore.delete(key);
      return throwError(() => err);
    }),
    finalize(() => {
      inFlight.delete(key);
    }),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  inFlight.set(key, request$);
  return request$;
};
