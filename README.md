# 음악 감상 기록 Music Journal

웹서비스설계 JWT 게시판 과제입니다. 좋아하는 곡과 그 순간의 감상을 기록하고 공유하는 게시판을 React와 Spring Boot로 구현했습니다.

## 작성자와 서비스 소개

- 학번 / 이름: 202219377 / 김지후
- 서비스 목적과 대상 사용자: 음악을 듣고 느낀 감상을 기록하거나 다른 사람의 감상평을 읽고 싶은 사용자를 위한 서비스입니다. 게시글 제목에는 곡명과 가수명을, 본문에는 감상을 작성할 수 있습니다. 목록과 상세는 로그인 없이 조회할 수 있습니다. 로그인한 사용자는 글을 작성하고 본인의 글만 수정하거나 삭제할 수 있습니다.
- 선택한 기술: React + Spring Boot
- 주요 기능: 공개 목록과 상세 조회, JWT 로그인과 내 정보 조회, 본인 글 작성과 수정 및 삭제, 토큰 없음과 변조 및 만료 처리, SQLite 데이터 유지

## 보고서와 테스트 증거

- [구현 보고서 PDF](docs/report.pdf)
- [테스트 증거](docs/screenshot/)

보고서 3장에 핵심 코드 설명, 4장에 T01부터 T17까지의 테스트 결과, 5장에 AI 활용 기록을 작성했습니다. 증거 파일은 테스트 ID로 구분했습니다.

## 프로젝트 구조

```text
.
├── README.md
├── .gitignore
├── frontend-react/                       # React 프론트
│   ├── README.md
│   ├── package.json
│   ├── package-lock.json
│   ├── index.html
│   ├── vite.config.js                    # 개발 서버와 preview의 API 프록시
│   └── src/
│       ├── api.js                        # 로그인, 토큰 전송과 오류 처리
│       ├── main.jsx                      # 화면, 사용자 상태와 404 처리
│       └── style.css
├── backend-spring/                       # Spring Boot API 서버
│   ├── README.md
│   ├── .mvn/wrapper/
│   │   └── maven-wrapper.properties
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── env.example.txt                   # 실제 키가 아닌 설정 예시
│   └── src/main/
│       ├── java/edu/classroom/board/
│       │   ├── BoardApplication.java     # 서버 시작
│       │   ├── BoardController.java      # 라우트와 입력 검사
│       │   ├── AuthService.java          # JWT 발급과 검증, 작성자 권한
│       │   ├── Users.java                # 테스트 계정과 비밀번호 검증
│       │   ├── PostStore.java            # SQLite 게시글 쿼리
│       │   ├── DatabaseConfig.java       # DB 경로와 연결 설정
│       │   ├── ApiError.java             # API 오류 정의
│       │   └── Errors.java               # 공통 JSON 오류 응답
│       └── resources/
│           ├── application.properties
│           └── schema.sql
├── tools/
│   └── check_api.py                      # 제공된 API 검사 도구
└── docs/
    ├── report.pdf                        # 구현 보고서
    └── screenshot/                       # T01부터 T17까지의 테스트 증거
```

## 실행 환경

| 항목 | 사용 버전 |
|---|---|
| 운영체제 | macOS 26.4.1 |
| 프론트 런타임 Node.js | 24.21.0, npm 11.19.0 |
| 백엔드 런타임 | OpenJDK 21.0.12.1, Java 컴파일 대상 17 |
| 프레임워크 | Spring Boot 3.5.16, JJWT 0.13.0, React 19.3.0, Vite 8.3.4 |
| 데이터 저장 | SQLite, sqlite-jdbc 3.51.3.0 |

## 설치와 실행

JDK 17 이상과 Node.js 20.19 이상이 필요합니다. Maven은 제공된 Wrapper를 사용하며, 최초 의존성 설치에는 인터넷 연결이 필요합니다. 터미널 A에서 백엔드를, 터미널 B에서 프론트를 실행합니다. GitHub는 코드와 문서 제출용이며 외부 배포는 하지 않았습니다.

### 1 저장소 복제

```sh
# 저장소를 복제하고 루트 폴더로 이동합니다.
git clone https://github.com/jikim08/wsd-jwt-board.git
cd wsd-jwt-board
```

### 2 백엔드 준비와 실행

Spring은 `.env` 파일을 자동으로 읽지 않으므로 서버를 실행할 터미널에서 환경변수를 설정합니다. JWT_SECRET이 없거나 64자리 16진수 형식이 아니면 시작되지 않으며, TOKEN_TTL은 양수여야 합니다.

macOS / Linux:

```sh
# 터미널 A: 저장소 루트에서 백엔드 폴더로 이동합니다.
cd backend-spring

# 새로운 서명 키를 생성하고 JWT 설정을 지정합니다.
export JWT_SECRET=$(node -e "console.log(require('node:crypto').randomBytes(32).toString('hex'))")
export JWT_ISSUER=webservice-class
export JWT_AUDIENCE=board-api
export TOKEN_TTL=90

# Maven Wrapper로 패키징한 뒤 서버를 실행합니다.
chmod +x mvnw
./mvnw package
java -jar target/jwt-board-1.0.0.jar
```

Windows PowerShell:

```powershell
# 터미널 A: 저장소 루트에서 백엔드 폴더로 이동합니다.
cd backend-spring

# 새로운 서명 키를 생성하고 JWT 설정을 지정합니다.
$env:JWT_SECRET = node -e "console.log(require('node:crypto').randomBytes(32).toString('hex'))"
$env:JWT_ISSUER = 'webservice-class'
$env:JWT_AUDIENCE = 'board-api'
$env:TOKEN_TTL = '90'

# Maven Wrapper로 패키징한 뒤 서버를 실행합니다.
.\mvnw.cmd package
java -jar target/jwt-board-1.0.0.jar
```

- 환경설정 예시 파일: `backend-spring/env.example.txt`. 설정 연결은 `backend-spring/src/main/resources/application.properties`에 있습니다.
- JWT_SECRET 설정 방법: 위 명령으로 32바이트 난수를 64자리 16진수 문자열로 생성하여 환경변수로 전달합니다. 서버는 이를 32바이트로 변환하여 HS256 키로 사용합니다. 실제 키는 저장소와 보고서에 기록하지 않습니다.
- TOKEN_TTL: 기본값은 90초입니다. 만료 테스트는 백엔드를 종료하고 같은 터미널에서 `export TOKEN_TTL=10`으로 변경한 뒤 다시 실행합니다. PowerShell은 `$env:TOKEN_TTL = '10'`을 사용합니다. 테스트 후 90으로 되돌려 재시작합니다.
- 백엔드 주소: [http://127.0.0.1:8000](http://127.0.0.1:8000)
- 정상 실행 확인 방법: [http://127.0.0.1:8000/api/health](http://127.0.0.1:8000/api/health)에서 HTTP 200과 `{"status":"ok","stack":"spring"}` 응답을 확인합니다. JSON 속성의 순서는 다를 수 있습니다.

그 밖의 설정은 JWT_ISSUER의 기본값 `webservice-class`, JWT_AUDIENCE의 기본값 `board-api`, PORT의 기본값 `8000`, DB_FILE의 기본값 `data/board.sqlite`입니다.

개발 중에는 같은 환경변수가 설정된 상태에서 `./mvnw spring-boot:run`을 사용할 수 있습니다. Windows는 `.\mvnw.cmd spring-boot:run`을 사용합니다. 다른 백엔드가 실행 중이면 먼저 종료합니다.

### 3 프론트 빌드와 로컬 실행

백엔드를 켜 두고 새 터미널 B를 엽니다. 복제한 `wsd-jwt-board` 루트 폴더로 이동한 뒤 실행합니다.

```sh
# 터미널 B: 저장소 루트에서 React 폴더로 이동합니다.
cd frontend-react

# lock 파일 기준으로 설치하고 빌드합니다.
npm ci
npm run build

# 빌드 결과를 4173번 포트에서 실행합니다.
npm run preview
```

- 최종 확인 주소: [http://127.0.0.1:4173](http://127.0.0.1:4173)
- 개발 중 실행: `frontend-react`에서 `npm run dev` 실행 후 [http://127.0.0.1:5173](http://127.0.0.1:5173)에 접속합니다.
- API 연결 방식과 포트 변경 방법: 브라우저의 `/api` 요청을 Vite의 preview 서버와 개발 서버가 `http://127.0.0.1:8000`으로 전달합니다. 백엔드 포트를 바꾸면 `frontend-react/vite.config.js`의 `server.proxy`와 `preview.proxy` 목적지도 함께 수정합니다.
- 종료: 프론트와 백엔드를 실행한 각 터미널에서 `Ctrl+C`를 누릅니다.
- 코드 변경 후 재실행: 프론트는 preview를 종료하고 `npm run build` 후 `npm run preview`를 실행합니다. 백엔드는 종료 후 `./mvnw package`로 다시 패키징하고 `java -jar target/jwt-board-1.0.0.jar`로 실행합니다. PowerShell의 패키징 명령은 `.\mvnw.cmd package`입니다. 환경변수 변경도 서버 재시작 후 적용됩니다.

### 4 테스트 계정과 DB

| 사용자 | 이메일 | 실습용 비밀번호 |
|---|---|---|
| Alice | alice@class.test | Classroom!2026 |
| Bob | bob@class.test | Classroom!2026 |

- SQLite 파일 위치와 자동 생성 방식: 백엔드 실행 폴더의 `data/board.sqlite`를 사용합니다. 안내대로 실행하면 저장소 루트 기준 `backend-spring/data/board.sqlite`에 생성됩니다. 서버 시작 시 필요한 폴더와 DB 파일, posts 테이블이 없으면 자동 생성합니다. 테스트 계정은 서버 시작 시 메모리에 준비됩니다.
- DB 초기화 방법: 백엔드를 종료한 뒤 `backend-spring/data/board.sqlite`를 삭제하고 다시 실행합니다. 기존 게시글이 모두 삭제되므로 초기화가 필요한 경우에만 수행합니다.
- 서버 재시작 후 데이터 유지 확인 결과: 글 작성 후 백엔드를 실제로 종료하고 재실행하여 동일한 글 ID, 제목, 본문과 작성자가 유지되는 것을 확인했습니다. 보고서 4장과 증거 폴더의 T15 캡처에 기록했습니다.

위 계정은 과제 전용 테스트 계정입니다. 실제 서비스 계정 정보나 서버 비밀 키는 기록하지 않습니다. 같은 터미널에서 서버만 재시작하면 기존 JWT_SECRET을 유지할 수 있습니다. 키 생성 명령을 다시 실행하면 기존 토큰은 서명 검증에 실패합니다.

## 구현 완료 범위

| 기능 | 완료 여부 | 주요 파일 또는 함수 |
|---|---|---|
| 공개 목록과 상세 | 완료 | BoardController.list(), get(), PostStore, 제공 코드 유지 |
| 로그인과 JWT 발급 | 완료 | AuthService 생성자, issueToken(), api.js의 login() |
| JWT 검증과 내 정보 조회 | 완료 | extractToken(), verifyToken(), currentUserId(), BoardController.me() |
| 글 작성과 작성자 저장 | 완료 | BoardController.create()가 currentUserId()의 결과를 작성자로 사용 |
| 본인 글 수정·삭제 | 완료 | AuthService.assertOwner(), api.js의 canEdit() |
| 토큰 만료와 로그아웃 | 완료 | api.js의 request(), clearSession(), main.jsx의 사용자 상태 갱신 |
| 입력 오류와 권한 오류 처리 | 완료 | api.js의 request(), Errors.java, main.jsx의 404 상태 초기화와 목록 갱신 |

알려진 문제와 미완성 기능: 필수 구현 범위에서 확인된 미완성 기능은 없습니다. 토큰은 메모리에만 저장하므로 새로고침하면 로그인이 해제됩니다. 로그아웃은 현재 탭의 토큰을 지우며, 별도 폐기 목록이 없어 이미 복사한 유효 토큰은 만료 시각까지 사용할 수 있습니다.

## 테스트 방법과 결과 요약

- 실행한 검사 명령과 실행 위치: `backend-spring`에서 `./mvnw spring-boot:run`으로 백엔드를 실행하고, `frontend-react`에서 `npm run build`와 `npm run preview`로 빌드 화면을 실행했습니다. 4173번 화면의 Network와 Console, API 클라이언트를 이용하여 T01부터 T17까지 수동 검증했습니다. 자동 검사 도구는 별도로 실행하지 않았습니다.
- Alice/Bob 권한 구분 확인 결과: Bob의 정상 토큰으로 Alice 글을 수정하거나 삭제하면 403 FORBIDDEN이 반환되고 재조회 결과 원문이 유지되었습니다. Bob 본인 글 작성은 201, 수정은 200, 삭제는 204로 성공했습니다.
- HTTP 상태와 오류 code 확인: 공개 조회와 로그인 및 수정 200, 작성 201, 삭제 204를 확인했습니다. 입력 오류 400 BAD_INPUT, 계정 불일치 401 INVALID_CREDENTIALS, 타인 글 변경 403 FORBIDDEN, 없는 글 404 NOT_FOUND를 확인했습니다. 상세 결과는 [보고서 4장](docs/report.pdf)에 기록했습니다.
- 응답 헤더와 본문 확인: 등록 201의 Location, 보호 API 401의 `WWW-Authenticate: Bearer`, 삭제 204의 빈 응답 본문을 확인했습니다.
- 토큰 없음·변조·만료 확인 결과: 무토큰 요청은 401 TOKEN_REQUIRED, 변조 토큰은 401 INVALID_TOKEN, 만료 토큰은 401 TOKEN_EXPIRED로 거부되었습니다. 만료 검증에 TOKEN_TTL 10초를 사용했으며, 401에서 로그인 해제와 403에서 로그인 유지도 확인했습니다.
- 전체 T01~T17 결과와 증거: 모든 시나리오의 판정은 PASS입니다. [보고서 4장](docs/report.pdf)과 [증거 폴더](docs/screenshot/)에 실제 응답과 화면 결과를 기록했습니다. T15는 실제 서버 재시작, T16은 브라우저 새로고침으로 확인했습니다.

제공된 `tools/check_api.py`는 저장소에 포함되어 있지만, 위 결과는 자동 검사 출력이 아닌 직접 수행한 수동 검증 기록입니다.

## 핵심 구현 설명

1. 로그인과 발급: 제공된 BoardController.login()이 입력을 검사하고 Users.verify()로 계정을 확인합니다. AuthService.issueToken()은 검증된 사용자 ID 문자열을 sub에 저장하고 iss, aud, exp를 설정하여 HS256으로 서명합니다. 성공하면 HTTP 200과 accessToken, expiresIn, user를 반환합니다.
2. 토큰 보관과 전송: api.js의 session에 토큰과 사용자 정보를 저장하며 localStorage와 sessionStorage는 사용하지 않습니다. authorizationHeaders()가 토큰이 있을 때만 Bearer 헤더를 구성하고 request()가 이를 첨부합니다. 401에서는 clearSession()으로 세션을 비우고 main.jsx의 run()이 화면 사용자 상태를 갱신합니다. 403에서는 로그인 상태를 유지합니다.
3. 서버 검증: currentUserId()가 extractToken()과 verifyToken()을 호출합니다. Bearer 형식, HS256 알고리즘과 서명, iss와 aud, exp의 존재와 만료를 검사합니다. sub가 양의 정수 문자열이고 long 범위에 들어가며 실제 계정이 존재하는지 확인한 뒤 ID를 반환합니다.
4. 작성자 권한 검사: assertOwner()가 인증된 사용자 ID와 DB의 authorId를 비교하여 다르면 403 FORBIDDEN을 반환합니다. 정상 형식의 보호 요청은 인증, 글 존재 확인, 작성자 권한 검사, 입력 검증, DB 변경 순서로 처리합니다. 프론트의 canEdit()은 버튼 표시를 제어하며 실제 권한 판단은 서버가 수행합니다.

상세 코드와 입력, 처리, 출력 및 실패 조건은 [보고서 3장](docs/report.pdf)에 작성했습니다.

## AI 활용 요약과 참고 자료

- AI 사용 여부: 사용
- 도구와 사용 목적: GPT 6.1 Sol을 활용하여 구현 계획 수립, JWT와 React 개념 학습, 코드 제안, 오류 분석, 보고서와 README 작성 지원을 받았습니다.
- 반영 위치와 직접 수정·검증한 내용: 과제 명세와 체크리스트에 맞는 코드를 단계별로 제안하고 목적과 함수 역할을 설명하며 주석을 포함하도록 요구했습니다. AuthService.java에 JWT 발급과 검증 및 작성자 권한 검사를 반영했습니다. api.js와 main.jsx에는 메모리 세션과 토큰 전송, 로그인 상태 동기화 및 오류 처리를 반영하고, 404에서 상세 화면과 수정 상태 초기화 및 목록 갱신을 추가했습니다. T01부터 T17까지 수동 테스트로 확인했습니다. 보고서와 README는 AI가 작성한 초안을 실제 구현과 테스트 결과에 맞게 검토하고 수정하여 반영했습니다.
- 상세 활용 기록: [보고서 5장](docs/report.pdf) 참조.
- 참고한 수업 자료·외부 코드·공식 문서:
  - 웹서비스설계 5주차와 6주차 강의자료: JWT 구조, 발급과 검증, 인증과 권한 검사 개념.
  - 과제 원본의 `02_API명세.md`: JWT 설정, HTTP 상태와 JSON code, 응답 헤더 기준.
  - 과제 원본의 `03_구현체크리스트.md`: 구현 순서와 검증 항목.
  - 과제 원본의 `05_테스트시나리오.md`: T01부터 T17까지의 절차와 기대 결과.


## 제출 확인

- [x] README의 안내 문구를 실제 내용으로 교체했다.
- [ ] 새 폴더에 clone하여 README만으로 설치·환경설정·빌드·로컬 실행했다.
- [x] 4173번 빌드 화면에서 API 연결과 전체 필수 기능을 확인했다.
- [ ] GitHub에서 보고서와 증거 링크가 열린다.
- [ ] 교수자가 저장소를 열람·복제할 수 있다.
- [ ] 비밀 키·실제 토큰·DB·node_modules·.venv·target을 커밋하지 않았다.
- [ ] 최종 push 후 저장소 URL과 제출 기준 커밋 SHA를 LMS에 제출했다.
