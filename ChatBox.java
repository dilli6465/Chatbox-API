package sunbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class ChatBox{

    private static final String API_URL = "https://api-inference.huggingface.co/models/meta-llama/Meta-Llama-3-8B-Instruct";
    private static final String HF_API_KEY = "Your_Hugging_Face_API_Key_Here";

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Prompt is required");
        }

        try {
            String response = query(prompt);
            return ResponseEntity.ok(response != null ? response : "Sorry, something went wrong. Please try again.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    private String query(String prompt) {
        String token = "Your_Generative_AI_Token_Here"; // Get your token from Generative AI
        String url = "https://api.openai.com/v1/completions";
        
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-bison-001");
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", 0);
        requestBody.put("max_tokens", 800);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JSONObject responseJson = new JSONObject(responseEntity.getBody());
            return responseJson.getString("choices").getJSONObject(0).getString("text");
        } else {
            throw new RuntimeException("Failed to query Generative AI: " + responseEntity.getStatusCodeValue());
        }
    }
}
