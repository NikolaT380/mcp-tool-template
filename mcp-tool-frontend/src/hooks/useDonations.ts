import { useCallback, useEffect, useState } from 'react';
import type { CreateDonationBatchRequest, DonationBatchResponse } from '../api/types/donation.ts';
import donationApi from '../api/donationApi.ts';
import useSnackbar from './useSnackbar.ts';

/**
 * _TODO(student): Drive the donation workflow for the DonationsPage: list the
 * batches (donationApi.findAll) and expose create/approve/submit actions,
 * re-fetching after every mutation, with loading/error state. Mirror the
 * searchRunsProvider pattern.
 */
const useDonations = () => {
  const [donations, setDonations] = useState<DonationBatchResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const { showSnackbar } = useSnackbar();

  const fetchDonations = useCallback(async () => {
    setLoading(true);
    try {
      const response = await donationApi.findAll();
      setDonations(response.data);
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || 'Failed to load donation batches.';
      showSnackbar(message, 'error');
    } finally {
      setLoading(false);
    }
  }, [showSnackbar]);

  useEffect(() => {
    fetchDonations();
  }, [fetchDonations]);

  const onCreate = async (data: CreateDonationBatchRequest) => {
    try {
      await donationApi.add(data);
      showSnackbar('Donation batch created successfully.', 'success');
      await fetchDonations();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || 'Failed to create donation batch.';
      showSnackbar(message, 'error');
    }
  };

  const onApprove = async (id: number) => {
    try {
      await donationApi.approve(String(id));
      showSnackbar('Donation batch approved.', 'success');
      await fetchDonations();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || 'Failed to approve donation batch.';
      showSnackbar(message, 'error');
    }
  };

  const onSubmit = async (id: number) => {
    try {
      await donationApi.submit(String(id));
      showSnackbar('Donation batch submitted.', 'success');
      await fetchDonations();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || 'Failed to submit donation batch.';
      showSnackbar(message, 'error');
    }
  };

  return { donations, loading, onCreate, onApprove, onSubmit };
};

export default useDonations;
