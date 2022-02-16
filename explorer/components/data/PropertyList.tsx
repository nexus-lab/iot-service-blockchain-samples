import { DefaultProps, List, Text, ThemeIcon } from '@mantine/core';
import { AlignLeft as AlignLeftIcon } from 'react-feather';

interface PropertyListProps extends DefaultProps {
  properties: { name: string; value?: string; icon?: React.ReactNode }[];
}

export default function PropertyList({ properties, ...props }: PropertyListProps) {
  return (
    <List spacing="xs" {...props}>
      {properties.map(({ name, value, icon }) => (
        <List.Item
          key={name}
          icon={
            <ThemeIcon color="gray" variant="light">
              {icon ?? <AlignLeftIcon size={16} />}
            </ThemeIcon>
          }>
          <Text weight={600} size="xs">
            {name}
          </Text>
          <Text sx={{ wordBreak: 'break-all' }}>
            {value === undefined ? '(NOT SET)' : value === '' ? '(EMPTY)' : value}
          </Text>
        </List.Item>
      ))}
    </List>
  );
}
