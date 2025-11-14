import cacheManager from '../CacheManager.js';
// cache-fn.js에서 cacheFetch와 retryFetch를 export해야 합니다.
// 현재 파일에서는 함수가 export되지 않았으므로, 테스트를 위해 임시로 수정이 필요합니다.
// 이 예제에서는 cache-fn.js가 수정되었다고 가정하고 진행합니다.
// 예: export { cacheFetch, retryFetch };
import { cacheFetch, retryFetch } from '../cache-fn.js';

// 전역 fetch를 모킹합니다.
global.fetch = jest.fn();

// Jest의 fake timers를 사용하여 시간 기반 테스트(retryDelay)를 제어합니다.
jest.useFakeTimers();

describe('cache-fn', () => {
    beforeEach(() => {
        // 각 테스트 전에 fetch 모의 함수와 캐시를 초기화합니다.
        fetch.mockClear();
        cacheManager._cacheStore.clear();
        jest.clearAllTimers();
    });

    describe('cacheFetch', () => {
        const key = 'api_data';
        const url = 'https://api.example.com/data';
        const mockData = { result: 'success' };

        test('캐시가 없을 때 fetch를 호출하고 결과를 캐시에 저장한다.', async () => {
            fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });

            const data = await cacheFetch(key, url);

            expect(fetch).toHaveBeenCalledTimes(1);
            expect(fetch).toHaveBeenCalledWith(url);
            expect(data).toEqual(mockData);
            expect(cacheManager.getData(key)).toEqual(mockData);
        });

        test('캐시가 있을 때 fetch를 호출하지 않고 캐시된 데이터를 반환한다.', async () => {
            cacheManager.saveData(key, mockData);

            const data = await cacheFetch(key, url);

            expect(fetch).not.toHaveBeenCalled();
            expect(data).toEqual(mockData);
        });

        test('`force: true` 옵션이 있으면 캐시 유무와 관계없이 fetch를 호출한다.', async () => {
            fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => ({ result: 'forced_refresh' }),
            });
            cacheManager.saveData(key, mockData); // 기존 캐시 데이터

            const data = await cacheFetch(key, url, { force: true });

            expect(fetch).toHaveBeenCalledTimes(1);
            expect(data).toEqual({ result: 'forced_refresh' });
            // force: true는 캐시를 업데이트하지는 않으므로, 기존 캐시는 그대로여야 합니다.
            // 만약 업데이트를 원한다면 cacheFetch 로직 수정이 필요합니다.
            expect(cacheManager.getData(key)).toEqual(mockData);
        });
    });

    describe('retryFetch', () => {
        const url = 'https://api.example.com/data';
        const mockData = { result: 'success' };

        test('fetch가 즉시 성공하면 데이터를 한 번만 호출하고 반환한다.', async () => {
            fetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });

            const data = await retryFetch(url, 3, 1000);

            expect(fetch).toHaveBeenCalledTimes(1);
            expect(data).toEqual(mockData);
        });

        test('fetch가 실패하면 지정된 횟수만큼 재시도한다.', async () => {
            fetch.mockRejectedValue(new Error('Network Error'));

            const promise = retryFetch(url, 3, 1000);

            // 3번의 시도와 2번의 딜레이가 발생해야 합니다.
            await jest.advanceTimersByTimeAsync(0); // 1st try
            expect(fetch).toHaveBeenCalledTimes(1);

            await jest.advanceTimersByTimeAsync(1000); // 1st delay -> 2nd try
            expect(fetch).toHaveBeenCalledTimes(2);

            await jest.advanceTimersByTimeAsync(1000); // 2nd delay -> 3rd try
            expect(fetch).toHaveBeenCalledTimes(3);

            await expect(promise).rejects.toThrow('Network Error');
        });

        test('재시도 중 fetch가 성공하면 즉시 데이터를 반환한다.', async () => {
            fetch
                .mockRejectedValueOnce(new Error('Network Error')) // 1st try fails
                .mockResolvedValueOnce({ // 2nd try succeeds
                    ok: true,
                    json: async () => mockData,
                });

            const promise = retryFetch(url, 3, 1000);

            await jest.advanceTimersByTimeAsync(0); // 1st try
            expect(fetch).toHaveBeenCalledTimes(1);

            await jest.advanceTimersByTimeAsync(1000); // 1st delay -> 2nd try
            
            const data = await promise;

            expect(fetch).toHaveBeenCalledTimes(2);
            expect(data).toEqual(mockData);
        });

        test('모든 재시도가 실패하면 마지막 에러를 throw한다.', async () => {
            const error1 = new Error('First Error');
            const error2 = new Error('Last Error');
            fetch.mockRejectedValueOnce(error1).mockRejectedValue(error2);

            const promise = retryFetch(url, 2, 1000);

            await jest.advanceTimersByTimeAsync(0);
            await jest.advanceTimersByTimeAsync(1000);

            await expect(promise).rejects.toThrow('Last Error');
            expect(fetch).toHaveBeenCalledTimes(2);
        });
    });
});
