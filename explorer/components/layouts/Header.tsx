import { Box, Divider, Title } from '@mantine/core';
import React from 'react';

interface HeaderProps {
  title: string;
  children?: React.ReactNode;
}

export default function Header({ title, children }: HeaderProps) {
  return (
    <>
      <Box my="lg">
        <Title order={2} mb="lg" sx={(theme) => ({ ...theme.headings.sizes.h1 })}>
          {title}
        </Title>
        {children}
      </Box>
      <Divider />
    </>
  );
}
