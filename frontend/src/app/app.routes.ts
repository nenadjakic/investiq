import { Routes } from '@angular/router';
import { Layout } from './pages/layout/layout';

export const routes: Routes = [
  {
    path: '',
    component: Layout,
   /* children: [
      {
        path: '',
        component: Dashboard,
        data: { title: 'Dashboard' },
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        component: Dashboard,
        data: { title: 'Dashboard' },
      },
      {
        path: 'portfolio',
        component: Portfolio,
        data: { title: 'Portfolio' },
      },
      {
        path: 'asset',
        component: Asset,
        data: { title: 'Assets' },
      },
      {
        path: 'transaction',
        component: Transaction,
        data: { title: 'Transactions' },
      },
      {
        path: 'settings',
        component: Settings,
        data: { title: 'Settings' },
        loadChildren: () =>
          import('./pages/settings/settings.routes').then(
            (m) => m.settingsRoutes,
          ),
      },
    ],*/
  },
];