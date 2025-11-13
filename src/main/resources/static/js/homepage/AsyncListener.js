/**
 * async 함수를 실행하는 동안은 이벤트 핸들러가 실행되지 않도록 합니다.
 *
 * @param {HTMLElement} element - 이벤트를 추가할 HTML 요소
 * @param {string} eventName - 이벤트 이름 (예: 'click')
 * @param {function(Event): Promise<void>} callback - 이벤트 발생 시 실행될 비동기 콜백 함수
 */
function addAsyncListener(element, eventName, callback) {
    const func = async (e) => {
        await callback(e);

        element?.removeEventListener(eventName, func, {once: true});
    }

    element?.addEventListener(eventName, func, { once: true });
}

export {addAsyncListener};