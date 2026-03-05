# CooperationCenter

협력센터 운영을 위한 Spring Boot 기반 웹 애플리케이션입니다.  
학교/학생/설문 기능을 중심으로 관리자와 사용자 화면을 제공합니다.

## 주요 기능

- 회원/권한 관리 (일반/관리자)
- 설문 생성/수정/복사/삭제
- 설문 응답 제출 (텍스트/객관식/파일/이미지/계층형 문항)
- 설문 응답 로그 조회 및 상세 확인
- CSV 내보내기, 파일 다운로드
- 설문 폴더 관리
- QR 코드 생성

## 기술 스택

- Backend: Java 17, Spring Boot 3.5, Spring MVC, Spring Security, Spring Data JPA
- View: Thymeleaf
- DB: MySQL
- Query: QueryDSL
- Frontend build: Rollup, TailwindCSS, Babel
- Infra/Etc: JWT, OAuth2 Client, Swagger(Springdoc), Actuator + Prometheus, Testcontainers

## 프로젝트 구조

```text
src/main/java/com/cooperation/project/cooperationcenter
  ├─ domain/
  │   ├─ member, school, student, survey, file ...
  │   └─ 각 도메인별 controller/service/repository/model/dto
  ├─ global/
  │   ├─ config, exception, filter ...
  └─ CooperationCenterApplication.java

src/main/resources
  ├─ templates/          # Thymeleaf 페이지
  ├─ static/             # JS/CSS/이미지 정적 리소스
  ├─ application.yml
  ├─ application-local.yml
  └─ application-prod.yml
```

## 실행 환경

- JDK 17
- Node.js / npm
- MySQL 8+

## 로컬 실행 방법

1. 프론트엔드 패키지 설치

```bash
npm ci
```

2. 애플리케이션 실행 (local 프로필)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Windows:

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

참고:

- `processResources` 단계에서 `npm run build`가 자동 실행됩니다.
- 기본 웹 포트는 `8081`입니다.
- Actuator(Prometheus/Health)는 local 기준 `8082` 포트로 열립니다.

## 환경 변수

기본/운영 설정에서 아래 값을 사용합니다.

- DB: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- JWT: `JWT_SECRET`
- OSS: `OSS_ACCESS_END_POINT`, `OSS_ACCESS_BUCKET_NAME`, `OSS_ACCESS_KEY_ID`, `OSS_ACCESS_KEY_SECRET`
- Mail(Naver): `MAIL_USERNAME`, `MAIL_PASSWORD`
- Mailgun: `MALIGUN_DOMAIN`, `MALIGUN_APIKEY`, `MALIGUN_FROMEMAIL`
- Tencent Map: `TENCENT_URL`, `TENCENT_API_KEY`
- Server Port(선택): `PORT`

## API 문서

- Swagger UI (local): `/api-test`
- OpenAPI JSON: `/v3/api-docs`

## 테스트

```bash
./gradlew test
```

Windows:

```powershell
.\gradlew.bat test
```

## 빌드

```bash
./gradlew clean build
```

Windows:

```powershell
.\gradlew.bat clean build
```
