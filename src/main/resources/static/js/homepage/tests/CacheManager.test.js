import cacheManager from '../common/CacheManager.js';

// Jest의 fake timers를 사용하여 시간 기반 테스트(maxAge)를 제어합니다.
jest.useFakeTimers();

describe('CacheManager', () => {
    // 각 테스트 전에 캐시를 초기화하여 테스트 간 독립성을 보장합니다.
    beforeEach(() => {
        // CacheManager는 싱글톤 인스턴스이므로, 내부 상태를 직접 초기화해야 합니다.
        // 이를 위해 CacheManager에 clear() 메서드를 추가하는 것을 고려할 수 있습니다.
        // 지금은 private 속성에 직접 접근하여 초기화합니다.
        cacheManager._cacheStore.clear();
    });

    describe('데이터 저장 및 조회 (saveData, getData)', () => {
        test('새로운 키에 데이터를 저장하고 조회할 수 있다.', () => {
            const key = 'user';
            const value = { id: 1, name: 'John Doe' };
            cacheManager.saveData(key, value);
            expect(cacheManager.getData(key)).toEqual(value);
        });

        test('기존 키의 데이터를 업데이트할 수 있다.', () => {
            const key = 'user';
            const initialValue = { id: 1, name: 'John Doe' };
            const updatedValue = { id: 1, name: 'Jane Doe' };

            cacheManager.saveData(key, initialValue);
            expect(cacheManager.getData(key)).toEqual(initialValue);

            cacheManager.saveData(key, updatedValue);
            expect(cacheManager.getData(key)).toEqual(updatedValue);
        });

        test('`undefined`나 `null`을 값으로 저장할 수 있다.', () => {
            const key1 = 'undefined_val';
            cacheManager.saveData(key1, undefined);
            expect(cacheManager.getData(key1)).toBeUndefined();

            const key2 = 'null_val';
            cacheManager.saveData(key2, null);
            expect(cacheManager.getData(key2)).toBeNull();
        });
    });

    describe('캐시 무효화 (invalidate)', () => {
        test('`invalidate` 호출 시 데이터가 undefined로 설정된다.', () => {
            const key = 'session';
            const value = { token: 'xyz' };
            cacheManager.saveData(key, value);

            cacheManager.invalidate(key);
            expect(cacheManager.getData(key)).toBeUndefined();
        });

        test('존재하지 않는 키를 무효화해도 오류가 발생하지 않는다.', () => {
            expect(() => cacheManager.invalidate('non_existent_key')).not.toThrow();
        });
    });

    describe('MaxAgePlugin (유효 기간 관리)', () => {
        test('`maxAge`가 지나면 캐시가 무효화된다.', () => {
            const key = 'temporary_data';
            const value = 'this will expire';
            const maxAge = 10000; // 10초

            cacheManager.saveData(key, value, { maxAge });

            // 9초 후, 데이터는 여전히 유효해야 합니다.
            jest.advanceTimersByTime(9000);
            expect(cacheManager.getData(key)).toBe(value);

            // 1초가 더 지나 총 10초가 경과하면, 데이터는 무효화되어야 합니다.
            jest.advanceTimersByTime(1000);
            expect(cacheManager.getData(key)).toBeUndefined();
        });

        test('`maxAge`를 500ms 미만으로 설정하면 500ms로 조정된다.', () => {
            const key = 'short_lived';
            const value = 'data';
            const maxAge = 400; // 500ms 미만

            cacheManager.saveData(key, value, { maxAge });

            // 450ms 후, 데이터는 여전히 유효해야 합니다 (500ms로 조정되었으므로).
            jest.advanceTimersByTime(450);
            expect(cacheManager.getData(key)).toBe(value);

            // 100ms가 더 지나 총 550ms가 경과하면, 데이터는 무효화되어야 합니다.
            jest.advanceTimersByTime(100);
            expect(cacheManager.getData(key)).toBeUndefined();
        });

        test('`maxAge`를 설정하지 않으면 캐시가 만료되지 않는다.', () => {
            const key = 'permanent_data';
            const value = 'this never expires';

            cacheManager.saveData(key, value);

            // 매우 긴 시간이 지나도 데이터는 유효해야 합니다.
            jest.advanceTimersByTime(1000 * 60 * 60 * 24 * 365); // 1년
            expect(cacheManager.getData(key)).toBe(value);
        });
    });

    describe('EventNotifierPlugin (변경 리스너)', () => {
        test('데이터 변경 시 리스너가 호출된다.', () => {
            const key = 'product';
            const listener = jest.fn();
            const initialValue = { name: 'Laptop', price: 1000 };
            const updatedValue = { name: 'Laptop', price: 950 };

            cacheManager.addChangeListener(key, listener);
            cacheManager.saveData(key, initialValue); // 첫 저장
            cacheManager.saveData(key, updatedValue); // 업데이트

            // 리스너는 총 2번 호출되어야 합니다.
            expect(listener).toHaveBeenCalledTimes(2);

            // 두 번째 호출(업데이트) 시의 인자를 검증합니다.
            const [prevObj, nowObject] = listener.mock.calls[1];
            expect(prevObj.value).toEqual(initialValue);
            expect(nowObject.value).toEqual(updatedValue);
        });

        test('리스너를 제거하면 더 이상 알림이 오지 않는다.', () => {
            const key = 'config';
            const listener = jest.fn();

            cacheManager.addChangeListener(key, listener);
            cacheManager.saveData(key, { theme: 'dark' });
            expect(listener).toHaveBeenCalledTimes(1);

            cacheManager.removeChangeListener(key, listener);
            cacheManager.saveData(key, { theme: 'light' });
            expect(listener).toHaveBeenCalledTimes(1); // 더 이상 호출되지 않음
        });

        test('하나의 키에 여러 리스너를 등록하고 모두 정상 동작한다.', () => {
            const key = 'multi_listeners';
            const listener1 = jest.fn();
            const listener2 = jest.fn();

            cacheManager.addChangeListener(key, listener1);
            cacheManager.addChangeListener(key, listener2);

            cacheManager.saveData(key, 'initial data');

            expect(listener1).toHaveBeenCalledTimes(1);
            expect(listener2).toHaveBeenCalledTimes(1);
        });
    });
});
