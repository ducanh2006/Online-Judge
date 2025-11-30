package com.onlinejudge.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.onlinejudge.dto.PageResponse;
import com.onlinejudge.dto.SubmissionDTO;
import com.onlinejudge.payload.request.CreateSubmissionRequest;
import com.onlinejudge.payload.response.SubmissionCreatedResponse;
import com.onlinejudge.utils.JwtUtils;
import com.onlinejudge.service.SubmissionService;
import com.openai.client.OpenAIClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;


// @Controller
@RestController
@CrossOrigin
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired private SubmissionService submissionService;
    @Autowired private JwtUtils jwtService;

    private final ExecutorService executor = Executors.newCachedThreadPool();


    @PostMapping
    public ResponseEntity<SubmissionCreatedResponse> createSubmission(@RequestBody CreateSubmissionRequest request, 
    @RequestHeader Map<String,String> headers
    ,Authentication authentication
    ) {
        System.out.println(" boolean = "+ authentication.isAuthenticated());
        try {
            System.out.println(" authentication  =");
            if( authentication == null ){
                System.out.println("null");
            }
            else 
            {
                System.out.println("vao else __________________");
                System.out.println(authentication.getName());
                System.out.println(authentication.getDetails());
                System.out.println(authentication.getPrincipal());
                System.out.println(authentication);
                System.out.println("______________________");
            }
            // System.out.println(authentication);
        } catch (Exception e) {
            System.out.println(" loi roi");
            // System.out.println(" exepction = "+e.getMessage());
        }
        headers.forEach((key, value) -> {
        System.out.println("Key: " + key + ", Value: " + value);
    });
        // SubmissionCreatedResponse response = submissionService.createSubmission(request, authentication.getName());
        System.out.println(request);
        try {
            System.out.println(" start_service_create_submission");
            
            System.out.println(" authentication.getName = "+ authentication.getName());

            SubmissionCreatedResponse response = submissionService.createSubmission(request, authentication.getName());
            return ResponseEntity.status(202).body(response);
        } catch (Exception e) {
            System.out.println(" bi loi roi ne");
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println(" chay den pha cuoi cung truoc khi return ");
        return ResponseEntity.status(202).body(null);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam int id, @RequestParam int type, @RequestParam String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }
        SseEmitter emitter = new SseEmitter(0L);

        // --- TẠO params TRƯỚC KHI CHẠY THREAD ASYNC ---
        ChatCompletionCreateParams params;
        OpenAIClient client;
        try {
            // đảm bảo method này load mọi dữ liệu cần thiết (description, input, etc.)
            // và trả về một ChatCompletionCreateParams đã hoàn thiện (không chứa proxies)
            params = submissionService.createChatCompletionCreateParams(id, type);
            client = submissionService.createOpenAIClient();
        } catch (Exception e) {
            // Chưa open stream lâu — có thể trả lỗi ngay
            try {
                emitter.send(SseEmitter.event().name("error").data("❌ Error preparing streaming: " + e.getMessage()));
            } catch (Exception ignored) {}
            emitter.completeWithError(e);
            return emitter;
        }

        executor.execute(() -> {
            StringBuilder pointBuilder = new StringBuilder(); 
            
            try {
                try (StreamResponse<ChatCompletionChunk> stream = client.chat().completions().createStreaming(params)) {
                    stream.stream().forEach(chunk -> {
                        chunk.choices().get(0).delta().content().ifPresent(str -> {
                            submissionService.safeSendSse(emitter, "message", str);
                            if(type == 1){
                                pointBuilder.append(str); 
                            }
                        });
                    });
                    submissionService.safeSendSse(emitter, "complete", "✅ Stream finished");
                    if(type == 1){
                        try {
                            short pointValue = Short.parseShort(pointBuilder.toString().trim()); 
                            submissionService.requestRegrade(id, pointValue);
                        } catch (NumberFormatException e) {
                            submissionService.safeSendSse(emitter, "error", "❌ Invalid point format: " + pointBuilder.toString());
                        }
                    }
                    emitter.complete();
                } catch (Exception apiError) {
                    submissionService.safeSendSse(emitter, "error", "❌ API error: " + apiError.getMessage());
                    emitter.completeWithError(apiError);
                }
            } catch (Exception generalError) {
                submissionService.safeSendSse(emitter, "error", "❌ General error: " + generalError.getMessage());
                emitter.completeWithError(generalError);
            }
        });

        return emitter;
    }

    @GetMapping
    public ResponseEntity<PageResponse<SubmissionDTO>> getSubmissions(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer problemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        page  = Math.max(0, page-1);
        size  = Math.min(100, size);
        Sort sortOrder = Sort.by(Sort.Direction.DESC,"submittedAt" );
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        PageResponse<SubmissionDTO> result = submissionService.getSubmissions(userId, problemId, pageable);
        return ResponseEntity.ok(result);
        
    }
}
