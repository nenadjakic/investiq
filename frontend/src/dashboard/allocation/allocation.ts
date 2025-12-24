import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import {
  IndustrySectorValueResponse,
  PortfolioChartResponse,
  PortfolioControllerService,
  PortfolioSummaryResponse,
  CountryValueResponse,
  CurrencyValueResponse,
} from '../../app/core/api';
import { CommonModule } from '@angular/common';
import { EChartsCoreOption, registerMap } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';

@Component({
  selector: 'app-allocation',
  standalone: true,
  templateUrl: './allocation.html',
  imports: [CommonModule, NgxEchartsDirective],
})
export class Allocation implements OnInit {
  private portfolioControllerService = inject(PortfolioControllerService);

  sectorData = signal<IndustrySectorValueResponse[]>([]);
  countryData = signal<CountryValueResponse[]>([]);
  currencyData = signal<CurrencyValueResponse[]>([]);
  assetTypeData = signal<Array<{ assetType?: string; valueEur?: number }>>([]);
  sectorOption = signal<EChartsCoreOption>({});
  sectorIndustryOption = signal<EChartsCoreOption>({});
  countryOption = signal<EChartsCoreOption>({});
  countryMapOption = signal<EChartsCoreOption>({});
  continentMapOption = signal<EChartsCoreOption>({});
  currencyOption = signal<EChartsCoreOption>({});
  assetTypeOption = signal<EChartsCoreOption>({});
  dataError = signal(false);
  private currencyFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 0,
  });

  private worldMapRegistered = false;
  private worldMapLoading = false;
  private worldMapReadyCallbacks: Array<() => void> = [];
  private static readonly WORLD_MAP_NAME = 'world';
  private static readonly WORLD_GEOJSON_URL = '/maps/world.json';

  private continentMapRegistered = false;
  private continentMapLoading = false;
  private continentMapReadyCallbacks: Array<() => void> = [];
  private static readonly CONTINENT_MAP_NAME = 'continents';
  private static readonly CONTINENT_GEOJSON_URL = '/maps/continents.json';

  private countryNameMap: Record<string, string> = {
    'Korea, Republic of': 'South Korea',
    "Korea, Democratic People's Republic of": 'North Korea',
    'Iran, Islamic Republic of': 'Iran',
    Vietnam: 'Viet Nam',
    Syria: 'Syrian Arab Republic',
    Tanzania: 'Tanzania, United Republic of',
    Venezuela: 'Venezuela, Bolivarian Republic of',
    Macedonia: 'Macedonia',
    Congo: 'Congo, Republic of the',
    'Democratic Republic of the Congo': 'Congo, Democratic Republic of the',
    'Taiwan, Province of China': 'Taiwan',
    'United States': 'United States of America'
  };

  private normalizeCountryName(apiName: string): string {
    return this.countryNameMap[apiName] || apiName;
  }

  private countryToContinentMap: Record<string, string> = {
    // Europe
    Austria: 'Europe',
    Belgium: 'Europe',
    Bulgaria: 'Europe',
    Croatia: 'Europe',
    Cyprus: 'Europe',
    'Czech Republic': 'Europe',
    Denmark: 'Europe',
    Estonia: 'Europe',
    Finland: 'Europe',
    France: 'Europe',
    Germany: 'Europe',
    Greece: 'Europe',
    Hungary: 'Europe',
    Ireland: 'Europe',
    Italy: 'Europe',
    Latvia: 'Europe',
    Lithuania: 'Europe',
    Luxembourg: 'Europe',
    Malta: 'Europe',
    Netherlands: 'Europe',
    Poland: 'Europe',
    Portugal: 'Europe',
    Romania: 'Europe',
    Slovakia: 'Europe',
    Slovenia: 'Europe',
    Spain: 'Europe',
    Sweden: 'Europe',
    'United Kingdom': 'Europe',
    Switzerland: 'Europe',
    Norway: 'Europe',
    Iceland: 'Europe',
    Serbia: 'Europe',
    Albania: 'Europe',
    'Bosnia and Herzegovina': 'Europe',
    Macedonia: 'Europe',
    Montenegro: 'Europe',
    Kosovo: 'Europe',
    Ukraine: 'Europe',
    Belarus: 'Europe',
    Moldova: 'Europe',
    Russia: 'Europe',
    Jersey: 'Europe',
    // Asia
    China: 'Asia',
    Japan: 'Asia',
    'South Korea': 'Asia',
    'North Korea': 'Asia',
    India: 'Asia',
    Indonesia: 'Asia',
    Thailand: 'Asia',
    Malaysia: 'Asia',
    Singapore: 'Asia',
    Philippines: 'Asia',
    'Viet Nam': 'Asia',
    Bangladesh: 'Asia',
    Pakistan: 'Asia',
    Taiwan: 'Asia',
    'Hong Kong': 'Asia',
    Israel: 'Asia',
    Turkey: 'Asia',
    'Saudi Arabia': 'Asia',
    'United Arab Emirates': 'Asia',
    Iran: 'Asia',
    Iraq: 'Asia',
    Qatar: 'Asia',
    Kuwait: 'Asia',
    Lebanon: 'Asia',
    Jordan: 'Asia',
    Syria: 'Asia',
    Yemen: 'Asia',
    Oman: 'Asia',
    Bahrain: 'Asia',
    Afghanistan: 'Asia',
    Kazakhstan: 'Asia',
    Uzbekistan: 'Asia',
    Myanmar: 'Asia',
    Cambodia: 'Asia',
    Laos: 'Asia',
    Mongolia: 'Asia',
    Nepal: 'Asia',
    'Sri Lanka': 'Asia',
    'Taiwan, Province of China': 'Asia',
    // North America
    'United States': 'North America',
    'United States of America': 'North America',
    Canada: 'North America',
    Mexico: 'North America',
    Guatemala: 'North America',
    Honduras: 'North America',
    'El Salvador': 'North America',
    Nicaragua: 'North America',
    'Costa Rica': 'North America',
    Panama: 'North America',
    Cuba: 'North America',
    Jamaica: 'North America',
    Haiti: 'North America',
    'Dominican Republic': 'North America',
    Bahamas: 'North America',
    'Trinidad and Tobago': 'North America',
    // South America
    Brazil: 'South America',
    Argentina: 'South America',
    Chile: 'South America',
    Colombia: 'South America',
    Peru: 'South America',
    Venezuela: 'South America',
    Ecuador: 'South America',
    Bolivia: 'South America',
    Paraguay: 'South America',
    Uruguay: 'South America',
    Guyana: 'South America',
    Suriname: 'South America',
    // Africa
    'South Africa': 'Africa',
    Nigeria: 'Africa',
    Egypt: 'Africa',
    Kenya: 'Africa',
    Morocco: 'Africa',
    Algeria: 'Africa',
    Tunisia: 'Africa',
    Ghana: 'Africa',
    Ethiopia: 'Africa',
    Tanzania: 'Africa',
    Uganda: 'Africa',
    Angola: 'Africa',
    Mozambique: 'Africa',
    Zimbabwe: 'Africa',
    Zambia: 'Africa',
    Botswana: 'Africa',
    Namibia: 'Africa',
    Libya: 'Africa',
    Sudan: 'Africa',
    Senegal: 'Africa',
    'Ivory Coast': 'Africa',
    Cameroon: 'Africa',
    Congo: 'Africa',
    'Congo, Republic of the': 'Africa',
    'Congo, Democratic Republic of the': 'Africa',
    'Democratic Republic of the Congo': 'Africa',
    Rwanda: 'Africa',
    Burundi: 'Africa',
    // Australia
    Australia: 'Australia',
    'New Zealand': 'Australia',
    'Papua New Guinea': 'Australia',
    Fiji: 'Australia',
    'Solomon Islands': 'Australia',
    Vanuatu: 'Australia',
  };

  private getContinent(country: string): string {
    const normalizedCountry = this.normalizeCountryName(country);
    // Try direct lookup using normalized name first, then the original API name
    if (this.countryToContinentMap[normalizedCountry]) {
      return this.countryToContinentMap[normalizedCountry];
    }
    if (this.countryToContinentMap[country]) {
      return this.countryToContinentMap[country];
    }

    // Case-insensitive key match against both normalized and original names
    const found = Object.keys(this.countryToContinentMap).find(
      (key) =>
        key.toLowerCase() === normalizedCountry.toLowerCase() || key.toLowerCase() === country.toLowerCase()
    );
    if (found) {
      return this.countryToContinentMap[found];
    }

    // Try cleaned variants (remove common suffixes) for both normalized and original
    const cleanup = (name: string) =>
      name.replace(/,? (Republic|Federation|Kingdom|State|States|Islands|and|of|the)$/gi, '').trim();

    const cleanedNormalized = cleanup(normalizedCountry);
    const cleanedOriginal = cleanup(country);

    if (this.countryToContinentMap[cleanedNormalized]) {
      return this.countryToContinentMap[cleanedNormalized];
    }
    if (this.countryToContinentMap[cleanedOriginal]) {
      return this.countryToContinentMap[cleanedOriginal];
    }

    const foundClean = Object.keys(this.countryToContinentMap).find(
      (key) =>
        key.toLowerCase() === cleanedNormalized.toLowerCase() || key.toLowerCase() === cleanedOriginal.toLowerCase()
    );
    if (foundClean) {
      return this.countryToContinentMap[foundClean];
    }

    return 'Other';
  }

  constructor() {
    effect(() => {
      const data = this.sectorData();
      if (data && data.length > 0) {
        const groupedBySector = data.reduce((acc, item) => {
          const sector = item.sector || 'Unknown';
          if (!acc[sector]) {
            acc[sector] = [];
          }
          acc[sector].push(item);
          return acc;
        }, {} as Record<string, IndustrySectorValueResponse[]>);

        const sunburstData = Object.entries(groupedBySector).map(([sector, industries]) => ({
          name: sector,
          children: industries.map((industry) => ({
            name: industry.industry || 'Unknown',
            value: industry.valueEur || 0,
          })),
        }));

        const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
        const sectorTotals: Record<string, number> = Object.entries(groupedBySector).reduce(
          (acc, [sector, items]) => {
            acc[sector] = items.reduce((s, it) => s + (it.valueEur || 0), 0);
            return acc;
          },
          {} as Record<string, number>
        );

        const pieData = Object.entries(sectorTotals)
          .map(([name, value]) => ({ name, value }))
          .sort((a, b) => b.value - a.value);

        this.sectorOption.set({
          legend: {
            orient: 'horizontal',
            bottom: 30,
            type: 'scroll',
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const pct = totalValue > 0 ? (val / totalValue) * 100 : 0;
              return `${params.name}<br/>€ ${val.toFixed(2)}<br/>${pct.toFixed(2)} %`;
            },
          },
          series: [
            {
              name: 'Sector Allocation',
              type: 'pie',
              radius: ['60%', '90%'],
              startAngle: 180,
              endAngle: 360,
              label: {
                show: false,
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: '{b}: {d}%',
                },
              },
              data: pieData,
            },
          ],
        });

        this.sectorIndustryOption.set({
          series: [
            {
              type: 'treemap',
              data: sunburstData,
              visibleMin: 300,
              label: {
                show: true,
                formatter: '{b}',
              },
              upperLabel: {
                show: true,
                height: 30,
              },
            },
          ],
          tooltip: {
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const name = params.name as string;
              const totalPct = totalValue > 0 ? (val / totalValue) * 100 : 0;

              const path = params.treePathInfo as Array<{ name: string }> | undefined;
              const parentName = path && path.length >= 2 ? path[path.length - 2].name : undefined;
              const parentTotal = parentName ? sectorTotals[parentName] ?? 0 : 0;
              const sectorPct = parentTotal > 0 ? (val / parentTotal) * 100 : undefined;

              const lines: string[] = [];
              lines.push(`${name}`);
              lines.push(`€ ${val.toFixed(2)}`);
              lines.push(`Total: ${totalPct.toFixed(2)} %`);
              if (sectorPct !== undefined) {
                lines.push(`Of sector ${parentName}: ${sectorPct.toFixed(2)} %`);
              }
              return lines.join('<br/>');
            },
          },
        });
      }
    });

    effect(() => {
      const data = this.countryData();
      if (data && data.length > 0) {
        const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
        const pieData = data
          .map((item) => ({
            name: item.country || 'Unknown',
            value: item.valueEur || 0,
          }))
          .sort((a, b) => b.value - a.value);

        this.countryOption.set({
          legend: {
            orient: 'horizontal',
            bottom: 30,
            type: 'scroll',
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const pct = totalValue > 0 ? (val / totalValue) * 100 : 0;
              return `${params.name}<br/>€ ${val.toFixed(2)}<br/>${pct.toFixed(2)} %`;
            },
          },
          series: [
            {
              name: 'Country Allocation',
              type: 'pie',
              radius: ['60%', '90%'],
              startAngle: 180,
              endAngle: 360,
              label: {
                show: false,
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: '{b}: {d}%',
                },
              },
              data: pieData,
            },
          ],
        });

        this.ensureWorldMapRegistered(() => this.updateCountryMapOption(data));
        this.ensureContinentMapRegistered(() => this.updateContinentMapOption(data));
      } else {
        this.countryMapOption.set({});
        this.continentMapOption.set({});
      }
    });

    effect(() => {
      const data = this.currencyData();
      if (data && data.length > 0) {
        const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
        const pieData = data
          .map((item) => ({
            name: item.currency || 'Unknown',
            value: item.valueEur || 0,
          }))
          .sort((a, b) => b.value - a.value);

        this.currencyOption.set({
          legend: {
            orient: 'horizontal',
            bottom: 30,
            type: 'scroll',
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const pct = totalValue > 0 ? (val / totalValue) * 100 : 0;
              return `${params.name}<br/>€ ${val.toFixed(2)}<br/>${pct.toFixed(2)} %`;
            },
          },
          series: [
            {
              name: 'Currency Exposure',
              type: 'pie',
              radius: ['60%', '90%'],
              startAngle: 180,
              endAngle: 360,
              label: {
                show: false,
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: '{b}: {d}%',
                },
              },
              data: pieData,
            },
          ],
        });
      }
    });

    effect(() => {
      const data = this.assetTypeData();
      if (data && data.length > 0) {
        const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
        const pieData = data
          .map((item) => ({
            name: item.assetType || 'Unknown',
            value: item.valueEur || 0,
          }))
          .sort((a, b) => b.value - a.value);

        this.assetTypeOption.set({
          legend: {
            orient: 'horizontal',
            bottom: 30,
            type: 'scroll',
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const val = Number(params.value) || 0;
              const pct = totalValue > 0 ? (val / totalValue) * 100 : 0;
              return `${params.name}<br/>€ ${val.toFixed(2)}<br/>${pct.toFixed(2)} %`;
            },
          },
          series: [
            {
              name: 'Asset Type',
              type: 'pie',
              radius: ['60%', '90%'],
              startAngle: 180,
              endAngle: 360,
              label: {
                show: false,
              },
              emphasis: {
                label: {
                  show: true,
                  formatter: '{b}: {d}%',
                },
              },
              data: pieData,
            },
          ],
        });
      }
    });
  }

  ngOnInit(): void {
    this.loadSectorAllocation();
  }

  loadSectorAllocation(): void {
    this.dataError.set(false);

    this.portfolioControllerService.getCombinedAllocation().subscribe({
      next: (data) => {
        this.sectorData.set(Array.isArray(data.byIndustrySector) ? data.byIndustrySector : []);
        this.countryData.set(Array.isArray(data.byCountry) ? data.byCountry : []);
        this.currencyData.set(Array.isArray(data.byCurrency) ? data.byCurrency : []);
        const byAssetType = (data as any)?.byAssetType;
        this.assetTypeData.set(Array.isArray(byAssetType) ? byAssetType : []);
      },
      error: (err) => {
        this.dataError.set(true);
        this.sectorData.set([]);
        this.countryData.set([]);
        this.countryMapOption.set({});
        this.continentMapOption.set({});
        this.currencyData.set([]);
        this.assetTypeData.set([]);
        console.error('Error loading allocation data:', err);
      },
    });
  }

  private ensureWorldMapRegistered(onReady?: () => void): void {
    if (this.worldMapRegistered) {
      onReady?.();
      return;
    }

    if (onReady) {
      this.worldMapReadyCallbacks.push(onReady);
    }

    if (this.worldMapLoading) {
      return;
    }

    this.worldMapLoading = true;
    fetch(Allocation.WORLD_GEOJSON_URL)
      .then((res) => res.json())
      .then((geoJson) => {
        registerMap(Allocation.WORLD_MAP_NAME, geoJson as any);
        this.worldMapRegistered = true;
        const callbacks = [...this.worldMapReadyCallbacks];
        this.worldMapReadyCallbacks = [];
        callbacks.forEach((cb) => cb());
      })
      .catch((err) => {
        console.error('Failed to load world GeoJSON for allocation map', err);
      })
      .finally(() => {
        this.worldMapLoading = false;
      });
  }

  private updateCountryMapOption(data: CountryValueResponse[]): void {
    if (!data || !data.length || !this.worldMapRegistered) {
      return;
    }

    const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
    const seriesData = data
      .map((item) => {
        const amount = item.valueEur || 0;
        const pct = totalValue > 0 ? (amount / totalValue) * 100 : 0;
        return {
          name: this.normalizeCountryName(item.country || 'Unknown'),
          value: pct,
          amount,
        };
      })
      .sort((a, b) => b.amount - a.amount);

    const maxVal = 100;

    this.countryMapOption.set({
      tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          const pct = Number(params.value) || 0;
          const amount = Number(params.data?.amount) || 0;
          return `${params.name}<br/>${pct.toFixed(2)} %<br/>${this.currencyFormatter.format(
            amount
          )}`;
        },
      },
      visualMap: {
        min: 0,
        max: maxVal || 100,
        left: 'left',
        text: ['100%', '0%'],
        inRange: {
          color: [
            '#e0f7ff',
            '#b0e0f0',
            '#87ceeb',
            '#7fffd4',
            '#90ee90',
            '#adff2f',
            '#ffff00',
            '#ffd700',
            '#ffb347',
            '#ffa500',
            '#ff8c00',
            '#ff6347',
            '#ff4500',
            '#ff0000',
          ],
        },
        outOfRange: { color: '#ffffff' },
        type: 'continuous',
        orient: 'vertical',
        calculable: true,
        itemWidth: 18,
        itemHeight: 180,
      },
      series: [
        {
          type: 'map',
          map: Allocation.WORLD_MAP_NAME,
          roam: true,
          name: 'Country Allocation',
          itemStyle: {
            areaColor: '#ffffff',
            borderColor: '#d1d5db',
          },
          emphasis: { label: { show: false } },
          data: seriesData,
        },
      ],
    });
  }

  private ensureContinentMapRegistered(onReady?: () => void): void {
    if (this.continentMapRegistered) {
      onReady?.();
      return;
    }

    if (onReady) {
      this.continentMapReadyCallbacks.push(onReady);
    }

    if (this.continentMapLoading) {
      return;
    }

    this.continentMapLoading = true;
    fetch(Allocation.CONTINENT_GEOJSON_URL)
      .then((res) => res.json())
      .then((geoJson) => {
        // Patch: Copy CONTINENT property to name for each feature
        if (geoJson && geoJson.features) {
          geoJson.features.forEach((feature: any) => {
            if (feature.properties && feature.properties.CONTINENT) {
              feature.properties.name = feature.properties.CONTINENT;
            }
          });
        }
        registerMap(Allocation.CONTINENT_MAP_NAME, geoJson as any);
        this.continentMapRegistered = true;
        const callbacks = [...this.continentMapReadyCallbacks];
        this.continentMapReadyCallbacks = [];
        callbacks.forEach((cb) => cb());
      })
      .catch((err) => {
        console.error('Failed to load continent GeoJSON for allocation map', err);
      })
      .finally(() => {
        this.continentMapLoading = false;
      });
  }

  private updateContinentMapOption(data: CountryValueResponse[]): void {
    if (!data || !data.length || !this.continentMapRegistered) {
      return;
    }

    const totalValue = data.reduce((sum, i) => sum + (i.valueEur || 0), 0);
    const continentTotals: Record<string, number> = {};
    const unmappedCountries: string[] = [];
    data.forEach((item) => {
      const country = item.country || '';
      const continent = this.getContinent(country);
      const amount = item.valueEur || 0;
      if (!continent || continent === 'Other') {
        unmappedCountries.push(country);
      }
      continentTotals[continent] = (continentTotals[continent] || 0) + amount;
    });
    if (unmappedCountries.length > 0) {
      const unique = Array.from(new Set(unmappedCountries.filter(Boolean)));
      if (unique.length > 0) {
        // eslint-disable-next-line no-console
        console.warn('Countries not mapped to continent:', unique);
      }
    }

    const seriesData = Object.entries(continentTotals)
      .map(([name, amount]) => {
        const pct = totalValue > 0 ? (amount / totalValue) * 100 : 0;
        return { name, value: pct, amount };
      })
      .sort((a, b) => b.amount - a.amount);

    const maxVal = 100;

    this.continentMapOption.set({
      tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          const pct = Number(params.value) || 0;
          const amount = Number(params.data?.amount) || 0;
          return `${params.name}<br/>${pct.toFixed(2)} %<br/>${this.currencyFormatter.format(
            amount
          )}`;
        },
      },
      visualMap: {
        min: 0,
        max: maxVal || 100,
        left: 'left',
        text: ['100%', '0%'],
        inRange: {
          color: [
            '#e0f7ff',
            '#b0e0f0',
            '#87ceeb',
            '#7fffd4',
            '#90ee90',
            '#adff2f',
            '#ffff00',
            '#ffd700',
            '#ffb347',
            '#ffa500',
            '#ff8c00',
            '#ff6347',
            '#ff4500',
            '#ff0000',
          ],
        },
        outOfRange: { color: '#ffffff' },
        type: 'continuous',
        orient: 'vertical',
        calculable: true,
        itemWidth: 18,
        itemHeight: 180,
      },
      series: [
        {
          type: 'map',
          map: Allocation.CONTINENT_MAP_NAME,
          roam: true,
          name: 'Continent Allocation',
          itemStyle: {
            areaColor: '#ffffff',
            borderColor: '#d1d5db',
          },
          emphasis: { label: { show: true } },
          data: seriesData,
        },
      ],
    });
  }
}
