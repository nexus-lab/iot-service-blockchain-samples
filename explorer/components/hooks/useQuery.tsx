import { AxiosError } from 'axios';
import { useState } from 'react';

export type QueryResult = [
  () => Promise<void>,
  { isLoading: boolean; isError: boolean; isCompleted: boolean; reset: () => void }
];

export default function useQuery<T>(
  queryFn: () => T | Promise<T>,
  onSuccess: (data: T) => void,
  onError: (error: Error) => void
): QueryResult {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isError, setIsError] = useState<boolean>(false);
  const [isCompleted, setIsCompleted] = useState<boolean>(false);

  const reset = () => {
    setIsLoading(false);
    setIsError(false);
    setIsCompleted(false);
  };

  const searchFn = async () => {
    setIsLoading(true);
    setIsError(false);
    setIsCompleted(false);

    try {
      const data = await queryFn();
      setIsCompleted(true);
      onSuccess(data);
    } catch (e) {
      setIsError(true);

      const error = e as Error;
      const { message } = (error as AxiosError).response?.data as { message: string };
      error.message = message ?? error.message;
      onError(error);
    } finally {
      setIsLoading(false);
    }
  };

  return [searchFn, { isLoading, isError, isCompleted, reset }];
}
