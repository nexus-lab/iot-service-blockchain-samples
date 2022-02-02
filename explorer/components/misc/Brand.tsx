import { Box, Group, Text, ThemeIcon } from '@mantine/core';
import { Globe as GlobeIcon } from 'react-feather';

export default function Brand() {
  return (
    <Box sx={(theme) => ({ padding: theme.spacing.xs })}>
      <Group spacing="sm" align="center">
        <ThemeIcon
          size="lg"
          radius="lg"
          variant="gradient"
          gradient={{ from: 'indigo', to: 'cyan' }}>
          <GlobeIcon size={20} />
        </ThemeIcon>
        <Text
          m={0}
          component="h1"
          variant="gradient"
          gradient={{ from: 'indigo', to: 'cyan', deg: 45 }}
          sx={(theme) => ({ ...theme.headings.sizes.h3, fontFamily: theme.headings.fontFamily })}>
          IBS Explorer
        </Text>
      </Group>
    </Box>
  );
}
