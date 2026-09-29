import { useCallback, useEffect, useState } from 'react';
import resourceApi from '../api/resourceApi.ts';
import type { PageResponse, ResourceFilter, ResourceResponse } from '../api/types/resource.ts';
import useSnackbar from './useSnackbar.ts';

/**
 * _TODO(student): Load a page of resources (resourceApi.findAll) for the
 * ResourcesPage, re-fetching whenever the filter or page changes, with
 * loading/error state, a delete action (resourceApi.delete) and an analyze
 * action (resourceApi.analyze). Mirror the searchRunsProvider pattern.
 */
const useResources = (filter: ResourceFilter, page: number, size: number) => {
  const [resources, setResources] = useState<PageResponse<ResourceResponse> | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const { showSnackbar } = useSnackbar();

  const fetchResources = useCallback(async () => {
    setLoading(true);
    try {
      const response = await resourceApi.findAll(filter, page, size);
      setResources(response.data);
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || 'Failed to load resources.';
      showSnackbar(message, 'error');
    } finally {
      setLoading(false);
    }
  }, [filter, page, size, showSnackbar]);

  useEffect(() => {
    fetchResources();
  }, [fetchResources]);

  const onDelete = useCallback(
      async (id: number) => {
        try {
          await resourceApi.delete(String(id));
          showSnackbar('Resource successfully deleted.', 'success');
          await fetchResources();
        } catch (err: unknown) {
          const error = err as { response?: { data?: { message?: string } } };
          const message = error.response?.data?.message || 'Failed to delete resource.';
          showSnackbar(message, 'error');
        }
      },
      [fetchResources, showSnackbar]
  );

  const onAnalyze = useCallback(
      async (id: number) => {
        try {
          await resourceApi.analyze(String(id));
          showSnackbar('Resource successfully analyzed.', 'success');
          await fetchResources();
        } catch (err: unknown) {
          const error = err as { response?: { data?: { message?: string } } };
          const message = error.response?.data?.message || 'Failed to analyze resource.';
          showSnackbar(message, 'error');
        }
      },
      [fetchResources, showSnackbar]
  );

  return { resources, loading, onDelete, onAnalyze };
};

export default useResources;
