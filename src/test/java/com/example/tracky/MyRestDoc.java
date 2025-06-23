package test.java.com.example.tracky;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

// 4. 문서 만들기
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080) // build 폴더에 adoc 파일 생성
@AutoConfigureMockMvc // MockMvc 클래스가 IoC에 로드 | RestTemplate -> 자바스크립트의 fetch와 동일, 진짜 환경에 요청 가능
@ExtendWith({ SpringExtension.class, RestDocumentationExtension.class })
public abstract class MyRestDoc {
    @Autowired
    protected MockMvc mvc;
    protected RestDocumentationResultHandler document;

    @BeforeEach
    public void documentSetUp() {
        this.document = MockMvcRestDocumentation.document("{class-name}/{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
    }
}