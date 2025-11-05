document.addEventListener("DOMContentLoaded", () => {
    const mobileBtn = document.getElementById("mobile-menu-button");
    const mobileMenu = document.getElementById("mobile-menu");
    const mobileUniBtn = document.getElementById("mobile-university-button");
    const mobileUniMenu = document.getElementById("mobile-university-menu");
    const uniBtn = document.getElementById("university-button");
    const uniMenu = document.getElementById("university-menu");

    // 모바일 전체 메뉴 토글
    if (mobileBtn && mobileMenu) {
        mobileBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            mobileMenu.classList.toggle("hidden");
            console.log(mobileMenu);
        });
    }

    // 모바일 대학교 하위 메뉴 토글
    if (mobileUniBtn && mobileUniMenu) {
        mobileUniBtn.addEventListener("click", (e) => {
            console.log("univ menu btn click");
            e.stopPropagation();
            mobileUniMenu.classList.toggle("hidden");
            mobileUniBtn.querySelector("i").classList.toggle("rotate-180");
        });
    }

    // 데스크탑 대학교 메뉴 토글
    if (uniBtn && uniMenu) {
        uniBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            uniMenu.classList.toggle("hidden");
        });

        document.addEventListener("click", (e) => {
            if (!uniBtn.contains(e.target) && !uniMenu.contains(e.target)) {
                uniMenu.classList.add("hidden");
            }
        });
    }
});