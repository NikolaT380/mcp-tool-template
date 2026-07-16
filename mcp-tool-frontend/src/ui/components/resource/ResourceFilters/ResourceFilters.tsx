import { Box, Typography } from '@mui/material';
import type { ResourceFilter } from '../../../../api/types/resource.ts';

interface ResourceFiltersProps {
  filter: ResourceFilter;
  onChange: (filter: ResourceFilter) => void;
}

/**
 * TODO(student): Implement the filter bar for the resource browser: search run,
 * minimum Macedonian confidence (slider), analyzed yes/no, donated yes/no, and
 * a free-text search over the content. Call onChange with the updated filter.
 */
const ResourceFilters = ({ filter, onChange }: ResourceFiltersProps) => {
  void filter;
  void onChange;

  return (
    <Box sx={{ mb: 2 }}>
      <Typography color='text.secondary'>
        TODO(student): Implement the resource filters.
      </Typography>
    </Box>
  );
};

export default ResourceFilters;
