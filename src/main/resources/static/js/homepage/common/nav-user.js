import LeafEvent from "./AsyncListener.js";

document.addEventListener("turbo:load", () => {
    const mobileBtn = document.getElementById("mobile-menu-button");
    const mobileMenu = document.getElementById("mobile-menu");
    const mobileUniBtn = document.getElementById("mobile-university-button");
    const mobileUniMenu = document.getElementById("mobile-university-menu");

    // 모바일 전체 메뉴 토글
    mobileBtn?.addEventListener("click", (e) => {
        e.stopPropagation();
        mobileMenu?.classList.toggle("hidden");
    });

    // 모바일 대학교 하위 메뉴 토글
    mobileUniBtn?.addEventListener("click", (e) => {
        e.stopPropagation();
        mobileUniMenu?.classList.toggle("hidden");
        mobileUniBtn?.querySelector("i").classList.toggle("rotate-180");
    });


    // 로그아웃 기능
    const webLogoutBtn = document.getElementById("web-logout-button");
    LeafEvent.addAsyncListener(webLogoutBtn, "click", logoutListener);

    async function logoutListener() {
        try {
            const response = await fetch("/member/logout", {
                method: "POST",
                credentials: "include"
            });

            if (response.ok) {
                Turbo.visit("/home");
            } else {
                throw new Error("로그아웃 실패");
            }

        } catch (err) {
            console.error("로그아웃 오류:", err);
            alert("로그아웃 오류 발생");
        }
    }
});

// Todo: 세션 연장기능 -> 미들웨어로 빼면 어떨까요?
// async function loadPageContent() {
//     const response = await fetch(window.location.pathname, {
//         credentials: 'include'
//     });
//
//     const contentType = response.headers.get('Content-Type');
//
//     if (contentType && contentType.includes('application/json')) {
//         const result = await response.json();
//
//         if (result.code === 'TOKEN-0000') {
//             const shouldRefresh = confirm("세션이 만료되었습니다. 연장하시겠습니까?");
//             if (shouldRefresh) {
//                 const refreshRes = await fetch('/api/v1/member/refresh', {
//                     method: 'POST',
//                     credentials: 'include'
//                 });
//                 const refreshJson = await refreshRes.json();
//                 if (refreshJson.isSuccess) {
//                     location.reload(); // 새로고침 (토큰 갱신 후)
//                 } else {
//                     alert("다시 로그인해주세요.");
//                     window.location.href = "/member/login";
//                 }
//             } else {
//                 window.location.href = "/member/login";
//             }
//             return;
//         }
//     }
//
//     // JSON 아니면 일반 HTML로 간주 → 그대로 렌더링
//     const html = await response.text();
//     document.open();
//     document.write(html);
//     document.close();
// }