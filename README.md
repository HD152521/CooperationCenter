# CooperationCenter

![CooperationCenter Logo](docs/assets/cooperationcenter-logo.svg)

협력센터 운영 업무를 위한 통합 웹 애플리케이션입니다.  
관리자/사용자가 설문을 만들고, 응답을 수집하고, 로그/파일/통계를 확인할 수 있도록 구성되어 있습니다.

## 프로젝트 이미지

![CooperationCenter Overview](docs/assets/cooperationcenter-overview.svg)

## 어떤 문제를 해결하나

- 설문 작성, 배포, 응답 수집, 결과 확인이 여러 시스템으로 분산된 문제를 하나의 플랫폼으로 통합
- 파일/이미지 업로드가 포함된 응답 처리와 내보내기(CSV, ZIP) 자동화
- 계층형 문항, 분기 점프 문항 같은 복잡한 설문 로직을 관리자 화면에서 직접 편집

## 핵심 기능

### 1) 설문 제작 (관리자)
- 설문 생성/수정/복사/삭제
- 문항 타입 지원:
  - 단답형, 서술형
  - 객관식(단일/다중), 드롭다운
  - 날짜, 파일, 이미지
  - 계층형 문항(Level 1~3)
- 문항 순서 변경(드래그), 분기 점프(next question) 설정
- 폴더 기반 설문 분류

### 2) 설문 응답 (사용자)
- 설문 상세 로딩 후 동적 렌더링
- 필수 응답/진행률 표시
- 분기 점프에 따라 문항 노출 제어
- 파일/이미지 포함 multipart 제출

### 3) 결과/운영
- 응답 로그 목록/상세 화면
- CSV 다운로드(선택/전체)
- 업로드 파일 다운로드(학생 기준/설문 기준)
- QR 코드 생성

## 어떻게 구현했나 (기능별 구현 방식)

### 설문 제작 화면
- Thymeleaf 템플릿 + 모듈형 Vanilla JS 스크립트로 문항 UI를 동적으로 조립
- `SortableJS`로 문항 순서 이동 처리
- 계층형 문항은 내부 스토어(`hierarchyStore`)를 사용해 Level 1~3 구조 관리
- 편집 모달에서 박스 추가/연결/검증 후 기존 계층 데이터로 반영
  - 최대 3레벨 제한
  - 순환 연결 방지
  - 부모-자식 그룹 기반 시각화

### 응답 화면
- `/api/v1/survey/{surveyId}` 응답(JSON)을 기반으로 문항 렌더링
- 입력값 타입별 검증 후 `/api/v1/survey/answer`로 multipart 전송
- 파일 업로드는 `FormData`에 데이터(JSON 문자열) + 파일 파트 분리 저장
- 점프 문항은 `data-next-question` 기반으로 이후 문항 표시/숨김 처리

### 로그/내보내기
- 응답 로그 목록 + 상세 화면 분리
- CSV/파일 다운로드는 REST API와 스트리밍 응답(`StreamingResponseBody`) 사용

## 기술 스택

- Backend: Java 17, Spring Boot 3.5, Spring MVC, Spring Security, Spring Data JPA
- View: Thymeleaf
- Query: QueryDSL
- DB: MySQL
- Auth: JWT, OAuth2 Client
- Docs: Springdoc OpenAPI(Swagger)
- Observability: Actuator, Prometheus
- Frontend build: Rollup, TailwindCSS, Babel
- Test: JUnit 5, Testcontainers

## 프로젝트 구조

```text
src/main/java/com/cooperation/project/cooperationcenter
  ├─ domain/
  │   ├─ member/
  │   ├─ school/
  │   ├─ student/
  │   ├─ survey/
  │   └─ file/
  ├─ global/
  │   ├─ config/
  │   ├─ exception/
  │   └─ filter/
  └─ CooperationCenterApplication.java

src/main/resources
  ├─ templates/                      # Thymeleaf 페이지
  ├─ static/                         # JS/CSS/이미지
  ├─ application.yml
  ├─ application-local.yml
  └─ application-prod.yml
```

## 실행 환경

- JDK 17
- Node.js / npm
- MySQL 8+

## 로컬 실행

1. 프론트엔드 의존성 설치

```bash
npm ci
```

2. 애플리케이션 실행(local 프로필)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Windows:

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

참고:

- `processResources` 단계에서 `npm run build` 자동 실행
- 기본 웹 포트: `8081`
- Actuator 포트(local): `8082`

## 환경 변수

- DB: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- JWT: `JWT_SECRET`
- OSS: `OSS_ACCESS_END_POINT`, `OSS_ACCESS_BUCKET_NAME`, `OSS_ACCESS_KEY_ID`, `OSS_ACCESS_KEY_SECRET`
- Mail(Naver): `MAIL_USERNAME`, `MAIL_PASSWORD`
- Mailgun: `MALIGUN_DOMAIN`, `MALIGUN_APIKEY`, `MALIGUN_FROMEMAIL`
- Tencent Map: `TENCENT_URL`, `TENCENT_API_KEY`
- Server Port(optional): `PORT`

## API 문서

- Swagger UI(local): `/api-test`
- OpenAPI JSON: `/v3/api-docs`

## 테스트 / 빌드

```bash
./gradlew test
./gradlew clean build
```

Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat clean build
```
