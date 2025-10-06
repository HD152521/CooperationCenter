document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById("university-button");
    const menu = document.getElementById("university-menu");

    let isVisible = false;

    button.addEventListener("click", function (e) {
        e.stopPropagation(); // 다른 요소 클릭 시 닫히지 않도록 방지
        isVisible = !isVisible;
        menu.classList.toggle("hidden", !isVisible);
    });

    // 외부 클릭 시 닫기
    document.addEventListener("click", function () {
        if (isVisible) {
            isVisible = false;
            menu.classList.add("hidden");
        }
    });

    // 메뉴 클릭 시엔 닫히지 않도록
    menu.addEventListener("click", function (e) {
        e.stopPropagation();
    });
});