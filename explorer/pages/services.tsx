import { Box, Button, Divider, Group, LoadingOverlay, Paper, Text } from '@mantine/core';
import { useNotifications } from '@mantine/notifications';
import { GetServerSideProps } from 'next';
import { useRouter } from 'next/router';
import { Fragment, useEffect, useState } from 'react';
import {
  Box as BoxIcon,
  Radio as RadioIcon,
  Search as SearchIcon,
  Users as UsersIcon,
  X as XIcon,
} from 'react-feather';

import { getService, getServices } from '../components/api/services';
import ServiceInfo from '../components/data/ServiceInfo';
import useQuery from '../components/hooks/useQuery';
import ClearableTextInput from '../components/inputs/ClearableTextInput';
import Header from '../components/layouts/Header';
import Empty from '../components/misc/Empty';
import Service from '../types/service';

interface ServicesPageProps {
  initialDeviceId?: string;
  initialOrganizationId?: string;
  initialServiceName?: string;
}

function ServicesPage({
  initialDeviceId,
  initialOrganizationId,
  initialServiceName,
}: ServicesPageProps) {
  const notifications = useNotifications();
  const router = useRouter();
  const [isInitialized, setIsInitialized] = useState(false);
  const [services, setServices] = useState<Service[]>([]);
  const [deviceId, setDeviceId] = useState(initialDeviceId ?? '');
  const [organizationId, setOrganizationId] = useState(initialOrganizationId ?? '');
  const [serviceName, setServiceName] = useState(initialServiceName ?? '');

  const [search, { isLoading }] = useQuery<Service[]>(
    async () => {
      await router.push({
        pathname: router.pathname,
        query: { deviceId, organizationId, initialServiceName },
      });
      if (serviceName !== '') {
        return [await getService(organizationId, deviceId, serviceName)];
      } else {
        return await getServices(organizationId, deviceId);
      }
    },
    setServices,
    (error) => {
      setServices([]);
      notifications.showNotification({
        title: 'Failed to find services',
        message: error.message,
        icon: <XIcon />,
        color: 'red',
        autoClose: 5000,
      });
    }
  );

  const isSearchEnabled = !isLoading && organizationId !== '' && deviceId !== '';

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
      <Header title="Services">
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
          {services.length ? (
            services.map((service, i) => (
              <Fragment key={[service.organizationId, service.deviceId, service.name].join('-')}>
                <ServiceInfo {...service} />
                {i === services.length - 1 ? null : <Divider />}
              </Fragment>
            ))
          ) : (
            <Empty sx={{ padding: 40 }}>
              <Text color="gray" size="sm" mt="md">
                Services not found
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
  const { organizationId, deviceId, serviceName } = context.query;
  const initialOrganizationId = typeof organizationId === 'string' ? organizationId : '';
  const initialDeviceId = typeof deviceId === 'string' ? deviceId : '';
  const initialServiceName = typeof serviceName === 'string' ? serviceName : '';

  return {
    props: {
      title: 'Services',
      initialOrganizationId,
      initialDeviceId,
      initialServiceName,
    },
  };
};

export default ServicesPage;
