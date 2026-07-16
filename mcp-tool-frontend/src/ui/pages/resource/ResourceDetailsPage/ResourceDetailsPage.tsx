import { Box, Typography } from '@mui/material';
import { useParams } from 'react-router';
import ResourceAnalysisPanel from '../../../components/resource/ResourceAnalysisPanel/ResourceAnalysisPanel.tsx';

/**
 * TODO(student): Show one resource in full: the complete content, the source
 * link, the language confidence, the donation status, and its analysis
 * (resourceApi.findById + the ResourceAnalysisPanel). Add an "Analyze" button
 * that calls resourceApi.analyze.
 */
const ResourceDetailsPage = () => {
  const { id } = useParams<{ id: string }>();

  return (
    <Box>
      <Typography variant='h5' gutterBottom>Resource #{id}</Typography>
      <Typography color='text.secondary' sx={{ mb: 2 }}>
        TODO(student): Implement this page.
      </Typography>
      <ResourceAnalysisPanel analysis={null}/>
    </Box>
  );
};

export default ResourceDetailsPage;
