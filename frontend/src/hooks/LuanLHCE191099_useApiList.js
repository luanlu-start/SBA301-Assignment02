import { useCallback, useEffect, useState } from 'react';
import api, { errorText } from '../services/LuanLHCE191099_api.js';

// Fetch a list whenever its URL or reload version changes.
export default function useApiList(path) {
  const [result, setResult] = useState({ requestKey: null, data: [], error: '' });
  const [reloadVersion, setReloadVersion] = useState(0);
  const requestKey = path + '|reload=' + reloadVersion;

  const reload = useCallback(() => {
    setReloadVersion((currentVersion) => currentVersion + 1);
  }, []);

  useEffect(() => {
    const controller = new AbortController();

    async function loadRecords() {
      try {
        const response = await api.get(path, { signal: controller.signal });
        if (!controller.signal.aborted) {
          setResult({ requestKey, data: response.data, error: '' });
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setResult({ requestKey, data: [], error: errorText(error) });
        }
      }
    }

    loadRecords();
    // Cancel obsolete searches so an older response cannot replace newer results.
    return () => controller.abort();
  }, [path, requestKey]);

  const isCurrentResult = result.requestKey === requestKey;
  return {
    data: isCurrentResult ? result.data : [],
    error: isCurrentResult ? result.error : '',
    loading: !isCurrentResult,
    reload,
  };
}
