import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import * as echarts from 'echarts/core';
import { BarChart, LineChart, MapChart, PieChart, SunburstChart, TreemapChart } from 'echarts/charts';
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
  VisualMapComponent,
  ToolboxComponent,
  TitleComponent
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

import { routes } from './app.routes';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideEchartsCore } from 'ngx-echarts';
import { cacheInterceptor } from './core/http/cache.interceptor';
import { retryInterceptor } from './core/http/retry.interceptor';

echarts.use([
  BarChart,
  GridComponent,
  CanvasRenderer,
  LineChart,
  TooltipComponent,
  LegendComponent,
  MapChart,
  VisualMapComponent,
  ToolboxComponent,
  SunburstChart,
  PieChart,
  TreemapChart,
  TitleComponent
]);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([cacheInterceptor, retryInterceptor])),
    provideEchartsCore({ echarts }),
  ],
};
