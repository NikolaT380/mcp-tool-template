import { Box, Button, FormControl, InputLabel, MenuItem,
    Paper, Select, Slider, TextField, Typography } from '@mui/material';
import React, { useState } from 'react';
import type { ResourceFilter } from '../../../../api/types/resource.ts';

interface ResourceFiltersProps {
  filter: ResourceFilter;
  onChange: (filter: ResourceFilter) => void;
}

/**
 * _TODO(student): Implement the filter bar for the resource browser: search run,
 * minimum Macedonian confidence (slider), analyzed yes/no, donated yes/no, and
 * a free-text search over the content. Call onChange with the updated filter.
 */
const ResourceFilters = ({ filter, onChange }: ResourceFiltersProps) => {
    const [searchTerm, setSearchTerm] = useState<string>(filter.search || '');
    const [minConfidence, setMinConfidence] = useState<number>(
        filter.minMacedonianConfidence !== undefined ? filter.minMacedonianConfidence : 0
    );
    const [analyzed, setAnalyzed] = useState<string>(
        filter.analyzed === undefined ? 'all' : String(filter.analyzed)
    );
    const [donated, setDonated] = useState<string>(
        filter.donated === undefined ? 'all' : String(filter.donated)
    );

    const handleApply = (e: React.FormEvent) => {
        e.preventDefault();
        onChange({
            ...filter,
            search: searchTerm.trim() || undefined,
            minMacedonianConfidence: minConfidence > 0 ? minConfidence : undefined,
            analyzed: analyzed === 'all' ? undefined : analyzed === 'true',
            donated: donated === 'all' ? undefined : donated === 'true',
        });
    };

    const handleReset = () => {
        setSearchTerm('');
        setMinConfidence(0);
        setAnalyzed('all');
        setDonated('all');
        onChange({});
    };

    // _TODO(student): Implement the resource filters.
    return (
        <Paper sx={{ p: 2, mb: 3 }} component="form" onSubmit={handleApply}>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center' }}>
                <TextField
                    label="Search text"
                    size="small"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Filter by title or content."
                    sx={{ minWidth: 220 }}
                />

                <Box sx={{ width: 180, px: 1 }}>
                    <Typography variant="caption" color="text.secondary">
                        Min MK Confidence: {Math.round(minConfidence * 100)}%
                    </Typography>
                    <Slider
                        size="small"
                        value={minConfidence}
                        min={0}
                        max={1}
                        step={0.01}
                        onChange={(_, val) => setMinConfidence(val as number)}
                    />
                </Box>

                <FormControl size="small" sx={{ minWidth: 130 }}>
                    <InputLabel>Analyzed</InputLabel>
                    <Select
                        value={analyzed}
                        label="Analyzed"
                        onChange={(e) => setAnalyzed(e.target.value)}
                    >
                        <MenuItem value="all">All</MenuItem>
                        <MenuItem value="true">Analyzed</MenuItem>
                        <MenuItem value="false">Not Analyzed</MenuItem>
                    </Select>
                </FormControl>

                <FormControl size="small" sx={{ minWidth: 130 }}>
                    <InputLabel>Donated</InputLabel>
                    <Select
                        value={donated}
                        label="Donated"
                        onChange={(e) => setDonated(e.target.value)}
                    >
                        <MenuItem value="all">All</MenuItem>
                        <MenuItem value="true">Donated</MenuItem>
                        <MenuItem value="false">Not Donated</MenuItem>
                    </Select>
                </FormControl>

                <Button type="submit" variant="contained">
                    Apply
                </Button>
                <Button variant="outlined" onClick={handleReset}>
                    Reset
                </Button>
            </Box>
        </Paper>
    );
};

export default ResourceFilters;
