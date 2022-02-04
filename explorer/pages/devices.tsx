import { Box, Button, Divider, Group, LoadingOverlay, Paper, Text } from '@mantine/core';
import { useNotifications } from '@mantine/notifications';
import { GetServerSideProps } from 'next';
import { useRouter } from 'next/router';
import { Fragment, useEffect, useState } from 'react';
import {
  Box as BoxIcon,
  Search as SearchIcon,
  Users as UsersIcon,
  X as XIcon,
} from 'react-feather';

import { getDevice, getDevices } from '../components/api/devices';
import DeviceInfo from '../components/data/DeviceInfo';
import useQuery from '../components/hooks/useQuery';
import ClearableTextInput from '../components/inputs/ClearableTextInput';
import Header from '../components/layouts/Header';
import Empty from '../components/misc/Empty';
import Device from '../types/device';

interface DevicesPageProps {
  initialDeviceId?: string;
  initialOrganizationId?: string;
}

function DevicesPage({ initialDeviceId, initialOrganizationId }: DevicesPageProps) {
  const notifications = useNotifications();
  const router = useRouter();
  const [isInitialized, setIsInitialized] = useState(false);
  const [devices, setDevices] = useState<Device[]>([]);
  const [deviceId, setDeviceId] = useState<string>(initialDeviceId ?? '');
  const [organizationId, setOrganizationId] = useState<string>(initialOrganizationId ?? '');

  const [search, { isLoading }] = useQuery<Device[]>(
    async () => {
      await router.push({ pathname: router.pathname, query: { deviceId, organizationId } });
      if (deviceId !== '') {
        return [await getDevice(organizationId, deviceId)];
      } else {
        return await getDevices(organizationId);
      }
    },
    setDevices,
    (error) => {
      setDevices([]);
      notifications.showNotification({
        title: 'Failed to find devices',
        message: error.message,
        icon: <XIcon />,
        color: 'red',
        autoClose: 5000,
      });
    }
  );

  const isSearchEnabled = !isLoading && organizationId !== '';

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
      <Header title="Devices">
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
          {devices.length ? (
            devices.map((device, i) => (
              <Fragment key={[device.organizationId, device.id].join('-')}>
                <DeviceInfo {...device} />
                {i === devices.length - 1 ? null : <Divider />}
              </Fragment>
            ))
          ) : (
            <Empty sx={{ padding: 40 }}>
              <Text color="gray" size="sm" mt="md">
                Devices not found
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
  const { organizationId, deviceId } = context.query;
  const initialOrganizationId = typeof organizationId === 'string' ? organizationId : '';
  const initialDeviceId = typeof deviceId === 'string' ? deviceId : '';

  return {
    props: {
      title: 'Devices',
      initialOrganizationId,
      initialDeviceId,
    },
  };
};

export default DevicesPage;
