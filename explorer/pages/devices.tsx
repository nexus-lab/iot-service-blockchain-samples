import { Box, Button, Divider, Group, Paper, Text } from '@mantine/core';
import moment from 'iot-service-blockchain/sdk/javascript/moment';
import { Fragment, useState } from 'react';
import { Box as BoxIcon, Filter as FilterIcon, Users as UsersIcon } from 'react-feather';

import DeviceInfo from '../components/data/DeviceInfo';
import ClearableTextInput from '../components/inputs/ClearableTextInput';
import Header from '../components/layouts/Header';
import Empty from '../components/misc/Empty';

function DevicesPage() {
  const [deviceId, setDeviceId] = useState('');
  const [organizationId, setOrganizationId] = useState('');

  return (
    <>
      <Header title="Devices">
        <Group>
          <ClearableTextInput
            value={deviceId}
            onChange={(e) => setDeviceId(e.target.value)}
            icon={<UsersIcon size={16} />}
            placeholder="Organization ID"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <ClearableTextInput
            value={organizationId}
            onChange={(e) => setOrganizationId(e.target.value)}
            icon={<BoxIcon size={16} />}
            placeholder="Device ID"
            sx={{ flexGrow: 1, flexBasis: 1 }}
            required
          />
          <Button leftIcon={<FilterIcon size={16} />} color="blue">
            Filter
          </Button>
        </Group>
      </Header>
      <Box mt="lg">
        <Paper shadow="xs">
          <Empty sx={(theme) => ({ padding: theme.spacing.xl })}>
            <Text color="gray" size="sm" mt="md">
              Devices not found
            </Text>
          </Empty>
          {new Array(5).fill(0).map((_, i) => (
            <Fragment key={i}>
              <DeviceInfo
                name={`device${i}`}
                id="asdfjkasjdfkasdjfkasdfjkasjdfkasdjfkasdfjkasjdfkasdjfkasdfjkasjdfkasdjfkasdfjkasjdfkasdjfkasdfjkasjdfkasdjfkasdfjkasjdfkasdjfk"
                organizationId="Org1"
                description="Device1 of Org1"
                lastUpdateTime={moment()}
              />
              {i == 4 ? null : <Divider />}
            </Fragment>
          ))}
        </Paper>
      </Box>
    </>
  );
}

export function getStaticProps() {
  return {
    props: {
      title: 'Devices',
    },
  };
}

export default DevicesPage;
