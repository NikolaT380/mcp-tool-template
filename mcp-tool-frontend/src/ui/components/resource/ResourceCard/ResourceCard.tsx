import { Box, Button, Card, CardActions, CardContent, Chip, Link, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import type { ResourceFilter, ResourceResponse } from '../../../../api/types/resource.ts';
import useResources from '../../../../hooks/useResources.ts';

interface ResourceCardProps {
  resource: ResourceResponse;
}

/**
 * _TODO(student): Show the resource: title, content preview, source link, the
 * Macedonian-language confidence, whether it has been analyzed, and whether it
 * is already part of a donation batch. Add navigation to /resources/{id}, an
 * analyze action and a delete action (useResources().onAnalyze / onDelete).
 */

const EMPTY_FILTER: ResourceFilter = {};

const ResourceCard = ({ resource }: ResourceCardProps) => {
    const navigate = useNavigate();
    const { onDelete, onAnalyze } = useResources(EMPTY_FILTER, 0, 10);

    const confidencePercent = Math.round((resource.macedonianConfidence || 0) * 100);
    const isAnalyzed = Boolean(resource.analysis);
    const isDonated = resource.donationBatchId !== null && resource.donationBatchId !== undefined;

    // _TODO(student): Render this resource.
    return (
        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1 }}>
                <Box sx={{ display: 'flex', gap: 1, mb: 1, flexWrap: 'wrap' }}>
                    <Chip
                        size="small"
                        label={`MK: ${confidencePercent}%`}
                        color={confidencePercent >= 60 ? 'success' : 'default'}
                    />
                    {isAnalyzed && <Chip size="small" label="Analyzed" color="primary" variant="outlined" />}
                    {isDonated && <Chip size="small" label="Donated" color="secondary" variant="outlined" />}
                    <Chip size="small" label={`${resource.wordCount || 0} words`} variant="outlined" />
                </Box>

                <Typography
                    variant="subtitle2"
                    sx={{ cursor: 'pointer', '&:hover': { color: 'primary.main' }, mb: 1 }}
                    onClick={() => navigate(`/resources/${resource.id}`)}
                >
                    {resource.title ?? `Resource #${resource.id}`}
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                        mb: 1.5,
                    }}
                >
                    {resource.content}
                </Typography>

                {resource.sourceUrl && (
                    <Link
                        href={resource.sourceUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        variant="caption"
                        sx={{ display: 'inline-block', wordBreak: 'break-all' }}
                    >
                        {resource.sourceUrl}
                    </Link>
                )}
            </CardContent>

            <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
                <Box sx={{ display: 'flex', gap: 1 }}>
                    {!isAnalyzed && (
                        <Button size="small" variant="contained" onClick={() => onAnalyze(resource.id)}>
                            Analyze
                        </Button>
                    )}
                    <Button size="small" color="error" onClick={() => onDelete(resource.id)}>
                        Delete
                    </Button>
                </Box>
                <Button size="small" onClick={() => navigate(`/resources/${resource.id}`)}>
                    Details
                </Button>
            </CardActions>
        </Card>
    );
};

export default ResourceCard;
