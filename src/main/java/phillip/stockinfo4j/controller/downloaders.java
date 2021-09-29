package phillip.stockinfo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@RestController
@RequestMapping("downloaders")
public class downloaders {

    private WebClient webClient = WebClient.builder().build();

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/stockdaily")
    public void getStockDaily(@RequestParam("date") String date) {
        String completeUrl = "https://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20210929&type=ALLBUT0999";

        //定義請求頭的接收類型
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest request) throws IOException {
                request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                request.getHeaders().setAcceptCharset(Arrays.asList(Charset.forName("big5")));
            }
        };
        // getForObject會將所有返回直接放到內存中,使用流來替代這個操作
        ResponseExtractor<Void> responseExtractor = response -> {
            // Here I write the response to a file but do what you like
            Files.copy(response.getBody(), Paths.get("temp/123.csv"));
            return null;
        };
        restTemplate.execute(completeUrl, HttpMethod.GET, requestCallback, responseExtractor);
        //        Flux<DataBuffer> dataBufferFlux = webClient.get()
//                .uri("https://ram.komica2.net/00/src/1632913463709.jpg")
//                .accept(MediaType.ALL)
//                .retrieve()
//                .bodyToFlux(DataBuffer.class);
//        Path path = FileSystems.getDefault().getPath("temp/123.jpg");
//        DataBufferUtils
//                .write(dataBufferFlux, path, CREATE_NEW)
//                .block();
    }
}