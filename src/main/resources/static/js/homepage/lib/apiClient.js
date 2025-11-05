// /static/js/lib/apiClient.js
import { Notifier } from "./notifier.js";

const DEFAULTS = {
    baseURL: "",                  // 필요 시 "/api/v1"
    csrfHeader: "X-CSRF-TOKEN",   // 사용 중이면 이름 맞춰 넣기
    csrfTokenSelector: 'meta[name="_csrf"]',
    // 서버 ErrorCode 매핑(원하는 문구로 쉽게 덮어쓰기 가능)
    errorMap: {
        "FILE-0000": "허용되지 않는 확장자입니다.",
        "FILE-0001": "파일을 읽는 중 오류가 발생했습니다.",
        "FILE-0002": "해당 파일을 찾을 수 없습니다.",
        "FILE-0003": "파일 업로드 기록을 가져올 수 없습니다.",
        "FILE-0004": "파일 사이즈가 너무 큽니다.",
        "COMMON400": "잘못된 요청입니다.",
        "COMMON401": "인증이 필요합니다.",
        "COMMON403": "접근이 금지되었습니다.",
        "COMMON500": "서버 에러가 발생했습니다."
    }
};

export const api = {
    config: { ...DEFAULTS },

    setBaseURL(url) { this.config.baseURL = url; },
    setErrorMap(map) { this.config.errorMap = { ...this.config.errorMap, ...map }; },
    setCsrf(headerName, selector = 'meta[name="_csrf"]') {
        this.config.csrfHeader = headerName; this.config.csrfTokenSelector = selector;
    },

    // ---------- public methods ----------
    get(url, opts = {})          { return this._request("GET", url, null, opts); },
    delete(url, opts = {})       { return this._request("DELETE", url, null, opts); },
    postJson(url, json, opts={}) { return this._request("POST", url, JSON.stringify(json), { ...opts, headers: { "Content-Type": "application/json", ...(opts.headers||{}) } }); },
    putJson(url, json, opts={})  { return this._request("PUT", url,  JSON.stringify(json), { ...opts, headers: { "Content-Type": "application/json", ...(opts.headers||{}) } }); },
    postMultipart(url, formData, opts={}) { return this._request("POST", url, formData, opts); },
    patchMultipart(url, formData, opts = {}) {return this._request("PATCH", url, formData, opts);},
    patchJson(url, json, opts = {}) { return this._request("PATCH",url,JSON.stringify(json),{ ...opts, headers: { "Content-Type": "application/json", ...(opts.headers || {}) } }); },

    // ---------- core ----------
    async _request(method, url, body, opts) {
        const full = this.config.baseURL ? this.config.baseURL + url : url;

        const headers = new Headers(opts?.headers || {});
        // CSRF 자동 주입(있을 때만)
        const tokenEl = document.querySelector(this.config.csrfTokenSelector);
        if (tokenEl && !headers.has(this.config.csrfHeader)) {
            headers.set(this.config.csrfHeader, tokenEl.getAttribute("content"));
        }

        let res;
        try {
            res = await fetch(full, {
                method,
                headers,
                body,
                credentials: opts?.credentials ?? "same-origin",
                signal: opts?.signal
            });
        } catch (e) {
            console.log("res 중에 오류");
            Notifier.modal("네트워크 오류", "서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
            throw e;
        }

        console.debug("[api] <-", res.status, res.statusText, "for", method, url);

        if (!res.ok) {
            console.log("!res.ok 진입");
            const err = await parseErrorResponse(res);
            handleError(err, res.status, this.config.errorMap);
            const serverMsg = typeof err.raw === "string" ? err.raw : (err.message || "");
            const e = new Error(serverMsg || `HTTP ${res.status}`);
            e.code   = err.code || null;
            e.status = res.status;
            e.payload = err.raw;
            throw e;
        }

        // content-type 따라 자동 파싱
        const ct = res.headers.get("Content-Type") || "";
        if (ct.includes("application/json")){
            const j = await res.json();
            if (j && typeof j === "object" && "isSuccess" in j) {
                if (j.isSuccess === false) {
                    const err = {
                        code: j.code ?? null,
                        message: j.message ?? null,
                        httpStatus: j.httpStatus ?? 400, // 표시용 상태(서버가 200으로 줘도 OK)
                        raw: j
                    };
                    console.error("[API ERROR LOGICAL]", err.httpStatus, err.code, err.message, err.raw);
                    handleError(err, err.httpStatus, this.config.errorMap);

                    const e = new Error(err.message || "Logical failure");
                    e.code = err.code; e.status = err.httpStatus; e.payload = err.raw;
                    throw e;
                }
                return ("result" in j ? j.result : j);
            }
            return res.json();
        }
        if (ct.startsWith("text/"))          return res.text();
        return res; // 파일/바이너리 응답 등
    }
};

// ---------- helpers ----------
async function parseErrorResponse(res) {
    try {
           const r2 = res.clone();
           const ct = r2.headers.get("Content-Type") || "";
           if (ct.includes("application/json")) {
                 const j = await r2.json();

            // 다양한 래핑 가능성 방어: {reason:{...}}, {error:{...}}, 최상위 ...
            const pick = (obj) => {
                if (!obj || typeof obj !== "object") return {};
                if ("code" in obj || "message" in obj || "httpStatus" in obj || "isSuccess" in obj) return obj;
                // 흔한 래핑 키
                if (obj.reason) return obj.reason;
                if (obj.error)  return obj.error;
                if (obj.data && (obj.data.reason || obj.data.error)) return obj.data.reason || obj.data.error;
                return obj;
            };

            const p = pick(j);
            return {
                code: p.code ?? null,
                message: p.message ?? null,
                httpStatus: p.httpStatus ?? res.status,
                isSuccess: typeof p.isSuccess === "boolean" ? p.isSuccess : false,
                raw: j
            };
        }
        const t = await r2.text();
        return { code: null, message: t || null, httpStatus: res.status, isSuccess: false, raw: t };
    } catch {
        return { code: null, message: null, httpStatus: res.status, isSuccess: false, raw: null };
    }
}

function handleError(err, status, errorMap) {
    // HTTP 상태 우선 처리
    if (status === 413) return Notifier.modal("업로드 실패", "파일이 너무 큽니다. 용량을 줄여 다시 시도해주세요. (413)");
    if (status === 415) return Notifier.modal("요청 형식 오류", "지원하지 않는 콘텐츠 형식입니다. (415)");
    if (status === 403) return Notifier.modal("권한 없음", "요청이 거부되었습니다. 로그인/권한을 확인해주세요. (403)");
    if (status === 401) return Notifier.modal("인증 필요", "로그인 세션이 만료되었을 수 있습니다. 다시 로그인해주세요. (401)");

    // 서버 커스텀 코드/메시지
    if (err.code && errorMap[err.code]) {
        return Notifier.modal("요청 실패", errorMap[err.code]);
    }
    if (err.message) {
        return Notifier.modal("요청 실패", err.message);
    }
    Notifier.modal("요청 실패", "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
}

// (선택) 레거시 페이지 지원
// window.api = api;
