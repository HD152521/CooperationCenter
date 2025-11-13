import cacheManager from "./cahceManager.mjs";

//캐싱 시간에 따른 캐싱 여부 테스트 함수
(async () => {
    // 1) 첫 호출 → fetch 실행
    const a = await cacheFetch(
        "test",
        "https://jsonplaceholder.typicode.com/posts/1"
    );
    console.log("첫 결과:", a);
    await new Promise((resolve) => setTimeout(resolve, 5000));
    console.log("5초 후...");
    // 2) 두 번째 호출 → 캐시 반환
    const b = await cacheFetch(
        "test",
        "https://jsonplaceholder.typicode.com/posts/1"
    );
    console.log("두번째 결과:", b);
    await new Promise((resolve) => setTimeout(resolve, 5000));
    console.log("5초 후...");
    // 3) 세 번째 호출 → 캐시 반환
    const c = await cacheFetch(
        "test",
        "https://jsonplaceholder.typicode.com/posts/1"
    );
    console.log("세번째 결과:", c);
})();

// 실패 시 재요청 되는지 여부 테스트
(async () => {
    try {
        await cacheFetch("test", "https://jsonplaceholder.typicodem/posts/1", {
            maxAge: 6000,
            retryCount: 2,
            retryDelay: 1000,
        });
    } catch (err) {
        console.error(err);
    }
})();


async function cacheFetch(key, url, options = {}) {
    const { maxAge = 8000, retryCount = 3, retryDelay, force = false } = options;
    const cached = cacheManager.getData(key);

    if (force) {
        const data = await retryFetch(url, retryCount, retryDelay);
        return data;
    }

    if (cached !== undefined) {
        console.log("캐시 반환:", key);
        return cached;
    }

    console.log("서버 fetch:", key);

    const data = await retryFetch(url, retryCount, retryDelay);

    cacheManager.saveData(key, data, { maxAge: maxAge });

    return data;
}

async function retryFetch(url, retryCount = 3, retryDelay = 0) {
    let lastError;

    if (retryCount <= 0) {
        try {
            const res = await fetch(url);
            if (!res.ok) {
                throw new Error("서버 응답 에러");
            }
            const data = await res.json();
            return data;
        } catch (err) {
            throw err;
        }
    }

    for (let i = 0; i < retryCount; i++) {
        try {
            const res = await fetch(url);
            if (!res.ok) {
                throw new Error("서버 응답 에러");
            }
            const data = await res.json();
            return data;
        } catch (err) {
            lastError = err;
            console.log(`재시도 ${i + 1}/${retryCount}`, err);
            await new Promise((resolve) => setTimeout(resolve, retryDelay));
        }
    }
    throw lastError;
}
