import { Card, CardContent, Typography } from '@mui/material';
import type { ResourceResponse } from '../../../../api/types/resource.ts';

interface ResourceCardProps {
  resource: ResourceResponse;
}

/**
 * TODO(student): Show the resource: title, content preview, source link, the
 * Macedonian-language confidence, whether it has been analyzed, and whether it
 * is already part of a donation batch. Add navigation to /resources/{id}, an
 * analyze action and a delete action (useResources().onAnalyze / onDelete).
 */
const ResourceCard = ({ resource }: ResourceCardProps) => {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant='subtitle2'>{resource.title ?? `Resource #${resource.id}`}</Typography>
        <Typography variant='body2' color='text.secondary'>
          TODO(student): Render this resource.
        </Typography>
      </CardContent>
    </Card>
  );
};

export default ResourceCard;
