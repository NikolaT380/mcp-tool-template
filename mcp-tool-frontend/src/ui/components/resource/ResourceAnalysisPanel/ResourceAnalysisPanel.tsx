import { Box, Chip, Typography } from '@mui/material';
import type { ResourceAnalysisResponse } from '../../../../api/types/resource.ts';

interface ResourceAnalysisPanelProps {
  analysis: ResourceAnalysisResponse | null;
}

/**
 * _TODO(student): Render the analysis of a resource: the summary, the keywords,
 * the sentence/word counts and the Macedonian-language confidence. Show a
 * call-to-action to run the analysis when it is still null.
 */
const ResourceAnalysisPanel = ({ analysis }: ResourceAnalysisPanelProps) => {
    if (!analysis) {
        return (
            <Box sx={{ mt: 1 }}>
                <Typography color="text.secondary">
                    This resource has not been analyzed yet. Click "Analyze" above to run the analysis.
                </Typography>
            </Box>
        );
    }

    // _TODO(student): Render the resource analysis.
    const keywordList = analysis.keywords
        ? analysis.keywords.split(',').map((k) => k.trim()).filter(Boolean)
        : [];

    const confidencePercent = Math.round((analysis.macedonianConfidence || 0) * 100);

    return (
        <Box sx={{ mt: 2 }}>
            <Typography variant="h6" sx={{ mb: 1 }}>
                Analysis Results
            </Typography>

            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                <Chip
                    size="small"
                    label={`MK Confidence: ${confidencePercent}%`}
                    color={confidencePercent >= 60 ? 'success' : 'default'}
                />
                <Chip
                    size="small"
                    label={`${analysis.sentenceCount || 0} sentences`}
                    variant="outlined"
                />
            </Box>

            {analysis.summary && (
                <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle2" sx={{ mb: 0.5 }}>
                        Summary
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {analysis.summary}
                    </Typography>
                </Box>
            )}

            {keywordList.length > 0 && (
                <Box>
                    <Typography variant="subtitle2" sx={{ mb: 1 }}>
                        Keywords
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                        {keywordList.map((kw, idx) => (
                            <Chip key={idx} size="small" label={kw} variant="outlined" />
                        ))}
                    </Box>
                </Box>
            )}
        </Box>
    );
};

export default ResourceAnalysisPanel;
