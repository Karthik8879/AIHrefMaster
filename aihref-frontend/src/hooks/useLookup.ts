import { useState, useEffect } from 'react';
import useSWR from 'swr';
import { apiService, LookupResponse } from '@/lib/api';

export function useLookup(lookupId: string | null) {
  const { data, error, isLoading } = useSWR<LookupResponse>(
    lookupId ? `/lookup/${lookupId}` : null,
    () => lookupId ? apiService.getLookup(lookupId) : null,
    {
      refreshInterval: 5000, // Refresh every 5 seconds while loading
      revalidateOnFocus: false,
    }
  );

  return {
    data,
    error,
    isLoading,
  };
}

export function useAnalyse() {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const analyseUrl = async (url: string): Promise<string | null> => {
    setIsAnalyzing(true);
    setError(null);

    try {
      const response = await apiService.analyseUrl(url);
      return response.lookupId;
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to analyze URL');
      return null;
    } finally {
      setIsAnalyzing(false);
    }
  };

  return {
    analyseUrl,
    isAnalyzing,
    error,
  };
}
