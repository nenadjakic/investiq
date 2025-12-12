import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import * as echarts from 'echarts/core';
import { BarChart, LineChart, PieChart, SunburstChart } from 'echarts/charts';
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

import { routes } from './app.routes';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideEchartsCore } from 'ngx-echarts';

echarts.use([
  BarChart,
  GridComponent,
  CanvasRenderer,
  LineChart,
  TooltipComponent,
  LegendComponent,
  SunburstChart,
  PieChart,
]);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withFetch()),
    provideEchartsCore({ echarts }),
  ],
};
