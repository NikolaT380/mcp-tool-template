import { useState } from 'react';
import type { CorpusStats } from '../api/types/stats.ts';

/**
 * TODO(student): Load the corpus statistics (statsApi.get) for the home
 * dashboard, with loading/error state. This is the same data the provided
 * `corpus_stats` MCP tool returns.
 */
const useStats = () => {
  const [stats] = useState<CorpusStats | null>(null);
  const [loading] = useState<boolean>(false);

  return { stats, loading };
};

export default useStats;
