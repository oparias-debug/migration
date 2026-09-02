import { useEffect, useRef } from 'react';
import flatpickr from 'flatpickr';
import type { Instance } from 'flatpickr/dist/types/instance';

interface DatePickerInputProps {
  value: string;
  onChange: (value: string) => void;
  hasError?: boolean;
  name?: string;
}

// Equivalente a la clase js-datepicker + initDatepicker() de app.js (front Java
// original): mismo flatpickr, mismo dateFormat 'Y-m-d', allowInput habilitado.
export function DatePickerInput({ value, onChange, hasError, name }: DatePickerInputProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const flatpickrRef = useRef<Instance | null>(null);

  useEffect(() => {
    if (!inputRef.current) return;
    flatpickrRef.current = flatpickr(inputRef.current, {
      allowInput: true,
      dateFormat: 'Y-m-d',
      onChange: ([date]) => {
        if (date) {
          const iso = date.toISOString().slice(0, 10);
          onChange(iso);
        }
      },
    });
    return () => flatpickrRef.current?.destroy();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    flatpickrRef.current?.setDate(value || '', false);
  }, [value]);

  return (
    <input
      ref={inputRef}
      name={name}
      className={hasError ? 'malo' : undefined}
      defaultValue={value}
      onChange={(e) => onChange(e.target.value)}
    />
  );
}
