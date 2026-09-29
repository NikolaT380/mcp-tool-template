import { Box, Card, CardContent, CircularProgress, Container, Grid, Typography } from '@mui/material';
import useStats from '../../../../hooks/useStats.ts';

/**
 * _TODO(student): Turn this into a small dashboard using useStats (which calls
 * GET /api/stats — the same data as the `corpus_stats` MCP tool): total search
 * runs, resources, analyzed resources, donation batches and donated resources.
 */
const HomePage = () => {
  const { stats, loading } = useStats();

  const statCards = [
    { title: 'Search Runs', value: stats?.searchRuns ?? 0, desc: 'Scrape sessions executed' },
    { title: 'Collected Resources', value: stats?.resources ?? 0, desc: 'Resources from izvor.net.mk' },
    { title: 'Analyzed Resources', value: stats?.analyzedResources ?? 0, desc: 'Analyzed with NLP metrics' },
    { title: 'Donation Batches', value: stats?.donationBatches ?? 0, desc: 'Batches created' },
    { title: 'Donated Resources', value: stats?.donatedResources ?? 0, desc: 'Resources in batches' },
  ];

  return (
    <Box sx={{ m: 0, p: 0 }}>
      <Container maxWidth='xl' sx={{ mt: 3, py: 3 }}>
        <Typography variant='h4' gutterBottom>
          MCP Tool for doniraj.vezilka.ai 🧵
        </Typography>
        <Typography variant='body1' sx={{ mb: 4 }}>
          This is an MCP (Model Context Protocol) server that searches and
          analyzes a specific Macedonian-language website and donates the
          results to the Vezilka language-preservation platform. Use the Search
          Runs page to collect resources, the Resources page to browse and
          analyze them, the Donations page to submit batches, and the MCP
          Playground to invoke the server's tools directly.
        </Typography>

        {loading && (
            <CircularProgress sx={{ display: 'block', mx: 'auto', mt: 4 }} />
        )}

        {!loading && (
            <Grid container spacing={3}>
              {statCards.map((item, index) => (
                  <Grid key={index} size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
                    <Card sx={{ height: '100%', textAlign: 'center', p: 1 }} variant="outlined">
                      <CardContent>
                        <Typography variant="h4" color="primary" sx={{ fontWeight: 'bold', mb: 1 }}>
                          {item.value}
                        </Typography>
                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                          {item.title}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {item.desc}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
              ))}
            </Grid>
        )}
      </Container>
    </Box>
  );
};

export default HomePage;
