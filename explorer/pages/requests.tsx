import { Box, Button, Divider, Group, LoadingOverlay, Paper, Text } from '@mantine/core';
import { useNotifications } from '@mantine/notifications';
import { GetServerSideProps } from 'next';
import { useRouter } from 'next/router';
import { Fragment, useEffect, useState } from 'react';
import {
  Box as BoxIcon,
  Radio as RadioIcon,
  Search as SearchIcon,
  Send as SendIcon,
  Users as UsersIcon,
  X as XIcon,
} from 'react-feather';

import { getServiceRequest, getServiceRequests } from '../components/api/requests';
import ServiceRequestInfo from '../components/data/ServiceRequestInfo';
import useQuery from '../components/hooks/useQuery';
import ClearableTextInput from '../components/inputs/ClearableTextInput';
import Header from '../components/layouts/Header';
import Empty from '../components/misc/Empty';
import VerticalDivider from '../components/misc/VerticalDivider';
import ServiceRequestResponse from '../types/requests';

interface RequestsPageProps {
  initialDeviceId?: string;
  initialOrganizationId?: string;
  initialServiceName?: string;
  initialRequestId?: string;
}

function RequestsPage({
  initialDeviceId,
  initialOrganizationId,
  initialServiceName,
  initialRequestId,
}: RequestsPageProps) {
  const notifications = useNotifications();
  const router = useRouter();
  const [isInitialized, setIsInitialized] = useState(false);
  const [requests, setRequests] = useState<ServiceRequestResponse[]>([]);
  const [deviceId, setDeviceId] = useState(initialDeviceId ?? '');
  const [organizationId, setOrganizationId] = useState(initialOrganizationId ?? '');
  const [serviceName, setServiceName] = useState(initialServiceName ?? '');
  const [requestId, setRequestId] = useState(initialRequestId ?? '');

  const [search, { isLoading }] = useQuery<ServiceRequestResponse[]>(
    async () => {
      await router.push({
        pathname: router.pathname,
        query: { requestId },
      });
      if (requestId !== '') {
        return [await getServiceRequest(requestId)];
      } else {
        return await getServiceRequests(organizationId, deviceId, serviceName);
      }
    },
    setRequests,
    (error) => {
      setRequests([]);
      notifications.showNotification({
        title: 'Failed to find service requests',
        message: error.message,
        icon: <XIcon />,
        color: 'red',
        autoClose: 5000,
      });
    }
  );

  const isSearchEnabled =
    !isLoading &&
    ((organizationId !== '' && deviceId !== '' && serviceName !== '') || requestId !== '');

  useEffect(() => {
    if (!isInitialized) {
      setIsInitialized(true);
      if (isSearchEnabled) {
        void search();
      }
    }
  }, [search, isInitialized, isSearchEnabled]);

  return (
    <>
      <Header title="Requests">
        <Group>
          <ClearableTextInput
            value={organizationId}
            onChange={(e) => setOrganizationId(e.target.value)}
            icon={<UsersIcon size={16} />}
            placeholder="Organization ID"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <ClearableTextInput
            value={deviceId}
            onChange={(e) => setDeviceId(e.target.value)}
            icon={<BoxIcon size={16} />}
            placeholder="Device ID"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <ClearableTextInput
            value={serviceName}
            onChange={(e) => setServiceName(e.target.value)}
            icon={<RadioIcon size={16} />}
            placeholder="Service Name"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <VerticalDivider label="OR" />
          <ClearableTextInput
            value={requestId}
            onChange={(e) => setRequestId(e.target.value)}
            icon={<SendIcon size={16} />}
            placeholder="Request ID"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <Button
            color="blue"
            onClick={search}
            loading={isLoading}
            disabled={!isSearchEnabled}
            leftIcon={<SearchIcon size={16} />}>
            {isLoading ? 'Searching' : 'Search'}
          </Button>
        </Group>
      </Header>
      <Box mt="lg">
        <Paper shadow="xs" sx={{ position: 'relative' }}>
          <LoadingOverlay visible={isLoading} />
          {requests.length ? (
            requests.map((request, i) => (
              <Fragment key={[request.request.id].join('-')}>
                <ServiceRequestInfo {...request} />
                {i === requests.length - 1 ? null : <Divider />}
              </Fragment>
            ))
          ) : (
            <Empty sx={{ padding: 40 }}>
              <Text color="gray" size="sm" mt="md">
                Service requests not found
              </Text>
            </Empty>
          )}
        </Paper>
      </Box>
    </>
  );
}

/* eslint-disable @typescript-eslint/require-await */
export const getServerSideProps: GetServerSideProps = async (context) => {
  const { organizationId, deviceId, serviceName, requestId } = context.query;
  const initialOrganizationId = typeof organizationId === 'string' ? organizationId : '';
  const initialDeviceId = typeof deviceId === 'string' ? deviceId : '';
  const initialServiceName = typeof serviceName === 'string' ? serviceName : '';
  const initialRequestId = typeof requestId === 'string' ? requestId : '';

  return {
    props: {
      title: 'Requests',
      initialOrganizationId,
      initialDeviceId,
      initialServiceName,
      initialRequestId,
    },
  };
};

export default RequestsPage;
