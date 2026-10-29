import { useCallback, useEffect, useState } from 'react';
import api, { errorText } from './LuanLHCE191099_api.js';
export default function useApiList(path) {
  const [result, setResult] = useState({ key: null, data: [], error: '' });
  const [version, setVersion] = useState(0);
  const requestKey = JSON.stringify([path, version]);
  const reload = useCallback(() => setVersion((value) => value + 1), []);
  useEffect(() => {
    const controller = new AbortController();
    api
      .get(path, { signal: controller.signal })
      .then((response) => {
        if (!controller.signal.aborted)
          setResult({ key: requestKey, data: response.data, error: '' });
      })
      .catch((error) => {
        if (!controller.signal.aborted)
          setResult({ key: requestKey, data: [], error: errorText(error) });
      });
    return () => controller.abort();
  }, [path, requestKey]);
  return {
    data: result.key === requestKey ? result.data : [],
    error: result.key === requestKey ? result.error : '',
    loading: result.key !== requestKey,
    reload,
  };
}
