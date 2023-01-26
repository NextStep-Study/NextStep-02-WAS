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
   
4. 보안 그룹 규칙
   - 인바운드 : 외부에서 EC2 인스턴스로 들어오는 트래픽. 대표적인 것들로는 HTTP, HTTPS, SSH, RDP 등
   - 아웃바운드 : EC2 인스턴스에서 외부로 나가는 트래픽. EC2 인스턴스 안에서 인터넷을 사용할 경우 Outbound
   - 인바운드 규칙에서 내 IP에서만 접속하도록 해두었기 때문에 putty 접속할 때 [EC2 > 인스턴스 > 보안 > 보안 그룹 선택 > 인바운드  > 소스] ip 확인할 것
   
## 원격 서버 배포 요구사항
- 로컬 개발 환경에 설치한 HTTP 웹 서버를 물리적으로 떨어져 있는 원격 서버에 배포해 정상적으로 동작하는지 테스트
- HTTP 웹 서버 배포 작업은 root 계정이 아닌 배포를 담당할 새로운 계정을 만들어 진행
  - AWS EC2의 경우 이미 계정이 추가되어 있기 때문에 계정 추가 및 sudo 권한 할당 과정 생략

### 원격 서버 (AWS EC2)에 배포하기 
1. ssh로 서버에 접속새 계정 추가 및 sudo 권할 할당
    - EC2 ubuntu에서는 사용자 이름 root와 ubuntu로 계정이 이미 있어서 생략
    - [ubuntu@탄력적 IP]로 ssh 접속 세팅
   
2. 각 계정별 UTF-8 인코딩 설정해 한글 이슈 해결
    - Locale이란 세계 각 나라에서 가지고 있는 언어, 날짜, 시간 등에 관해 i18n(국제화)를 통해 같은 프로그램이더라도 OS별로 설정되어있는 것에 따라 어떤 방식으로 출력할지 결정하게 되는 것
    - 한국의 Locale은 보통 ko_KR.UTF-8로 사용. 오래된 서버라면 ko_KR.EUC-KR일 수도 있음
    - AWS 우분투 이미지는 아마 기본 설정으로 en-US.UTF-8
    - sudo dpkg-reconfigure locales 로 시스템 계정 locale ko_KR.UTF-8로 설정. 시스템 디폴트도 맞춰줌
   
3. JDK, 메이븐 설치
   - apt로 JDK와 메이븐 설치 및 bash_profile 에 PATH 설정 
     - apt 방식은 os 버전에 상관없이 최신 버전으로 다운 받아지며 모든 사용자가 사용할 수 있도록 usr/bin에 폴더에 자동으로 설치 
     - 누가 언제 어디서 버전을 업데이트하고 설치했는지 지속적으로 모니터링이 가능하므로 안전한 설치방법
   - openjdk 18.0.2 
     - JAVA_HOME=usr/lib/jvm/java-18-openjdk-amd64
   - maven 3.6.3 
     - MAVEN_HOME=usr/share/maven
   - [apt 참고](https://chucoding.tistory.com/57)
   - [java 설치 참고](https://qjadud22.tistory.com/27) 
   - [maven 설치 참고](https://chucoding.tistory.com/62) 
   - [vi 편집기 사용 참고](https://jhnyang.tistory.com/54)
   - [bash_profile 참고](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=jeong2091&logNo=221995920586)
   - [PATH 설정 참고](https://jjeongil.tistory.com/1396)

4. Git 설치, clone 및 빌드
- apt 명령어로 설치
  - git version 2.34.1
  - git clone으로 소스코드 받아서 빌드 후 실행
  - aws 보안그룹 인바운드 규칙에 tcp 규칙 사용자 지정 추가하기
  - 서버아이피:포트 접속 확인

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
1. Runnable 인터페이스 사용
    - run() 메세드 하나 뿐
2. Thread 클래스 사용
    - Thread 클래스는 Runnable 인터페이스를 구현한 클래스
#### 특징
- java.lang 패키지에 위치하며 import할 필요가 없음
- run() 메소드는 쓰레드가 수행되는 우리가 구현해야하는 부분
- start() 메소드는 스레드를 시작하는 메소드
    - start() 메소드를 만들지 않아도 java에서 run()을 수행하도록 동작

### HTTP
#### 요청 데이터
    - 요청라인 : 요청 데이터의 첫 번째 라인, "[HTTP 메서드] [URI] [HTTP 버전]" 으로 구성
    - 요청헤더: "<필드 이름> : <필드 값>" 형태로 구성, 요청 라인을 제외한 나머지 요청부분으로 마지막은 빈 문자열("")로 구성
    - 요청본문: 선택사항으로 헤더의 빈 문자열 이후에 추가될 수 있음
##### HTTP 메서드
    - GET : 서버에 존재하는 데이터를 조회하는 역할 담당
    - POST : 데이터의 상태를 변경하는 작업 담당
    - HEAD :
    - PUT :
    - DELETE :
    - PATCH :
    - TRACE :
    - OPTIONS :
- HTML에서 GET, POST 메서드만 사용하도록 지원하고 있으나, 최근 경향은 REST API 설계와 AJAX 기반으로 웹 애플리케이션을 개발하는 방향으로 발전하고 있음
- 메소드의 역할 및 특성을 이해하고 적절히 활용하는 것이 중요해졌다.

#### 응답
##### HTTP Status Code
    - 2xx :  성공, 클라이언트가 요청한 동작을 수신하여 이해 및 승낙하여 성공적으로 처리함
    - 3xx : 리다이렉션, 클라이언트는 요청을 마치기 위해 추가 동작이 필요함
    - 4xx : 요청 오류, 클라이언트에 오류가 있음
    - 5xx : 서버 오류, 서버가 유효한 요청을 명백하게 수행하지 못했음

### 네트워크
- Client-Server

### git push 되돌리기
- [push 되돌리기 참고](https://ninano1109.tistory.com/3)

## HTTP 웹 서버에 대한 요구사항 및 구현
### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
- webserver 패키지의 WebServer 클래스는 웹 서버를 시작하고 사용자의 요청이 있을 때까지 대기 상태에 있다가 사용자 요청이 있을 경우 사용자 요청을 RequestHandler 클래스에 위임
  - 사용자 요청이 발생할 때까지 대기 상태에 있도록 지우너한느 역할은 자바에 포함되어있는 SeverSocket 클래스가 담당
  - WebServer 클래스는 ServerSocket에 사용자 요청이 발생하는 순간 클라이언트와 연결을 담당하는 Socket을 RequestHandler에 전달하면서 새로운 스레드를 실행하는 방식으로 멀티스레드 프로그래밍을 지원
- RequestHandler 클래스는 Thread를 상속하고 있으며, 사용자의 요청에 대한 처리와 응답에 대한 처리를 담당하는 가장 중심이 되는 클래스
  - RequestHandler 클래스의 run() 메소드에서 실습을 진행
  - run() 메소드의 복잡도가 증가하는 경우 새로운 클래스, 메소드로 분리하는 방식으로 리팩토링
  - run() 메소드에서 InputStream은 클라이언트(웹 브라우저)에서 서버로 요청을 보낼 때 전달되는 데이터, OutputStream은 서버에서 클라이언트에 응답을 보낼 때 전달되는 데이터를 담당하는 스트림
- InputStream 스트림 처리
  - 클라이언트에서 서버로 전송하는 데이터를 BufferedReader의 readLine() 메서드를 이용해 한 줄씩 읽어서 분석
  - InputStream은 byte stream이기 때문에 InputStreamReader로 character stream으로 바꾸어서 UTF-8로 디코딩 해줌 [InputStreamReader Char 디코딩 참고](https://codingdog.tistory.com/entry/java-inputstreamreader-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%B4-%EB%B4%85%EC%8B%9C%EB%8B%A4)
- 요청라인 부분을 토큰화하여 URI를 추출하고, ./webapp 경로로 파일 반환
  - java io와 NIO를 사용하여 파일 데이터 처리
  
### 요구사항 2 - get 방식으로 회원가입
- 요청라인에서 추출한 URI를 경로와 쿼리 스트링으로 분리
  - get 메서드로 요청시 매개변수가 uri에 쿼리 스트링으로 전달
- url 경로가 회원가입이면 쿼리 스트링을 분리하도록 구현
  - 쿼리 스트링을 파싱해서 사용자 회원가입 정보를 Map으로 저장하고 User 생성

### 요구사항 3 - post 방식으로 회원가입
- get 방식은 요청 데이터(URI)에 민감정보가 포함되어 전송되고, 길이제한이 있어 한계가 있음
  - 이를 POST 방식으로 수정
  - form 태그의 method 속성을 post로 수정
  - post 방식에서는 HTTP 요청의 본문(body)를 통해 전달되고, 본문 데이터에 대한 길이가 Content-Length라는 필드 이름으로 전달 됨
  - body로 위치만 바뀌고 쿼리 스트링과 동일하게 &으로 이어진 형식으로 전달됨
- 본문읽기
  - HTTP 요청의 body를 읽는 기능을 구현한 IOUtils.readData() 활용
  - Content-Length의 값으로 본문의 길이만큼 읽어 Map<String, String> 에 데이터를 저장
  - IOUtils.readData()는 br를 전달받아서 요청 본문이 시작되는 부분부터 요청 본문의 길이만큼 char[]에 읽어온 다음, String으로 변환하여 반환

### 요구사항 4 - redirect 방식으로 이동
- 회원가입을 완료하면 index.html로 이동하도록 구현
  - url 수정으로 index.html을 반환하게 되면 화면만 index.html이고 브라우저는 /user/create 요청 정보를 가지고 있게된다
  - 따라서, 사용자가 새로고침을 하면 다시 회원가입 요청이 중복되게 된다.
  - 이러한 문제를 해결하고자 302(redirect) 상태 응답으로 /index.html 로 리다이렉트 하도록 한다.
  - 경로는 /(루트) 상대경로로 지정해야지 /index.html로 요청 되므로 주의한다. (index.html로 설정시 /user/index.html을 요청하게 됨)

### 요구사항 5 - cookie
- HTTP는 기본적으로 stateless이다
  - 이러한 문제로 로그인한 상태를 기록할 방법이 필요하다.
  - 우선 클라이언트에 Cookie 저장하고 주고받는 방식으로 로그인 상태를 유지하도록 구현한다.
  - 

### 요구사항 6 - stylesheet 적용
- 

### heroku 서버에 배포 후
-