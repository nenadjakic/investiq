import { Routes } from '@angular/router';
import { DashboardPage } from '../dashboard/dashboard';
import { AssetPage } from '../asset/asset';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: DashboardPage,
  },
  {
    path: 'transactions',
    component: DashboardPage,
    data: { title: 'Transactions' },
    pathMatch: 'full',
  },

  {
    path: 'assets',
    component: AssetPage,
    data: { title: 'Assets' },
    pathMatch: 'full',
  },
];
