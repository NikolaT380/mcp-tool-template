import { useState } from 'react';
import type { PageResponse, ResourceFilter, ResourceResponse } from '../api/types/resource.ts';

/**
 * TODO(student): Load a page of resources (resourceApi.findAll) for the
 * ResourcesPage, re-fetching whenever the filter or page changes, with
 * loading/error state, a delete action (resourceApi.delete) and an analyze
 * action (resourceApi.analyze). Mirror the searchRunsProvider pattern.
 */
const useResources = (filter: ResourceFilter, page: number, size: number) => {
  void filter;
  void page;
  void size;

  const [resources] = useState<PageResponse<ResourceResponse> | null>(null);
  const [loading] = useState<boolean>(false);

  const onDelete = async (id: number) => {
    void id;
  };
  const onAnalyze = async (id: number) => {
    void id;
  };

  return { resources, loading, onDelete, onAnalyze };
};

export default useResources;
