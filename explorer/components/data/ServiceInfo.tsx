import { Box, Button, Collapse, Divider, Group, Paper, Text, ThemeIcon } from '@mantine/core';
import moment from 'moment';
import { useRouter } from 'next/router';
import { useState } from 'react';
import {
  Box as BoxIcon,
  Clock as ClockIcon,
  Copy as CopyIcon,
  Radio as RadioIcon,
  Tag as TagIcon,
  Users as UsersIcon,
} from 'react-feather';

import Service from '../../types/service';
import Qrcode from '../misc/Qrcode';
import VerticalDivider from '../misc/VerticalDivider';
import PropertyList from './PropertyList';

export default function ServiceInfo({
  name,
  deviceId,
  organizationId,
  version,
  description,
  lastUpdateTime,
}: Service) {
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);

  const formattedLastUpdateTime = moment(lastUpdateTime).format('YYYY-MM-DD HH:mm:ss');
  const properties = [
    { name: 'Name', value: name, icon: <TagIcon size={16} /> },
    { name: 'Device ID', value: deviceId, icon: <BoxIcon size={16} /> },
    { name: 'Organization ID', value: organizationId, icon: <UsersIcon size={16} /> },
    { name: 'Version', value: String(version), icon: <CopyIcon size={16} /> },
    { name: 'Description', value: description },
    { name: 'Last Update Time', value: formattedLastUpdateTime, icon: <ClockIcon size={16} /> },
  ];

  const handleQuickLinkClick: React.MouseEventHandler<HTMLButtonElement> = (event) => {
    event.stopPropagation();
    void router.push({
      pathname: '/requests',
      query: { organizationId, deviceId, serviceName: name },
    });
  };

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
              {formattedLastUpdateTime}
            </Text>
          </Group>
          <Button variant="subtle" size="xs" onClick={handleQuickLinkClick}>
            REQUESTS
          </Button>
        </Group>
      </Paper>
      <Collapse in={expanded}>
        <Divider />
        <Paper padding="md">
          <Group sx={{ alignItems: 'flex-start' }}>
            <PropertyList properties={properties} sx={{ flexGrow: 2, flexBasis: 1 }} />
            <VerticalDivider />
            <Box
              sx={{
                flexGrow: 1,
                flexBasis: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                alignSelf: 'stretch',
              }}>
              <Qrcode
                width={300}
                height={300}
                content={JSON.stringify({ organizationId, deviceId, serviceName: name })}
              />
            </Box>
          </Group>
        </Paper>
      </Collapse>
    </>
  );
}
