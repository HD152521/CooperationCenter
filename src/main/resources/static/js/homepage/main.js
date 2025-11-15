import * as Turbo from '@hotwired/turbo';

// 전역 스코프에 의존할 수 있는 레거시 스크립트를 먼저 임포트합니다.
// 이 파일들은 실행 시 특정 동작을 수행하거나 전역 변수를 설정할 수 있습니다.
import Url from './url.js';
import './nav-user.js';

// ES 모듈 스크립트들을 임포트합니다.
// 이 모듈들은 번들에 포함되며, 각 모듈의 최상위 코드가 실행됩니다.
import './CacheManager.js';
import LeafQuery from './cache-fn.js';
import LeafEvent from "./AsyncListener.js";

// Turbo를 전역에서 사용할 수 있도록 window 객체에 할당 (선택 사항)
// 다른 스크립트에서 Turbo.visit() 등을 사용해야 할 경우 필요합니다.
window.Turbo = Turbo;
window.LeafQuery = LeafQuery;
window.LeafEvent = LeafEvent;

// 모든 전역변수 파싱
for (const key in Url) {
    window[key] = Url[key];
}
