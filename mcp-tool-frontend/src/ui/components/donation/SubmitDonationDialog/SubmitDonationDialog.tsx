import { Box, Button, Checkbox, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import resourceApi from '../../../../api/resourceApi.ts';
import type { ResourceResponse } from '../../../../api/types/resource.ts';
import useDonations from '../../../../hooks/useDonations.ts';

interface SubmitDonationDialogProps {
  open: boolean;
  onClose: () => void;
}

/**
 * _TODO(student): Implement the "create donation batch" flow: let the user pick
 * not-yet-donated resources (resourceApi.findAll with donated=false), review
 * their content, and create the batch via useDonations().onCreate.
 */
const SubmitDonationDialog = ({ open, onClose }: SubmitDonationDialogProps) => {
    const { onCreate } = useDonations();
    const [availableResources, setAvailableResources] = useState<ResourceResponse[]>([]);
    const [selectedIds, setSelectedIds] = useState<number[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [submitting, setSubmitting] = useState<boolean>(false);

    useEffect(() => {
        if (open) {
            setSelectedIds([]);
            setLoading(true);
            resourceApi
                .findAll({ donated: false }, 0, 50)
                .then((res) => {
                    setAvailableResources(res.data.content || []);
                })
                .catch(() => {
                    setAvailableResources([]);
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }, [open]);

    const handleToggle = (id: number) => {
        setSelectedIds((prev) =>
            prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
        );
    };

    const handleCreate = async () => {
        if (selectedIds.length === 0) return;
        setSubmitting(true);
        try {
            await onCreate({ resourceIds: selectedIds });
            onClose();
        } finally {
            setSubmitting(false);
        }
    };

    // _TODO(student): Implement this dialog.
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth='md'>
            <DialogTitle>New Donation Batch</DialogTitle>
            <DialogContent dividers>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    Select not-yet-donated resources to bundle into a new batch for doniraj.vezilka.ai:
                </Typography>

                {loading ? (
                    <CircularProgress sx={{ display: 'block', mx: 'auto', my: 3 }} />
                ) : availableResources.length === 0 ? (
                    <Typography color="text.secondary" align="center" sx={{ my: 3 }}>
                        No undonated resources found. Search for more resources first.
                    </Typography>
                ) : (
                    <List sx={{ maxHeight: 320, overflow: 'auto' }}>
                        {availableResources.map((res) => (
                            <ListItem key={res.id} disablePadding>
                                <ListItemButton onClick={() => handleToggle(res.id)} dense>
                                    <ListItemIcon>
                                        <Checkbox
                                            edge="start"
                                            checked={selectedIds.includes(res.id)}
                                            tabIndex={-1}
                                            disableRipple
                                        />
                                    </ListItemIcon>
                                    <ListItemText
                                        primary={res.title ?? `Resource #${res.id}`}
                                        secondary={`${res.wordCount || 0} words | MK Confidence: ${Math.round((res.macedonianConfidence || 0) * 100)}%`}
                                    />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                )}

                {selectedIds.length > 0 && (
                    <Box sx={{ mt: 1.5 }}>
                        <Typography variant="caption" sx={{ fontWeight: 'bold' }}>
                            Selected: {selectedIds.length} resource(s)
                        </Typography>
                    </Box>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={submitting}>
                    Cancel
                </Button>
                <Button
                    variant="contained"
                    onClick={handleCreate}
                    disabled={selectedIds.length === 0 || submitting}
                >
                    {submitting ? 'Creating..' : 'Create Batch'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default SubmitDonationDialog;
