import { Box, Button, Chip, CircularProgress, Link, Typography } from '@mui/material';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import resourceApi from '../../../../api/resourceApi.ts';
import type { ResourceResponse } from '../../../../api/types/resource.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';
import ResourceAnalysisPanel from '../../../components/resource/ResourceAnalysisPanel/ResourceAnalysisPanel.tsx';

/**
 * _TODO(student): Show one resource in full: the complete content, the source
 * link, the language confidence, the donation status, and its analysis
 * (resourceApi.findById + the ResourceAnalysisPanel). Add an "Analyze" button
 * that calls resourceApi.analyze.
 */
const ResourceDetailsPage = () => {
    const { id } = useParams<{ id: string }>();
    const { showSnackbar } = useSnackbar();
    const navigate = useNavigate();

    const [resource, setResource] = useState<ResourceResponse | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [analyzing, setAnalyzing] = useState<boolean>(false);

    const fetchResource = useCallback(async () => {
        if (!id) return;
        setLoading(true);
        try {
            const response = await resourceApi.findById(id);
            setResource(response.data);
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } };
            showSnackbar(error.response?.data?.message || 'Failed to load resource details.', 'error');
        } finally {
            setLoading(false);
        }
    }, [id, showSnackbar]);

    useEffect(() => {
        fetchResource();
    }, [fetchResource]);

    const onAnalyze = async () => {
        if (!id) return;
        setAnalyzing(true);
        try {
            await resourceApi.analyze(id);
            showSnackbar('Resource analyzed successfully.', 'success');
            await fetchResource();
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } };
            showSnackbar(error.response?.data?.message || 'Failed to analyze resource.', 'error');
        } finally {
            setAnalyzing(false);
        }
    };

    if (loading) {
        return <CircularProgress sx={{ display: 'block', mx: 'auto', mt: 4 }} />;
    }

    if (!resource) {
        return (
            <Box>
                <Typography color="error">Resource not found.</Typography>
                <Button sx={{ mt: 2 }} onClick={() => navigate('/resources')}>
                    Back to Resources
                </Button>
            </Box>
        );
    }

    const confidencePercent = Math.round((resource.macedonianConfidence || 0) * 100);
    const isDonated = resource.donationBatchId !== null && resource.donationBatchId !== undefined;

    // _TODO(student): Implement this page.
    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant='h5'>
                    {resource.title ?? `Resource #${resource.id}`}
                </Typography>
                {!resource.analysis && (
                    <Button variant="contained" onClick={onAnalyze} disabled={analyzing}>
                        {analyzing ? 'Analyzing' : 'Analyze'}
                    </Button>
                )}
            </Box>

            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                <Chip
                    size="small"
                    label={`MK Confidence: ${confidencePercent}%`}
                    color={confidencePercent >= 60 ? 'success' : 'default'}
                />
                <Chip
                    size="small"
                    label={isDonated ? 'Donated' : 'Not Donated'}
                    color={isDonated ? 'secondary' : 'default'}
                    variant="outlined"
                />
                <Chip size="small" label={`${resource.wordCount || 0} words`} variant="outlined" />
            </Box>

            <Box sx={{ mb: 3 }}>
                <Typography variant="subtitle2" sx={{ mb: 0.5 }}>
                    Content
                </Typography>
                <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', mb: 1.5 }}>
                    {resource.content}
                </Typography>
                {resource.sourceUrl && (
                    <Typography variant="caption" color="text.secondary">
                        Source:{' '}
                        <Link
                            href={resource.sourceUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                        >
                            {resource.sourceUrl}
                        </Link>
                    </Typography>
                )}
            </Box>

            <ResourceAnalysisPanel analysis={resource.analysis} />
        </Box>
    );
};

export default ResourceDetailsPage;
