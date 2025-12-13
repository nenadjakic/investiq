# InvestIQ - Frontend (Angular) - concrete & UI-focused

This frontend is an Angular 21 application tailored to this repository. This README explains exactly what users see in the UI, how the frontend ties into the backend, and how to work with the concrete frontend tooling already present.

## Tech stack
- **Framework**: Angular 21
- **Charts**: ECharts via ngx-echarts for interactive charts
- **Styling**: Tailwind/PostCSS

## What the frontend shows
The application UI is a management dashboard for portfolios with these concrete views and controls:

1. Dashboard
   - Portfolio summary card
   - Allocation breakdown (interactive pie/donut chart) showing weights by sectors, country, asset class and currency
   - Performance chart (interactive time-series using ECharts)
   - Top movers table (best/worst performers)
   - Table of holdings with columns: ticker, name, quantity, avg cost, current price, market value, unrealized P/L, allocation %
   
2. Transactions & Import
   - Transaction list with filtering by date, asset, account