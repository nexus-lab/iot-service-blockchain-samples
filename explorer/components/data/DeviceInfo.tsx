import { Box, Button, Collapse, Divider, Group, Paper, Text, ThemeIcon } from '@mantine/core';
import moment from 'moment';
import { useRouter } from 'next/router';
import React, { useState } from 'react';
import {
  Box as BoxIcon,
  Clock as ClockIcon,
  Hash as HashIcon,
  Tag as TagIcon,
  Users as UsersIcon,
} from 'react-feather';

import Device from '../../types/device';
import PropertyList from './PropertyList';

export default function DeviceInfo({
  id,
  organizationId,
  name,
  description,
  lastUpdateTime,
}: Device) {
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);

  const formattedLastUpdateTime = moment(lastUpdateTime).format('YYYY-MM-DD HH:mm:ss');
  const properties = [
    { name: 'Name', value: name, icon: <TagIcon size={16} /> },
    { name: 'Device ID', value: id, icon: <HashIcon size={16} /> },
    { name: 'Organization ID', value: organizationId, icon: <UsersIcon size={16} /> },
    { name: 'Description', value: description },
    { name: 'Last Update Time', value: formattedLastUpdateTime, icon: <ClockIcon size={16} /> },
  ];

  const handleQuickLinkClick: React.MouseEventHandler<HTMLButtonElement> = (event) => {
    event.stopPropagation();
    void router.push({ pathname: '/services', query: { organizationId, deviceId: id } });
  };

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
              {formattedLastUpdateTime}
            </Text>
          </Group>
          <Button size="xs" variant="subtle" onClick={handleQuickLinkClick}>
            SERVICES
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
