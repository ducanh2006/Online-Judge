package com.onlinejudge.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.onlinejudge.dto.PageResponse;
import com.onlinejudge.dto.SubmissionDTO;
import com.onlinejudge.entity.ProblemEntity;
import com.onlinejudge.entity.SubmissionEntity;
import com.onlinejudge.entity.UserEntity;
import com.onlinejudge.mapper.ProblemMapper;
import com.onlinejudge.mapper.SubmissionMapper;
import com.onlinejudge.payload.CreateSubmissionRequest;
import com.onlinejudge.payload.SubmissionCreatedResponse;
import com.onlinejudge.repository.ProblemRepository;
import com.onlinejudge.repository.SubmissionRepository;
import com.onlinejudge.repository.UserRepository;
import com.onlinejudge.spec.SubmissionSpecification;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
public class SubmissionService {
	@Value("${gemini.api.key}")
	private String apiKey;
	@Value("${gemini.api.url}")
	private String baseUrl;

    @Value("${gemini.api.model}")
	private String aiModel;
	
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final SubmissionMapper submissionMapper;
    private final ProblemMapper problemMapper;
    
//    public SubmissionService(SubmissionRepository submissionRepository, ProblemRepository problemRepository, UserRepository userRepository, SubmissionMapper submissionMapper) {
//        this.submissionRepository = submissionRepository;
//        this.problemRepository = problemRepository;
//        this.userRepository = userRepository;
//        this.submissionMapper = submissionMapper;
//    }

    @Transactional
    public SubmissionCreatedResponse createSubmission(CreateSubmissionRequest request, String username) {
        System.out.println(" start_service");
   	    System.out.println( " apiKey = "+ apiKey + " baseUrl = "+baseUrl );
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println(" chay xong den dong User ");
        ProblemEntity problem = problemRepository.findById(request.getProblemId()).orElseThrow(() -> new RuntimeException("Problem not found"));
        System.out.println(" chay xong den problem ");
        SubmissionEntity submission = SubmissionEntity.builder().user(user).problem(problem).yourSolution(request.getYourSolution()).status(SubmissionEntity.SubmissionStatus.Pending).build();
        System.out.println(" chay xong submission");    
        SubmissionEntity saved = submissionRepository.save(submission);
        System.out.println(" chay xong den save id");
        // submission.SubmissionStatus.Pending
        try {
            System.out.println(" vao try SubmissionEntity.SubmissionStatus.Pending = "+ SubmissionEntity.SubmissionStatus.Pending);
            return new SubmissionCreatedResponse(saved.getId(),SubmissionEntity.SubmissionStatus.Pending);

        } catch (Exception e) {
            System.out.println( e.getCause());
            return new SubmissionCreatedResponse(saved.getId(), saved.getStatus());

        }
        // return new SubmissionCreatedResponse(saved.getId(), saved.getStatus());
    }

    public OpenAIClient createOpenAIClient(){
        return OpenAIOkHttpClient.builder().apiKey(apiKey).baseUrl(baseUrl).build();
    }

    public ChatCompletionCreateParams createChatCompletionCreateParams(int id, int type) {
        SubmissionEntity submissionEntity = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with id: " + id));

        ProblemEntity problemEntity = submissionEntity.getProblem();
        String yourSolution = submissionEntity.getYourSolution();
        String problemDescription = problemEntity.getDescription();
        String correctSolution = problemEntity.getSolution();
        String prompt;

        if (type == 1) {
            // ---- TYPE 1: CHẤM ĐIỂM ----
            prompt = String.format(
                """
                You are an intelligent grading assistant. 
                Your task is to evaluate a user's solution to a problem on a scale of 0 to 100.

                Please consider correctness, completeness, and reasoning.
                Only output the integer score without any other text, explanations, or symbols.

                --- Problem Description ---
                %s

                --- Correct Solution ---
                %s

                --- User's Solution ---
                %s

                Output format:
                85
                """,
                problemDescription, correctSolution, yourSolution
            );
        } 
        else if (type == 2) {
            Short score = submissionEntity.getScore();
            // ---- TYPE 2: GIẢI THÍCH ----
            if (score == null)
                throw new IllegalArgumentException("Score must be provided for explanation type (type=2).");

            prompt = String.format(
                """
                You are an intelligent grading assistant.
                The user's solution has already been evaluated and given a score of %d/100.

                Now, explain in detail *why* this score was given.
                Your explanation should mention:
                - What the user did correctly.
                - What mistakes or gaps exist compared to the correct solution.
                - How the user could improve their answer.
                
                Please write in a concise, clear, and educational tone.

                --- Problem Description ---
                %s

                --- Correct Solution ---
                %s

                --- User's Solution ---
                %s
                """,
                score, problemDescription, correctSolution, yourSolution
            );
        } 
        else {
            throw new IllegalArgumentException("Invalid type: " + type + ". Must be 1 or 2.");
        }

        return ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(aiModel)
                .build();
    }
    public void safeSendSse(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (Exception e) {
            System.err.println("SSE send failed: " + e.getMessage());
        }
    }


    public PageResponse<SubmissionDTO> getSubmissions(Integer userId, Integer problemId, Pageable pageable) {
        Specification<SubmissionEntity> spec = SubmissionSpecification.withCriteria(userId, problemId);
        Page<SubmissionEntity> page = submissionRepository.findAll(spec, pageable);
        List<SubmissionDTO> dtos = page.getContent().stream().map(submissionMapper::toDto).collect(Collectors.toList());
        return PageResponse.<SubmissionDTO>builder().content(dtos).pageNumber(page.getNumber() + 1).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).isLast(page.isLast()).build();
    }

    public SubmissionDTO getSubmissionDetails(int id) {
        SubmissionEntity submission = submissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Submission not found"));
        return submissionMapper.toDto(submission);
    }

    public void requestRegrade(int id , short score) {
        SubmissionEntity submission = submissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setStatus(SubmissionEntity.SubmissionStatus.Completed);
        submission.setScore(score);
        submissionRepository.save(submission);
    }

}
