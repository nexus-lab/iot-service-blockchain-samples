import { ActionIcon, TextInput, TextInputProps } from '@mantine/core';
import { useCallback, useEffect } from 'react';
import { useRef, useState } from 'react';
import { X as XIcon } from 'react-feather';

export default function ClearableTextInput({ value, onChange, ...props }: TextInputProps) {
  const ref = useRef<HTMLInputElement>(null);
  const [isClearButtonVisible, setClearButtonVisible] = useState(false);

  const handleClear: React.MouseEventHandler<HTMLButtonElement> = useCallback(() => {
    if (ref.current) {
      const inputPrototype = window.HTMLInputElement.prototype;
      Object.getOwnPropertyDescriptor(inputPrototype, 'value')?.set?.call(ref.current.value, '');
      ref.current.dispatchEvent(new Event('change', { bubbles: true }));
    }
  }, []);

  useEffect(() => {
    setClearButtonVisible(Boolean(value));
  }, [value]);

  return (
    <TextInput
      ref={ref}
      value={value}
      onChange={onChange}
      rightSection={
        isClearButtonVisible ? (
          <ActionIcon onClick={handleClear} color="gray">
            <XIcon size={16} />
          </ActionIcon>
        ) : null
      }
      {...props}
    />
  );
}
