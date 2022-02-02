import { AppShell, Container, Divider, MantineTheme, Navbar, ScrollArea } from '@mantine/core';
import React from 'react';

import Brand from '../misc/Brand';
import MainNav from '../navigation/MainNav';

export default function DefaultLayout({ children }: { children: React.ReactNode }) {
  const styles = (theme: MantineTheme) => ({
    main: {
      paddingTop: 0,
      paddingBottom: 0,
      backgroundColor: theme.colors.gray[0],
    },
  });

  return (
    <AppShell
      fixed
      styles={styles}
      navbar={
        <Navbar fixed position={{ top: 0, left: 0 }} padding="xs" width={{ base: 300 }}>
          <Navbar.Section mb="md">
            <Brand />
          </Navbar.Section>
          <Divider />
          <Navbar.Section grow mt="md">
            <MainNav />
          </Navbar.Section>
        </Navbar>
      }>
      <ScrollArea style={{ height: '100vh' }}>
        <Container
          size="lg"
          sx={(theme) => ({ paddingTop: theme.spacing.md, paddingBottom: theme.spacing.xl })}>
          {children}
        </Container>
      </ScrollArea>
    </AppShell>
  );
}
