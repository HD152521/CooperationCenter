// /static/js/lib/notifier.js
export const Notifier = {
    modal(title, message, okText = "닫기") {
        const wrap = document.createElement("div");
        wrap.className = "fixed inset-0 z-[9999] flex items-center justify-center";
        wrap.style.background = "rgba(0,0,0,.45)";

        const box = document.createElement("div");
        box.style.maxWidth = "520px";
        box.style.width = "92%";
        box.style.background = "#fff";
        box.style.borderRadius = "16px";
        box.style.padding = "20px 24px";
        box.style.boxShadow = "0 10px 30px rgba(0,0,0,.2)";
        box.innerHTML = `
      <div style="display:flex;justify-content:center;align-items:center;margin-bottom:12px;">
        <div style="width:56px;height:56px;border-radius:999px;background:#fee2e2;display:flex;align-items:center;justify-content:center;font-size:26px;color:#ef4444;">!</div>
      </div>
      <h3 style="text-align:center;font-weight:700;font-size:18px;margin:0 0 6px;">${escapeHtml(title)}</h3>
      <p style="text-align:center;color:#4b5563;margin:0 0 16px;white-space:pre-line;">${escapeHtml(message)}</p>
      <div style="display:flex;justify-content:center;gap:8px;">
        <button id="__notifier_ok__" style="padding:8px 16px;background:#ef4444;color:#fff;border:none;border-radius:10px;cursor:pointer;">${escapeHtml(okText)}</button>
      </div>
    `;
        wrap.appendChild(box);
        wrap.addEventListener("click", (e) => { if (e.target === wrap) wrap.remove(); });
        box.querySelector("#__notifier_ok__").onclick = () => wrap.remove();
        document.body.appendChild(wrap);
    },

    toast(message, ms = 2000) {
        const el = document.createElement("div");
        el.textContent = message;
        el.style.position = "fixed";
        el.style.left = "50%";
        el.style.bottom = "22px";
        el.style.transform = "translateX(-50%)";
        el.style.background = "#111827";
        el.style.color = "#fff";
        el.style.padding = "10px 14px";
        el.style.borderRadius = "10px";
        el.style.zIndex = "9999";
        el.style.boxShadow = "0 6px 20px rgba(0,0,0,.25)";
        document.body.appendChild(el);
        setTimeout(() => el.remove(), ms);
    }
};

function escapeHtml(s) {
    if (typeof s !== "string") return "";
    return s.replace(/[&<>"'`=\/]/g, c => ({
        "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;","`":"&#96;","=":"&#61;","/":"&#47;"
    }[c]));
}

// (선택) 모듈 사용이 어려운 레거시 페이지 지원
// window.Notifier = Notifier;