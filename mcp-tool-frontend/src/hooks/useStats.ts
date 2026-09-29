import { useCallback, useEffect, useState } from 'react';
import statsApi from '../api/statsApi.ts';
import type { CorpusStats } from '../api/types/stats.ts';
import useSnackbar from './useSnackbar.ts';

/**
 * _TODO(student): Load the corpus statistics (statsApi.get) for the home
 * dashboard, with loading/error state. This is the same data the provided
 * `corpus_stats` MCP tool returns.
 */
const useStats = () => {
  const [stats, setStats] = useState<CorpusStats | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const { showSnackbar } = useSnackbar();

  const fetchStats = useCallback(async () => {
    setLoading(true);
    try {
      const response = await statsApi.get();
      setStats(response.data);
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      showSnackbar(error.response?.data?.message || 'Failed to load corpus statistics.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showSnackbar]);

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  return { stats, loading };
};

export default useStats;
