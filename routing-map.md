# 프로젝트 URL 라우팅 맵

이 문서는 프로젝트의 URL과 해당 URL이 렌더링하는 HTML 페이지를 정리한 문서입니다.

## 홈페이지 (`/`)

| URL | 대상 HTML 파일 | 컨트롤러 |
| --- | --- | --- |
| `/`, `/home` | `homepage/user/index.html` | `HomeController` |
| `/profile` | `homepage/user/member/profile.html` | `MemberProfileController` |

### 사용자 인증 (`/member`)

| URL | 대상 HTML 파일 | 컨트롤러 |
| --- | --- | --- |
| `/member/signup` | `homepage/user/signup.html` | `MemberController` |
| `/member/login` | `homepage/user/login.html` | `MemberController` |
| `/member/password/forgot` | `homepage/user/forgetPassword.html` | `MemberController` |
| `/member/password/reset` | `homepage/user/resetPassword.html` | `MemberController` |

### 기관 (`/agency`)

| URL | 대상 HTML 파일 | 컨트롤러 |
| --- | --- | --- |
| `/agency/list` | `homepage/user/agency/agency-introduction.html` | `AgengyController` |

### 학교 (`/school`)

| URL | 대상 HTML 파일 | 컨트롤러 |
| --- | --- | --- |
| `/{school}/board/{boardId}` | (조건부 렌더링) | `SchoolController` |
| | - (공지) `homepage/user/school/postTemplate.html` | |
| | - (소개) `homepage/user/school/{school}/{content}` | |
| | - (자료실) `homepage/user/school/school-board.html` | |
| | - (일정) `homepage/user/school/school-schedule.html` | |
| `/{school}/files/{boardId}` | `homepage/user/school/school-board.html` | `SchoolController` |
| `/{school}/board/{boardId}/post/{postId}` | `homepage/user/school/postDetailTemplate.html` | `SchoolController` |

### 설문 (`/survey`)

| URL | 대상 HTML 파일 | 컨트롤러 |
| --- | --- | --- |
| `/survey/make` | `homepage/user/survey/survey-make.html` | `SurveyController` |
| `/survey/list` | (조건부 렌더링) | `SurveyController` |
| | - (관리자/폴더) `homepage/user/survey/survey-folder-list.html` | |
| | - (관리자/목록) `homepage/user/survey/survey-list-admin.html` | |
| | - (사용자/목록) `homepage/user/survey/survey-list-user.html` | |
| `/survey/answer/{surveyId}` | `homepage/user/survey/survey-answer.html` | `SurveyController` |
| `/survey/edit/{surveyId}` | `homepage/user/survey/survey-make.html` | `SurveyController` |
| `/survey/log/list/{surveyId}` | `homepage/user/survey/survey-answer-log.html` | `SurveyController` |
| `/survey/log/detail/{logId}` | `homepage/user/survey/survey-answer-detail.html` | `SurveyController` |

---

## 관리자 페이지 (`/admin`)

| URL | 대상 HTML 파일 / 동작 | 컨트롤러 |
| --- | --- | --- |
| `/admin`, `/admin/home` | `adminpage/user/index.html` | `HomeController` |
| `/admin/login` | `adminpage/user/login.html` | `MemberAdminController` |
| `/admin/logout` | `redirect:/admin/login` | `MemberAdminController` |
| `/admin/user` | `adminpage/user/member/manageUser.html` | `MemberAdminController` |
| `/admin/survey` | `adminpage/user/survey/manageSurvey.html` | `SurveyAdminController` |
| `/admin/school` | `adminpage/user/school/manageSchool.html` | `SchoolAdminController` |
| `/admin/student` | `adminpage/user/student/studentList.html` | `StudentAdminController` |
