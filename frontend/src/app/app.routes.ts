import { Routes } from '@angular/router';
import { Dashboard } from '../dashboard/dashboard';
import { AssetPage } from '../assets/assets';
import { Transactions } from '../transactions/transactions';
import { ReferenceData } from '../reference-data/reference-data';
import { StagingTransactions } from '../staging-transactions/staging-transactions';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: Dashboard,
  },
  {
    path: 'transactions',
    component: Transactions,
    data: { title: 'Transactions' },
    pathMatch: 'full',
  },
  {
    path: 'staging-transactions',
    component: StagingTransactions,
    data: { title: 'Staging Transactions' },
    pathMatch: 'full',
  },

  {
    path: 'assets',
    component: AssetPage,
    data: { title: 'Assets' },
    pathMatch: 'full',
  },
  {
    path: 'reference-data',
    component: ReferenceData,
    data: { title: 'Reference Data' },
    pathMatch: 'full',
  },
];
