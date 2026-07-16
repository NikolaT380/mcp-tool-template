import { Box, Typography } from '@mui/material';
import type { ResourceAnalysisResponse } from '../../../../api/types/resource.ts';

interface ResourceAnalysisPanelProps {
  analysis: ResourceAnalysisResponse | null;
}

/**
 * TODO(student): Render the analysis of a resource: the summary, the keywords,
 * the sentence/word counts and the Macedonian-language confidence. Show a
 * call-to-action to run the analysis when it is still null.
 */
const ResourceAnalysisPanel = ({ analysis }: ResourceAnalysisPanelProps) => {
  void analysis;

  return (
    <Box>
      <Typography color='text.secondary'>
        TODO(student): Render the resource analysis.
      </Typography>
    </Box>
  );
};

export default ResourceAnalysisPanel;
