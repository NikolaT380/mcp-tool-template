import { Box, Button, Card, CardActions, CardContent, Chip, Typography } from '@mui/material';
import donationApi from '../../../../api/donationApi.ts';
import type { DonationBatchResponse } from '../../../../api/types/donation.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';

interface DonationBatchCardProps {
  batch: DonationBatchResponse;
}

/**
 * _TODO(student): Show the batch: number of resources, status, the Vezilka
 * reference once submitted, and approve/submit actions
 * (useDonations().onApprove / onSubmit) enabled according to the status.
 */
const DonationBatchCard = ({ batch }: DonationBatchCardProps) => {
  const { showSnackbar } = useSnackbar();

  const handleApprove = async () => {
    try {
      await donationApi.approve(String(batch.id));
      showSnackbar('Donation batch approved.', 'success');
      window.location.reload();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      showSnackbar(error.response?.data?.message || 'Failed to approve donation batch.', 'error');
    }
  };

  const handleSubmit = async () => {
    try {
      await donationApi.submit(String(batch.id));
      showSnackbar('Donation batch submitted to Vezilka.', 'success');
      window.location.reload();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      showSnackbar(error.response?.data?.message || 'Failed to submit donation batch.', 'error');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DRAFT':
        return 'default';
      case 'APPROVED':
        return 'info';
      case 'SUBMITTED':
        return 'primary';
      case 'ACCEPTED':
        return 'success';
      case 'REJECTED':
      case 'FAILED':
        return 'error';
      default:
        return 'default';
    }
  };

  // _TODO(student): Render this donation batch.
  return (
      <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent sx={{ flexGrow: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
              Batch #{batch.id}
            </Typography>
            <Chip
                size="small"
                label={batch.status}
                color={getStatusColor(batch.status)}
            />
          </Box>

          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            Resources: <strong>{batch.resourceIds?.length || 0}</strong>
          </Typography>

          {batch.vezilkaReference && (
              <Box sx={{ mt: 1, p: 1, bgcolor: 'action.hover', borderRadius: 1 }}>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                  Vezilka Reference:
                </Typography>
                <Typography variant="body2" sx={{ fontFamily: 'monospace', wordBreak: 'break-all' }}>
                  {batch.vezilkaReference}
                </Typography>
              </Box>
          )}

          {batch.submittedAt && (
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                Submitted: {new Date(batch.submittedAt).toLocaleString()}
              </Typography>
          )}
        </CardContent>
        <CardActions sx={{ justifyContent: 'flex-end', px: 2, pb: 2 }}>
          {batch.status === 'DRAFT' && (
              <Button size="small" variant="outlined" onClick={handleApprove}>
                Approve
              </Button>
          )}
          {batch.status === 'APPROVED' && (
              <Button size="small" variant="contained" onClick={handleSubmit}>
                Submit
              </Button>
          )}
        </CardActions>
      </Card>
  );
};

export default DonationBatchCard;
