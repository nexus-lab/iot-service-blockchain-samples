import { BrowserQRCodeSvgWriter } from '@zxing/browser';
import { useEffect, useMemo, useRef } from 'react';

interface QrcodeProps {
  content: string;
  width: number;
  height: number;
}

export default function Qrcode({ content, width, height }: QrcodeProps) {
  const container = useRef<HTMLDivElement>(null);
  const writer = useMemo(() => new BrowserQRCodeSvgWriter(), []);
  const svg = useMemo(() => writer.write(content, width, height), [writer, content, width, height]);

  useEffect(() => {
    const { current: parent } = container;
    if (parent) {
      while (parent.firstChild) {
        parent.firstChild.remove();
      }
      parent.appendChild(svg);
    }
  }, [svg]);

  return <div ref={container}></div>;
}
