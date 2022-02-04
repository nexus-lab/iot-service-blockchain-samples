import { Box, Collapse, Divider, Group, Paper, Text, ThemeIcon } from '@mantine/core';
import moment from 'moment';
import { useState } from 'react';
import {
  Box as BoxIcon,
  Clock as ClockIcon,
  Code as CodeIcon,
  File as FileIcon,
  Grid as GridIcon,
  Hash as HashIcon,
  Radio as RadioIcon,
  Send as SendIcon,
  Terminal as TerminalIcon,
  Users as UsersIcon,
} from 'react-feather';

import ServiceRequestResponse from '../../types/requests';
import Empty from '../misc/Empty';
import VerticalDivider from '../misc/VerticalDivider';
import PropertyList from './PropertyList';

export default function ServiceRequestInfo({ request, response }: ServiceRequestResponse) {
  const [expanded, setExpanded] = useState(false);

  const formattedRequestTime = moment(request.time).format('YYYY-MM-DD HH:mm:ss');
  const requestProperties = [
    { name: 'Request ID', value: request.id, icon: <HashIcon size={16} /> },
    { name: 'Request Time', value: formattedRequestTime, icon: <ClockIcon size={16} /> },
    { name: 'Service Name', value: request.service.name, icon: <RadioIcon size={16} /> },
    {
      name: 'Service Organization ID',
      value: request.service.organizationId,
      icon: <UsersIcon size={16} />,
    },
    { name: 'Service Device ID', value: request.service.deviceId, icon: <BoxIcon size={16} /> },
    { name: 'Request Method', value: request.method, icon: <CodeIcon size={16} /> },
    { name: 'Request Arguments', value: request.args?.join('\n'), icon: <GridIcon size={16} /> },
  ];

  const formattedResponseTime = moment(request.time).format('YYYY-MM-DD HH:mm:ss');
  const responseProperties = [
    { name: 'Response Time', value: formattedResponseTime, icon: <ClockIcon size={16} /> },
    {
      name: 'Response Status Code',
      value: response ? String(response?.statusCode) : undefined,
      icon: <TerminalIcon size={16} />,
    },
    { name: 'Response Return Value', value: response?.returnValue, icon: <FileIcon size={16} /> },
  ];

  return (
    <>
      <Paper padding="md" onClick={() => setExpanded((v) => !v)} sx={{ cursor: 'pointer' }}>
        <Group noWrap>
          <ThemeIcon variant="light">
            <SendIcon size={16} />
          </ThemeIcon>
          <Box sx={{ flexGrow: 1, flexShrink: 1, overflow: 'hidden' }} title="Request ID">
            <Text sx={{ textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{request.id}</Text>
          </Box>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Service Organization ID">
            <ThemeIcon variant="light" color="gray">
              <UsersIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {request.service.organizationId}
            </Text>
          </Group>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Service Name">
            <ThemeIcon variant="light" color="gray">
              <RadioIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {request.service.name}
            </Text>
          </Group>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Request Time">
            <ThemeIcon variant="light" color="gray">
              <ClockIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {formattedRequestTime}
            </Text>
          </Group>
        </Group>
      </Paper>
      <Collapse in={expanded}>
        <Divider />
        <Paper padding="md">
          <Group sx={{ alignItems: 'flex-start' }}>
            <PropertyList sx={{ flexGrow: 1, flexBasis: 1 }} properties={requestProperties} />
            <VerticalDivider />
            {Boolean(response) ? (
              <PropertyList sx={{ flexGrow: 1, flexBasis: 1 }} properties={responseProperties} />
            ) : (
              <Empty sx={{ flexGrow: 1, flexBasis: 1 }}>
                <Text color="gray" size="sm" mt="md">
                  Response not found
                </Text>
              </Empty>
            )}
          </Group>
        </Paper>
      </Collapse>
    </>
  );
}
