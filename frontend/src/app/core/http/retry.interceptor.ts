import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { retry } from 'rxjs/operators';
import { timer, throwError } from 'rxjs';

const RETRY_COUNT = 3;
const BACKOFF_MS = 300;

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  const isIdempotent = req.method === 'GET' || req.method === 'HEAD' || req.method === 'OPTIONS';
  if (!isIdempotent) {
    return next(req);
  }

  return next(req).pipe(
    retry({
      count: RETRY_COUNT,
      delay: (error, retryIndex) => {
        if (!(error instanceof HttpErrorResponse)) {
          return throwError(() => error);
        }

        const status = error.status;
        const retryable = status === 0 || (status >= 500 && status < 600);
        if (!retryable) {
          return throwError(() => error);
        }

        const backoff = (retryIndex + 1) * BACKOFF_MS;
        return timer(backoff);
      },
    })
  );
};
