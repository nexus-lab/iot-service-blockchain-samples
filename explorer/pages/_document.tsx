import { createGetInitialProps } from '@mantine/next';
import { Head, Html, Main, NextScript } from 'next/document';

export const getInitialProps = createGetInitialProps();

export default function Document() {
  return (
    <Html>
      <Head />
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}
