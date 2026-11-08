// 차 일기 — 시안 프레임 (싶 디자인 시스템)
// - 캔버스 = 차량 화면의 앱 영역. 가로 1700×1184px / 세로 분할 840×1184px (URL ?layout=portrait)
// - 싶은 Light 고정이라 테마 전환 없음
// - ?shot=1 이면 툴바 없이 원본 크기로 렌더 (캡처용)
(function () {
  const params = new URLSearchParams(location.search);
  const display = document.querySelector('.display');
  const supportsPortrait = document.body.dataset.portrait === 'true';
  const title = document.body.dataset.title || document.title;

  display.dataset.layout = supportsPortrait && params.get('layout') === 'portrait' ? 'portrait' : 'landscape';
  if (params.get('shot')) document.body.classList.add('shot');

  const stage = document.createElement('div');
  stage.className = 'preview-stage';
  const scaler = document.createElement('div');
  scaler.className = 'preview-scaler';
  display.replaceWith(stage);
  stage.appendChild(scaler);
  scaler.appendChild(display);

  const link = (k, v) => { const p = new URLSearchParams(location.search); p.set(k, v); return '?' + p.toString(); };
  const layout = display.dataset.layout;
  const bar = document.createElement('div');
  bar.className = 'preview-bar';
  bar.innerHTML =
    `<a href="index.html">← 전체 화면</a><span class="title">${title}</span>` +
    (supportsPortrait
      ? `<button aria-pressed="${layout === 'landscape'}" onclick="location.search='${link('layout', 'landscape')}'">가로</button>` +
        `<button aria-pressed="${layout === 'portrait'}" onclick="location.search='${link('layout', 'portrait')}'">세로 분할</button>`
      : '');
  document.body.prepend(bar);

  function fit() {
    if (document.body.classList.contains('shot')) { scaler.style.transform = ''; stage.style.height = ''; return; }
    const w = display.offsetWidth, h = display.offsetHeight;
    const s = Math.min(1, (stage.clientWidth - 32) / w);
    scaler.style.transform = `scale(${s})`;
    stage.style.height = h * s + 32 + 'px';
  }
  addEventListener('resize', fit);
  fit();
})();
