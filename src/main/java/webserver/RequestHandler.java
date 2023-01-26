package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            // 클라이언트의 socket을 InputStream(Byte)에서 InputStreamReader(Char) UTF-8로 디코딩하고 BufferedReader로 한 줄씩 해석
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            // HTTP 요청의 첫 줄인 요청 라인 디버깅
            log.debug("request line : {}", line);

            // 무한루프 예외처리
            if (line == null) return;

            // 요청 라인을 split() 메서드로 각 요소 토근화
            String[] tokens = line.split(" ");
            // HTTP 요청의 Content-Length 를 저장
            int contentLength = 0;

            // HTTP Header 를 모두 읽을 때 까지 반복
            while (!line.equals("")){
                line = br.readLine();
                // HTTP header 부분 디버깅
                log.debug("header : {}", line);
                // 요청 본문에 Content-Length가 있으면 초기화
                if (line.contains("Content-Length")) contentLength = getContentLength(line);
            }

            // url 로 회원가입 분기처리
            String url = tokens[1];
            if (url.startsWith("/user/create")){
                // 요청 본문의 길이 만큼 데이터를 읽어옴 - 필자가 구현해둔 IOUtils.readData() 활용
                String body = IOUtils.readData(br, contentLength);
                // 쿼리 스트링 정보를 Map으로 저장, 필자가 구현해둔 파싱 메서드를 활용
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                // 사용자를 생성
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("User : {}", user);
            } else {
                DataOutputStream dos = new DataOutputStream(out);
                // file을 file io와 NIO로 처리
                byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // 요청 본문에서 Content-Length 값을 추출하는 메서드
    private int getContentLength(String line){
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}