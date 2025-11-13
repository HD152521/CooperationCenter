/**
 * CacheManager 인스턴스 생성 및 내보내기
 */
const cacheManager = new CacheManager();
export default cacheManager;


class CacheManager {
    constructor() {
        /**
         * @private
         * 캐시 데이터를 저장하는 Map 객체.
         * key: 캐시 키, value: CacheObject 인스턴스
         */
        this._cacheStore = new Map();
        this._eventNotifierPlugin = new EventNotifierPlugin();
        /**
         * @private
         * 캐시 동작에 영향을 주는 플러그인 목록. 플러그인은 캐시 생성, 데이터 변경, 무효화, 접근 시점에 특정 로직을 수행할 수 있다.
         */
        this._plugins = [
            new MaxAgePlugin(),
            this._eventNotifierPlugin,
        ];
    }
    
    saveData(key, value, option) {
        const nowObj = this.#matchedCache(key);
        const prevObj = new CacheObject(nowObj);
        prevObj.freeze(); // prevObj 는 수정할 수 없어야 함.

        nowObj.value = value;
        nowObj.option = option ?? nowObj.option;

        this._plugins.forEach(plugin => {
            plugin.onDataChanged(prevObj, nowObj);
        });

        // 수정 후 invalidate 된 상태가 있을 수 있음.
        if (nowObj.isInvalidated()) {
            this._plugins.forEach(plugin => {
                plugin.onDataInvalidated(nowObj);
            });
        }
    }
    
    /**
     * 캐시에서 데이터를 조회한다.
     * @param {string} key 조회할 캐시의 키
     * @returns {*} 캐시된 데이터. 캐시가 없거나 유효하지 않은 경우 undefined 를 반환한다.
     * @public
     */
    getData(key) {
        const cacheObj = this.#matchedCache(key);

        this._plugins.forEach(plugin => {
            plugin.onDataFetch(cacheObj);
        });

        return cacheObj.value;
    }
    /**
     * 특정 캐시를 무효화한다.
     * 캐시된 데이터를 삭제하고, 플러그인에 알린다.
     * @param {string} key 무효화할 캐시의 키
     * @public
     */
    invalidate(key) {
        const cacheObj = this.#matchedCache(key, false);

        if (!cacheObj) return;

        cacheObj.invalidate();
        this._plugins.forEach(plugin => {
            plugin.onDataInvalidated(cacheObj);
        });
    }
    /**
     * 특정 캐시 키에 대한 변경 사항을 수신하는 리스너를 추가한다.
     * @param {string} key 캐시 키
     * @param {function} listener 변경 사항을 수신할 리스너 함수
     * @public
     */
    addChangeListener(key, listener) {
        const cacheObj = this.#matchedCache(key);
        this._eventNotifierPlugin.addChangeListener(cacheObj, listener);
    }
    /**
     * 특정 캐시 키에 대한 변경 사항을 수신하는 리스너를 제거한다.
     * @param {string} key 캐시 키
     * @param {function} listener 제거할 리스너 함수
     * @public
     *
     */

    removeChangeListener(key, listener) {
        const cacheObj = this.#matchedCache(key, false);
        if (!cacheObj) return;
        
        this._eventNotifierPlugin.removeChangeListener(cacheObj, listener);
    }

    /**
     * @private
     * 캐시 저장소에서 주어진 키에 해당하는 CacheObject 를 찾거나 생성한다.
     * @param key 캐시 키
     * @param returnDefaultValue 캐시가 없을 경우 기본값을 반환할지 여부
     */
    #matchedCache(key, returnDefaultValue=true) {
        let cache = this._cacheStore.get(key);
        if (!cache && returnDefaultValue) {
            let cache = new CacheObject(key);
            this._cacheStore.set(key, cache);

            this._plugins.forEach(plugin => {
                plugin.onCacheCreated(cache);
            });

            return cache;
        }

        return cache;
    }
}

/**
 * @class CacheObject
 * @description 캐시 항목을 나타내는 클래스
 */
class CacheObject {
    constructor(key, value=undefined, option = {}) {
        /* object가 들어오면, 객체 복사 */
        if (key instanceof CachePlugin) {
            this._key = key.key;
            this._value = key.value;
            this.option = key.option;

            return;
        }

        this._key = key;
        this._value = value;
        this.option = option;
    }

    get key() {
        return this._key;
    }

    get value() {
        return this._value;
    }

    set value(newValue) {
        this._value = newValue;
    }

    invalidate() {
        this.value = undefined;
    }

    isInvalidated() {
        return this.value === undefined || this.value === null;
    }

    freeze() {
        Object.freeze(this);
        Object.freeze(this._key);
        Object.freeze(this._value);
        Object.freeze(this.option);
    }
}

/**
 * @class CachePlugin
 * @description CacheManager 에 적용할 수 있는 플러그인의 기본 클래스.
 * 플러그인을 사용하면 캐시 동작에 사용자 정의 로직을 추가할 수 있다.
 * @abstract
 */
class CachePlugin {
    /**
     * 캐시 객체가 생성될 때 호출된다.
     * @param {CacheObject} cacheObj 새로 생성된 캐시 객체
     */
    onCacheCreated(cacheObj) {}
    /**
     * 캐시 데이터가 변경될 때 호출된다.
     * @param {CacheObject} prevObj 이전 캐시 객체
     * @param {CacheObject} nowObject 현재 캐시 객체
     */
    onDataChanged(prevObj, nowObject) {}
    /**
     * 캐시가 무효화될 때 호출된다.
     * @param {CacheObject} cacheObj 무효화된 캐시 객체
     */
    onDataInvalidated(cacheObj) {}
    /**
     * 캐시에서 데이터를 가져올 때 호출된다.
     * @param {CacheObject} cacheObj 접근 중인 캐시 객체
     */
    onDataFetch(cacheObj) {}
}

/**
 * @class MaxAgePlugin
 * @extends CachePlugin
 * @description 캐시 객체의 유효 기간을 관리하는 플러그인.
 */
class MaxAgePlugin extends CachePlugin {
    onCacheCreated(cacheObj) {
        super.onCacheCreated(cacheObj);

        if (!Number.isInteger(cacheObj.option.maxAge)) {
            cacheObj.option.maxAge = Infinity;
        }
        else if (cacheObj.option.maxAge < 500) {
            cacheObj.option.maxAge = 500;
        }
    }

    onDataChanged(prevObj, nowObject) {
        super.onDataChanged(prevObj, nowObject);

        if (!nowObject.isInvalidated()) {
            nowObject.option.lastModified = Date.now();
        }
    }

    onDataFetch(cacheObj) {
        super.onDataFetch(cacheObj);

        if (cacheObj.isInvalidated())
            return;

        if (cacheObj.option.lastModified + cacheObj.option.maxAge >= Date.now()) {
            cacheObj.invalidate();
        }
    }
}

/**
 * @class EventNotifierPlugin
 * @extends CachePlugin
 * @description 캐시 데이터 변경 시 리스너에게 알림을 보내는 플러그인.
 */
class EventNotifierPlugin extends CachePlugin {
    onCacheCreated(cacheObj) {
        super.onCacheCreated(cacheObj);
        
        if (!cacheObj.option.changeEventListeners) 
            cacheObj.option.changeEventListeners = [];
    }
    
    onDataChanged(prevObj, nowObject) {
        super.onDataChanged(prevObj, nowObject);
        
        nowObject.option.changeEventListeners.forEach(listener => {
            if (listener) listener(prevObj, nowObject);
        });
    }
    
    addChangeListener(cacheObj, listener) {
        if (!cacheObj.option.changeEventListeners) 
            cacheObj.option.changeEventListeners = [];
        
        cacheObj.option.changeEventListeners.push(listener);
    }
    
    removeChangeListener(cacheObj, listener) {
        if (!cacheObj.option.changeEventListeners) 
            return;
        
        const removed = cacheObj.option.changeEventListeners.filter(l => l !== listener);
        cacheObj.option.changeEventListeners = removed;
    }
}