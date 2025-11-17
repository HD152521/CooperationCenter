import commonjs from '@rollup/plugin-commonjs';
import resolve from '@rollup/plugin-node-resolve';
import terser from '@rollup/plugin-terser';

export default {
  // 1. 번들링 시작점
  input: 'src/main/resources/static/js/homepage/main.js',

  // 2. 번들링 결과물 설정
  output: {
    // 출력될 파일 경로
    file: 'src/main/resources/static/js/homepage/build/bundle.js',
    // 번들 형식: iife (Immediately Invoked Function Expression)
    // 브라우저 환경에서 단독으로 실행되는 스크립트에 적합합니다.
    format: 'iife',
    // 소스맵을 생성하여 디버깅을 용이하게 합니다.
    sourcemap: true,
  },

  // 3. 플러그인 설정
  plugins: [
    resolve(),
    commonjs(),
    terser()
  ],

  // 4. 경고 제어
  onwarn(warning, warn) {
    // 'THIS_IS_UNDEFINED' 경고는 일부 라이브러리에서 발생하는 흔한 경고이므로 무시합니다.
    if (warning.code === 'THIS_IS_UNDEFINED') {
      return;
    }
    warn(warning);
  }
};
