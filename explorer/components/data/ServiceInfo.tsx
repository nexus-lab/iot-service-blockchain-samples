import { Box, Button, Collapse, Divider, Group, Paper, Text, ThemeIcon } from '@mantine/core';
import moment from 'iot-service-blockchain/sdk/javascript/moment';
import { useState } from 'react';
import {
  Box as BoxIcon,
  Clock as ClockIcon,
  Copy as CopyIcon,
  Radio as RadioIcon,
  Send as SendIcon,
  Tag as TagIcon,
  Users as UsersIcon,
} from 'react-feather';

import PropertyList from './PropertyList';

interface ServiceInfoProps {
  name: string;
  deviceId: string;
  organizationId: string;
  version: number;
  description?: string;
  lastUpdateTime: moment.Moment;
}

export default function ServiceInfo({
  name,
  deviceId,
  organizationId,
  version,
  description,
  lastUpdateTime,
}: ServiceInfoProps) {
  const [expanded, setExpanded] = useState(false);

  const properties = [
    { name: 'Name', value: name, icon: <TagIcon size={16} /> },
    { name: 'Device ID', value: deviceId, icon: <BoxIcon size={16} /> },
    { name: 'Organization ID', value: organizationId, icon: <UsersIcon size={16} /> },
    { name: 'Version', value: String(version), icon: <CopyIcon size={16} /> },
    { name: 'Description', value: description },
    {
      name: 'Last Update Time',
      value: lastUpdateTime.format('YYYY-MM-DD HH:mm:ss'),
      icon: <ClockIcon size={16} />,
    },
  ];

  return (
    <>
      <Paper padding="md" onClick={() => setExpanded((v) => !v)} sx={{ cursor: 'pointer' }}>
        <Group noWrap>
          <ThemeIcon variant="light">
            <RadioIcon size={16} />
          </ThemeIcon>
          <Box sx={{ flexGrow: 1, flexShrink: 1, overflow: 'hidden' }} title="Service Name">
            <Text sx={{ textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{name}</Text>
          </Box>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Organization ID">
            <ThemeIcon variant="light" color="gray">
              <UsersIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {organizationId}
            </Text>
          </Group>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Version">
            <ThemeIcon variant="light" color="gray">
              <CopyIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              Version {version}
            </Text>
          </Group>
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Last Update Time">
            <ThemeIcon variant="light" color="gray">
              <ClockIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {lastUpdateTime.format('YYYY-MM-DD HH:mm:ss')}
            </Text>
          </Group>
          <Button leftIcon={<SendIcon size={16} />} variant="light" size="xs">
            Go to Requests
          </Button>
        </Group>
      </Paper>
      <Collapse in={expanded}>
        <Divider />
        <Paper padding="md">
          <PropertyList properties={properties} />
        </Paper>
      </Collapse>
    </>
  );
}
