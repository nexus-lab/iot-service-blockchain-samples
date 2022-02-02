import { Box, Button, Collapse, Divider, Group, Paper, Text, ThemeIcon } from '@mantine/core';
import moment from 'iot-service-blockchain/sdk/javascript/moment';
import { useState } from 'react';
import {
  Box as BoxIcon,
  Clock as ClockIcon,
  Hash as HashIcon,
  Radio as RadioIcon,
  Tag as TagIcon,
  Users as UsersIcon,
} from 'react-feather';

import PropertyList from './PropertyList';

interface DeviceInfoProps {
  id: string;
  organizationId: string;
  name?: string;
  description?: string;
  lastUpdateTime: moment.Moment;
}

export default function DeviceInfo({
  id,
  organizationId,
  name,
  description,
  lastUpdateTime,
}: DeviceInfoProps) {
  const [expanded, setExpanded] = useState(false);

  const properties = [
    { name: 'Name', value: name, icon: <TagIcon size={16} /> },
    { name: 'Device ID', value: id, icon: <HashIcon size={16} /> },
    { name: 'Organization ID', value: organizationId, icon: <UsersIcon size={16} /> },
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
            <BoxIcon size={16} />
          </ThemeIcon>
          <Box sx={{ flexGrow: 1, flexShrink: 1, overflow: 'hidden' }} title="Device Name">
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
          <Group spacing="xs" sx={{ flexShrink: 0 }} title="Last Update Time">
            <ThemeIcon variant="light" color="gray">
              <ClockIcon size={16} />
            </ThemeIcon>
            <Text size="sm" color="gray">
              {lastUpdateTime.format('YYYY-MM-DD HH:mm:ss')}
            </Text>
          </Group>
          <Button leftIcon={<RadioIcon size={16} />} variant="light" size="xs">
            Go to Services
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
