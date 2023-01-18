# HTTP 웹 서버 직접 구현하기
## 목표
- 다음 요구사항의 질문/답변 게시판을 직접 구현한 공통 라이브러리와 프레임워크를 사용해 구현하는 것을 이 책의 실습 목표로 한다.

## 서비스 요구사항
- 질문/답변 게시판의 요구사항을 사용자의 흐름 순으로 정리
  - 질문/답변 게시판에 처음 접근하면 질문 목록을 볼 수 있다
  - 질문 목록 화면에서 회원가입, 로그인, 로그아웃, 개인정보 수정이 가능하며, 질문하기 화면으로 이동할 수 있다
  - 회원가입 버튼을 클릭하면 회원가입 화면이 나온다
  - 회원가입 화면에서 회원가입을 할 수 있다
  - 로그인 버튼을 누르면 로그인 화면이 나온다
  - 회원가입한 사용자는 로그인이 가능하다
  - 질문하기 버튼을 클릭하면 질문하기 화면이 나온다
  - 질문하기 화면에서 사용자는 질문을 할 수 있다
  - 질문 목록 화면에서 각 질문 제목을 클리하면 각 질문의 상세보기 화면으로 이동한다
  - 상세보기 화면에서는 답변을 추가할 수 있고, 질문과 답변의 수정/삭제가 가능하다

## 로컬 개발 환경 구축
- Java 8 버전
- IntelliJ
- Maven
- GitHub 소스코드 clone
- webserver.WebServer의 main 메소드 실행 후 http://localhost:8080 접속하여 "Hello World" 메시지 출력 확인

### Maven
- 빌드 도구
- 설정 파일을 XML로 작성

## 원격 서버에 배포
- 이번 장에서는 HTTP 웹 서버를 원격 서버에 애자일하게 배포하는 경험을 목표로 한다
- 원격 서버에 직접 배포를 반복함으로써 터미널 환경에서 작업하는 것에 익숙해지도록 한다

### 애자일 프로세스
- 현 시점에 가장 가치가 있는, 동작하는 소프트웨어를 만드는 것을 원칙으로 함
- 완벽하기보다는 현재 상태에서 사용가능한 수준으로 빠르게 완성하는 것을 목표로 함
- 반복주기 완료에 따른 성취감과 요구사항에 대한 빠른 피드백이 장점

### 리눅스 명령어 & vi 에디터
- pwd, cd, ls, chmod, cp, rm, mv, ln, ps, kill

### 원격 서버 준비 -> AWS EC2
- 우분투 리눅스 운영체제를 기반한 실습
- 우분투 운영체제를 사용할 수 있는 서버 선정
- AWS EC2 인스턴스를 우분투로 생성

1. AWS 회원가입
   - 기존 계정 프리티어 만료로 신규 생성
2. EC2 인스턴스 프로비저닝
   - AMI(Amazon Machine Image)
     - Ubuntu Server 22.0.4 LTS (HVM), SSD Volume Type
   - 인스턴스 유형
     - t2.micro
   - EIP 할당
     - 인스턴스 중단 후 다시 시작할 때 IP가 변경되므로 Elastic IP 할당
3. EC2 서버에 접속하기
   - Windows 이므로 별도의 클라이언트 설치 (putty)
   - [실행 파일 다운로드](https://www.putty.org/)
   - putty.exe, puttygen.exe 모두 다운로드 후 puttygen.exe 실행
     - putty는 pem 키로 사용 불가하여 pem 키를 ppk 파일로 변환해주어야 하는데 puttygen이 이 과정을 담당
   - putty.exe를 실행하여 ppk을 불러와 권한 설정하고 SSH 접속
   - Ubuntu AMI의 경우 사용자 이름은 ubuntu 디폴트

## 원격 서버 배포 요구사항
- 로컬 개발 환경에 설치한 HTTP 웹 서버를 물리적으로 떨어져 있는 원격 서버에 배포해 정상적으로 동작하는지 테스트
- HTTP 웹 서버 배포 작업은 root 계정이 아닌 배포를 담당할 새로운 계정을 만들어 진행
  - AWS EC2의 경우 이미 계정이 추가되어 있기 때문에 계정 추가 및 sudo 권한 할당 과정 생략

### 원격 서버 (AWS EC2)에 배포하기 


### 크폼 개발자 도구

## HTTP 웹 서버에 대한 요구사항
### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* WebServer class에서 Socket
### 요구사항 2 - get 방식으로 회원가입
*
### 요구사항 3 - post 방식으로 회원가입
*
### 요구사항 4 - redirect 방식으로 이동
*
### 요구사항 5 - cookie
*
### 요구사항 6 - stylesheet 적용
*
### heroku 서버에 배포 후
*


### 로깅 라이브러리 활용
#### 로그 레벨
- 종류
  - TRACE, DEBUG, INFO, WARN, ERROR
- 순서
  - TRACE < DEBUG < INFO < WARN < ERROR 순으로 로그 레벨이 높아짐
  - 로그 레벨이 높을 수록 출력되는 메시지는 적어짐
  - 로그 레벨이 낮을 수록 더 많은 로깅 레벨이 출력
  - ex) WARN으로 설정 시 WARN, ERROR 레벨의 메시지만 출력
- 로깅 메세지 구현을 통해 로그 레벨 결정
  - log.trace(), log.debug(), log.info(), log.warn(), log.error()
- 메시지 생성
  - ex) log.debug("New Client Connet! IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
  - 메시지에 인자를 전달하는데 문자열을 더하는 비용을 줄이기 위해 {}를 이용해 동적인 메시지를 구현
  - 메소드에서 로그 레벨에 따라 메시지를 더할 필요가 있는지의 여부를 판단하여 비용 감소
- logback.xml
  - Logback의 로그 레벨과 메시지 형식에 대한 설정 파일
  
### 자바의 스레드
#### 자바에서 스레드를 생성하는 방법
1. thread class를 상속받아 run() 메서드 작성

### 자바의 입출력 스트림

### HTTP
#### HTTP Header
- 요청 헤더
- 응답 헤더

### 네트워크
- Client-Server

#### HTTP Status Code

### 웹 서버



## 2. HTTP 웹 서버에 대한 요구사항 구현