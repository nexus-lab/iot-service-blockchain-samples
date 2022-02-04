import '@fontsource/raleway/400.css';
import '@fontsource/raleway/700.css';
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/700.css';
import { MantineProvider, MantineThemeOverride } from '@mantine/core';
import { NotificationsProvider } from '@mantine/notifications';
import type { AppProps } from 'next/app';
import Head from 'next/head';

import DefaultLayout from '../components/layouts/DefaultLayout';

export default function App({ Component, pageProps }: AppProps) {
  const { title } = pageProps; // eslint-disable-line @typescript-eslint/no-unsafe-assignment
  const theme: MantineThemeOverride = {
    fontFamily: 'Roboto',
    headings: { fontFamily: 'Raleway' },
    colorScheme: 'light',
  };

  return (
    <>
      <Head>
        <title>{['IBS Explorer', title].filter(Boolean).join(' - ')}</title>
        <meta name="viewport" content="minimum-scale=1, initial-scale=1, width=device-width" />
      </Head>
      <MantineProvider theme={theme} withGlobalStyles withNormalizeCSS>
        <NotificationsProvider>
          <DefaultLayout>
            <Component {...pageProps} />
          </DefaultLayout>{' '}
        </NotificationsProvider>
      </MantineProvider>
    </>
  );
}
