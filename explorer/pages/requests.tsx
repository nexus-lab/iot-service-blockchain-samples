import { Box, Button, Divider, Group, Paper, Text } from '@mantine/core';
import moment from 'iot-service-blockchain/sdk/javascript/moment';
import { Fragment, useState } from 'react';
import {
  Box as BoxIcon,
  Filter as FilterIcon,
  Radio as RadioIcon,
  Send as SendIcon,
  Users as UsersIcon,
} from 'react-feather';

import ServiceRequestInfo from '../components/data/ServiceRequestInfo';
import ClearableTextInput from '../components/inputs/ClearableTextInput';
import Header from '../components/layouts/Header';
import Empty from '../components/misc/Empty';
import VerticalDivider from '../components/misc/VerticalDivider';

function RequestsPage() {
  const [deviceId, setDeviceId] = useState('');
  const [organizationId, setOrganizationId] = useState('');
  const [serviceName, setServiceName] = useState('');
  const [requestId, setRequestId] = useState('');

  return (
    <>
      <Header title="Requests">
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
          <Button leftIcon={<FilterIcon size={16} />} color="blue">
            Filter
          </Button>
        </Group>
      </Header>
      <Box mt="lg">
        <Paper shadow="xs">
          <Empty sx={(theme) => ({ padding: theme.spacing.xl })}>
            <Text color="gray" size="sm" mt="md">
              Requests not found
            </Text>
          </Empty>
          {new Array(5).fill(0).map((_, i) => (
            <Fragment key={i}>
              <ServiceRequestInfo
                request={{
                  id: '51a877fb-8959-4ab8-898b-ebdf2fa21f7c',
                  time: moment(),
                  service: { name: 'service1', deviceId: '', organizationId: 'Org1' },
                  method: 'GET',
                  args: ['1', '2', '3'],
                }}
                response={{
                  requestId: '51a877fb-8959-4ab8-898b-ebdf2fa21f7c',
                  time: moment(),
                  statusCode: 1,
                  returnValue: 'GET,1,2,3',
                }}
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
      title: 'Requests',
    },
  };
}

export default RequestsPage;
