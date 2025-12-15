import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () => import('../dashboard/dashboard').then(m => m.Dashboard),
  },
  {
    path: 'transactions',
    loadComponent: () => import('../transactions/transactions').then(m => m.Transactions),
    data: { title: 'Transactions' },
    pathMatch: 'full',
  },
  {
    path: 'staging-transactions',
    loadComponent: () => import('../staging-transactions/staging-transactions').then(m => m.StagingTransactions),
    data: { title: 'Staging Transactions' },
    pathMatch: 'full',
  },

  {
    path: 'assets',
    loadComponent: () => import('../assets/assets').then(m => m.AssetPage),
    data: { title: 'Assets' },
    pathMatch: 'full',
  },
  {
    path: 'reference-data',
    loadComponent: () => import('../reference-data/reference-data').then(m => m.ReferenceData),
    data: { title: 'Reference Data' },
    pathMatch: 'full',
  },
];
