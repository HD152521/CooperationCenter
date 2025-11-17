# API Documentation

이 문서는 Cooperation Center 프로젝트의 API를 역할과 기능 중심으로 정리합니다.

## 1. 사용자 / 인증 (User / Authentication)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| 회원가입 | `POST` | `/api/v1/member/signup` | `MemberRestController.java` | `homepage/user/signup.html` |
| 아이디(이메일) 중복 확인 | `GET` | `/api/v1/member/check-id` | `MemberRestController.java` | `homepage/user/signup.html` (동적 URL) |
| 로그인 | `POST` | `/api/v1/member/login` | `MemberRestController.java` | `homepage/user/login.html` |
| 로그아웃 | `POST` | `/api/v1/member/logout` | `MemberRestController.java` | - |
| 토큰 재발급 | `POST` | `/api/v1/member/refresh` | `MemberRestController.java` | - |
| 비밀번호 재설정 이메일 발송 | `POST` | `/api/v1/member/reset/email` | `MemberRestController.java` | `homepage/user/forgetPassword.html` |
| 비밀번호 재설정 | `POST` | `/api/v1/member/reset/password` | `MemberRestController.java` | `homepage/user/resetPassword.html` |
| 회원 정보 수정 | `PATCH` | `/api/v1/profile/member` | `MemberProfileRestController.java` | `homepage/user/member/profile.html` |
| 유학원 정보 수정 | `PATCH` | `/api/v1/profile/agency` | `MemberProfileRestController.java` | `homepage/user/member/profile.html` |
| 사업자등록증 수정 | `PATCH` | `/api/v1/profile/businessCert` | `MemberProfileRestController.java` | `homepage/user/member/profile.html` |
| 유학원 대표사진 수정 | `PATCH` | `/api/v1/profile/agencyPicture` | `MemberProfileRestController.java` | `homepage/user/member/profile.html` |
| **[관리자]** 로그인 페이지 | `GET` | `/admin/login` | `MemberAdminController.java` | - |
| **[관리자]** 로그인 | `POST` | `/api/v1/admin/login` | `MemberAdminRestController.java` | `adminpage/user/login.html` |
| **[관리자]** 로그아웃 | `GET` | `/admin/logout` | `MemberAdminController.java` | - |
| **[관리자]** 로그인 기록 조회 | `GET` | `/api/v1/admin/login/log` | `MemberAdminRestController.java` | `adminpage/user/index.html` |
| **[관리자]** 회원가입 승인 | `POST` | `/api/v1/admin/accept/{memberEmail}` | `MemberAdminRestController.java` | `adminpage/user/index.html` |
| **[관리자]** 회원가입 보류 | `POST` | `/api/v1/admin/pending/{memberEmail}` | `MemberAdminRestController.java` | - |
| **[관리자]** 회원 상세 정보 조회 | `GET` | `/api/v1/admin/detail/{memberEmail}` | `MemberAdminRestController.java` | `adminpage/user/member/manageUser.html` |

## 2. 학생 데이터 (Student Data)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| **[관리자]** 모든 학생 정보 조회 | `GET` | `/api/v1/admin/students` | `StudentAdminRestController.java` | - |
| **[관리자]** 학생 상세 정보 조회 | `GET` | `/api/v1/admin/students/{id}` | `StudentAdminRestController.java` | `adminpage/user/student/studentList.html` |
| **[관리자]** 학생 정보 Excel 다운로드 | `GET` | `/api/v1/admin/students/download` | `StudentAdminRestController.java` | `adminpage/user/student/studentList.html` |

## 3. 설문조사 (Survey)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| 설문조사 목록 조회 | `GET` | `/api/v1/survey/list` | `SurveyRestController.java` | `adminpage/user/student/studentList.html` |
| 설문조사 상세 조회 | `GET` | `/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-answer.html` |
| 설문조사 답변 제출 | `POST` | `/api/v1/survey/answer` | `SurveyRestController.java` | `homepage/user/survey/survey-answer.html` |
| QR 코드 생성 | `GET` | `/api/v1/survey/qr` | `SurveyRestController.java` | `homepage/user/survey/survey-list-admin.html` |
| **[관리자]** 설문조사 생성 | `POST` | `/api/v1/survey/admin/make` | `SurveyRestController.java` | `homepage/user/survey/survey-make.html` |
| **[관리자]** 설문조사 수정 | `PATCH` | `/api/v1/survey/admin/edit` | `SurveyRestController.java` | `homepage/user/survey/survey-make.html` |
| **[관리자]** 설문조사 삭제 | `DELETE` | `/api/v1/survey/admin/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-list-admin.html` |
| **[관리자]** 설문조사 복사 | `POST` | `/api/v1/survey/admin/copy/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-list-admin.html` |
| **[관리자]** 설문조사 템플릿 조회 | `GET` | `/api/v1/survey/admin/template` | `SurveyRestController.java` | `homepage/user/survey/survey-make.html` |
| **[관리자]** 설문조사 답변 로그 조회 | `GET` | `/api/v1/survey/admin/answer/{surveyId}` | `SurveyRestController.java` | - |
| **[관리자]** 답변 로그 CSV 추출 | `POST` | `/api/v1/survey/admin/log/csv` | `SurveyRestController.java` | `homepage/user/survey/survey-answer-log.html` |
| **[관리자]** 답변 로그 전체 CSV 추출 | `POST` | `/api/v1/survey/admin/log/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-answer-log.html` |
| **[관리자]** 학생별 파일 다운로드 | `POST` | `/api/v1/survey/admin/log/file/student/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-answer-log.html` |
| **[관리자]** 문항별 파일 다운로드 | `POST` | `/api/v1/survey/admin/log/file/survey/{surveyId}` | `SurveyRestController.java` | `homepage/user/survey/survey-answer-log.html` |
| **[관리자]** 폴더 관리 | `GET`, `POST`, `PATCH`, `DELETE` | `/api/v1/survey/admin/folders` | `SurveyRestController.java` | `homepage/user/survey/survey-folder-list.html` |

## 4. 학교 / 게시판 (School / Board)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| **[관리자]** 학교 정보 저장 | `POST` | `/api/v1/admin/school/save` | `SchoolAdminRestController.java` | - |
| **[관리자]** 게시판 생성/삭제 | `POST`, `DELETE` | `/api/v1/admin/school/board` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |
| **[관리자]** 게시글 관리 | `POST`, `PATCH`, `DELETE`, `GET` | `/api/v1/admin/school/post` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |
| **[관리자]** 게시글 목록 조회 | `GET` | `/api/v1/admin/school/posts` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |
| **[관리자]** 파일 게시글 관리 | `POST`, `GET`, `PATCH`, `DELETE` | `/api/v1/admin/school/file` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |
| **[관리자]** 학사일정 관리 | `POST`, `GET`, `PATCH`, `DELETE` | `/api/v1/admin/school/schedule` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |
| **[관리자]** 학사일정 목록 조회 | `GET` | `/api/v1/admin/school/schedules` | `SchoolAdminRestController.java` | `adminpage/user/school/manageSchool.html` |

## 5. 유학원 (Agency)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| 유학원 지역 목록 조회 | `GET` | `/api/v1/agency/region` | `AgencyRestController.java` | `homepage/user/member/profile.html` |

## 6. 파일 관리 (File Management)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| 이미지 조회 | `GET` | `/api/v1/file/img/{type}/{fileId}` | `FileRestController.java` | `adminpage/user/school/manageSchool.html` (SunEditor), `homepage/user/school/school-board.html` |
| 파일 다운로드 | `GET` | `/api/v1/file/{type}/{fileId}` | `FileRestController.java` | `homepage/user/school/school-board.html` |
| PDF 조회 | `GET` | `/api/v1/file/pdf/{type}/{fileId}` | `FileRestController.java` | - |
| 파일 업로드 | `POST` | `/api/v1/file/{type}` | `FileRestController.java` | - |
| OSS 파일 업로드 | `POST` | `/api/v1/oss/upload` | `OssController.java` | - |

## 7. 외부 연동 (External Integration)

| 기능 | HTTP Method | API Endpoint | 정의 위치 (Controller) | 프론트엔드 사용처 |
| --- | --- | --- | --- | --- |
| 주소 검색 제안 (Tencent) | `GET` | `/api/v1/tencent/address` | `MemberAddressController.java` | `homepage/user/member/profile.html`, `homepage/user/signup.html` |