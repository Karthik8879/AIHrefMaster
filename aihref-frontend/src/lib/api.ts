import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface LookupRequest {
  url: string;
}

export interface AnalyseResponse {
  lookupId: string;
  message: string;
}

export interface CountryData {
  country: string;
  share: number;
  visits: number;
}

export interface DeviceData {
  device: string;
  share: number;
}

export interface ReferrerData {
  site: string;
  share: number;
}

export interface TrafficData {
  visits: number;
  rank: number;
  bounce: number;
  avgDuration: number;
  countries: CountryData[];
  devices: DeviceData[];
  referrers: ReferrerData[];
  searchShare: number;
  socialShare: number;
}

export interface WebVitalsData {
  lcp: number;
  fid: number;
  cls: number;
  score: number;
  screenshotB64: string;
}

export interface SeoData {
  title: string;
  description: string;
  h1: string;
  wordCount: number;
  readability: number;
  canonical: boolean;
}

export interface TechData {
  ipv6: boolean;
  http2: boolean;
  sslDaysLeft: number;
  securityGrade: string;
}

export interface LiveData {
  trafficIndex: number;
  lastUpdated: string;
}

export interface ThreatData {
  safeBrowsing: boolean;
}

export interface LookupResponse {
  id: string;
  url: string;
  requestedAt: string;
  expiresAt: string;
  traffic: TrafficData;
  webVitals: WebVitalsData;
  seo: SeoData;
  tech: TechData;
  live: LiveData;
  threat: ThreatData;
}

export const apiService = {
  async analyseUrl(url: string): Promise<AnalyseResponse> {
    const response = await api.post<AnalyseResponse>('/analyse', { url });
    return response.data;
  },

  async getLookup(lookupId: string): Promise<LookupResponse> {
    const response = await api.get<LookupResponse>(`/lookup/${lookupId}`);
    return response.data;
  },

  async exportLookup(lookupId: string, type: 'csv' | 'json' = 'json'): Promise<Blob> {
    const response = await api.get(`/export/${lookupId}?type=${type}`, {
      responseType: 'blob',
    });
    return response.data;
  },

  async healthCheck(): Promise<any> {
    const response = await api.get('/health');
    return response.data;
  },
};
