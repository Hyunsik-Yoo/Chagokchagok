// 차 일기 — 스탬프 SVG 스프라이트 (싶 디자인 시스템)
// 싶 규칙 "이모지는 장식이 아니라 데이터다": 일기 이벤트 하나 = 이모지 하나.
// 모양은 싶 배지 메달(BadgeMedalView): 바탕 원 + 링, 가운데 이모지. 링은 기본 gray2-5, 얻은 배지는 --medal-ring: tennis-green.
// 사용: <svg class="stamp"><use href="#stamp-rain"/></svg>
// Compose: Box(CircleShape, background = gray2, border = 6.dp gray2_5) { Text(emoji) }
(function () {
  const STAMPS = {
    rain: '☔', night: '🌙', wind: '🍃', highway: '🛣️', flag: '🚩', star: '⭐',
    cold: '❄️', sun: '☀️', fuel: '⛽', car: '🚗', earth: '🌏', letter: '💌',
  };
  const symbols = Object.entries(STAMPS).map(([id, emoji]) =>
    `<symbol id="stamp-${id}" viewBox="0 0 96 96">` +
      `<circle cx="48" cy="48" r="45" fill="var(--medal-bg, var(--gray2))" stroke="var(--medal-ring, var(--gray2-5))" stroke-width="6"/>` +
      `<text x="48" y="50" text-anchor="middle" dominant-baseline="central" font-size="46">${emoji}</text>` +
    `</symbol>`);

  const sprite = `<svg xmlns="http://www.w3.org/2000/svg" style="position:absolute;width:0;height:0" aria-hidden="true">${symbols.join('')}</svg>`;
  document.body.insertAdjacentHTML('afterbegin', sprite);
})();
