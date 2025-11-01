// package com.ducanh.controller;

// /**
//  * Compatibility stub after refactor:
//  * All major classes were moved into dedicated packages:
//  * - com.ducanh.model
//  * - com.ducanh.dto
//  * - com.ducanh.payload
//  * - com.ducanh.repository
//  * - com.ducanh.spec
//  * - com.ducanh.mapper
//  * - com.ducanh.service
//  * - com.ducanh.controller
//  */
// public final class All {
//     private All() {}
// }


// // =================================================================================
// // SECTION: Specifications for Filtering
// // =================================================================================

// class ProblemSpecification {

//     public static Specification<ProblemEntity> withCriteria(
//             List<Integer> subjectIds, List<Integer> tagIds, boolean matchAllTags) {
//         return (root, query, cb) -> {
//             query.distinct(true);
//             Predicate predicate = cb.conjunction();

//             if (!CollectionUtils.isEmpty(subjectIds)) {
//                 predicate = cb.and(predicate, root.get("subject").get("id").in(subjectIds));
//             }

//             if (!CollectionUtils.isEmpty(tagIds)) {
//                 Predicate tagPredicate = buildTagPredicate(root, query, cb, tagIds, matchAllTags);
//                 if(tagPredicate != null) {
//                     predicate = cb.and(predicate, tagPredicate);
//                 }
//             }

//             return predicate;
//         };
//     }

//     private static Predicate buildTagPredicate(Root<ProblemEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb, List<Integer> tagIds, boolean matchAllTags) {
//         Join<ProblemEntity, ProblemTagEntity> problemTagJoin = root.join("problemTagEntities", JoinType.LEFT);
//         Join<ProblemTagEntity, TagEntity> tagJoin = problemTagJoin.join("tag", JoinType.LEFT);
        
//         Predicate tagPredicate = tagJoin.get("id").in(tagIds);
        
//         if (matchAllTags) {
//             query.where(tagPredicate)
//                 .groupBy(root.get("id"))
//                 .having(cb.equal(cb.count(root.get("id")), (long) tagIds.size()));
//             return null;
//         } else {
//             return tagPredicate;
//         }
//     }
// }

// class SubmissionSpecification {
//      public static Specification<SubmissionEntity> withCriteria(Integer userId, Integer problemId) {
//          return (root, query, cb) -> {
//              Predicate predicate = cb.conjunction();
//              if (userId != null) {
//                  predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
//              }
//              if (problemId != null) {
//                  predicate = cb.and(predicate, cb.equal(root.get("problem").get("id"), problemId));
//              }
//              return predicate;
//          };
//      }
// }

// // =================================================================================
// // SECTION: Security (JWT, Config, Services)
// // =================================================================================

// @Component
// class JwtUtils {
//     private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

//     @Value("${app.jwtSecret}")
//     private String jwtSecret;

//     @Value("${app.jwtExpirationMs}")
//     private int jwtExpirationMs;

//     public String generateJwtToken(Authentication authentication) {
//         UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
//         return Jwts.builder()
//                 .setSubject((userPrincipal.getUsername()))
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                 .signWith(key(), SignatureAlgorithm.HS256)
//                 .compact();
//     }

//     private Key key() {
//         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//     }

//     public String getUserNameFromJwtToken(String token) {
//         return Jwts.parserBuilder().setSigningKey(key()).build()
//                    .parseClaimsJws(token).getBody().getSubject();
//     }

//     public boolean validateJwtToken(String authToken) {
//         try {
//             Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
//             return true;
//         } catch (Exception e) {
//             logger.error("JWT validation error: {}", e.getMessage());
//         }
//         return false;
//     }
// }

// @Getter
// class UserDetailsImpl implements UserDetails {
//     private final int id;
//     private final String username;
//     private final String email;
//     private final String password;
//     private final Collection<? extends GrantedAuthority> authorities;

//     public UserDetailsImpl(int id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
//         this.id = id;
//         this.username = username;
//         this.email = email;
//         this.password = password;
//         this.authorities = authorities;
//     }

//     public static UserDetailsImpl build(UserEntity user) {
//         List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//         return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), authorities);
//     }

//     @Override public boolean isAccountNonExpired() { return true; }
//     @Override public boolean isAccountNonLocked() { return true; }
//     @Override public boolean isCredentialsNonExpired() { return true; }
//     @Override public boolean isEnabled() { return true; }
// }

// @Component
// class JwtAuthTokenFilter extends OncePerRequestFilter {
//     @Autowired private JwtUtils jwtUtils;
//     @Autowired private UserDetailsServiceImpl userDetailsService;
//     private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         try {
//             String jwt = parseJwt(request);
//             if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                 String username = jwtUtils.getUserNameFromJwtToken(jwt);
//                 UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                 UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                 authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             }
//         } catch (Exception e) {
//             logger.error("Cannot set user authentication", e);
//         }
//         filterChain.doFilter(request, response);
//     }

//     private String parseJwt(HttpServletRequest request) {
//         String headerAuth = request.getHeader("Authorization");
//         return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ") ? headerAuth.substring(7) : null;
//     }
// }

// @Service
// class UserDetailsServiceImpl implements UserDetailsService {
//     @Autowired
//     UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         UserEntity user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//         return UserDetailsImpl.build(user);
//     }
// }

// @Configuration
// @EnableMethodSecurity(prePostEnabled = true)
// class WebSecurityConfig {
//     @Autowired
//     UserDetailsServiceImpl userDetailsService;
    
//     @Autowired
//     private JwtAuthTokenFilter jwtAuthTokenFilter;

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//         return authConfig.getAuthenticationManager();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http.csrf(csrf -> csrf.disable())
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/auth/**").permitAll()
//                 .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/problems/**", "/api/tags/**", "/api/subjects/**").permitAll()
//                 .anyRequest().authenticated()
//             );

//         http.authenticationProvider(authenticationProvider());
//         http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
//         return http.build();
//     }
// }


// // =================================================================================
// // SECTION: Mappers
// // =================================================================================

// @Component
// class ProblemMapper {
//     ProblemDTO toListDto(ProblemEntity entity) {
//         return ProblemDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 .difficulty(entity.getDifficulty())
//                 .lastUpdated(entity.getLastUpdated())
//                 .subject(SubjectDTO.builder().id(entity.getSubject().getId()).name(entity.getSubject().getName()).build())
//                 .tags(entity.getProblemTagEntities().stream()
//                         .map(pt -> TagDTO.builder().id(pt.getTag().getId()).name(pt.getTag().getName()).build())
//                         .collect(Collectors.toList()))
//                 .build();
//     }

//     ProblemDTO toDetailDto(ProblemEntity entity, boolean includeSolution) {
//         ProblemDTO dto = toListDto(entity);
//         dto.setDescription(entity.getDescription());
//         if(includeSolution) {
//             dto.setSolution(entity.getSolution());
//         }
//         return dto;
//     }
// }

// @Component
// class SubmissionMapper {
//     SubmissionDTO toDto(SubmissionEntity entity) {
//         return SubmissionDTO.builder()
//                 .id(entity.getId())
//                 .problemId(entity.getProblem().getId())
//                 .problemTitle(entity.getProblem().getTitle())
//                 .userId(entity.getUser().getId())
//                 .username(entity.getUser().getUsername())
//                 .yourSolution(entity.getYourSolution())
//                 .score(entity.getScore())
//                 .submittedAt(entity.getSubmittedAt())
//                 .status(entity.getStatus())
//                 .gradeJson(entity.getGradeJson())
//                 .gradedAt(entity.getGradedAt())
//                 .compiledPdfUrl(entity.getCompiledPdfUrl())
//                 .build();
//     }
// }


// // =================================================================================
// // SECTION: Services
// // =================================================================================

// @Service
// @RequiredArgsConstructor
// class ProblemService {

//     private final TagController tagController;
//     private final All problemRepository;
//     private final SubjectRepository subjectRepository;
//     private final TagRepository tagRepository;
//     private final ProblemMapper problemMapper;



//     public ProblemDTO getProblemDetails(int id, boolean isAdmin) {
//         // Sử dụng phương thức mới từ repository
//         ProblemEntity problem = problemRepository.findProblemByIdWithDetails(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));
//         return problemMapper.toDetailDto(problem, isAdmin);
//     }

//     // Phương thức tìm kiếm chính, đã điều chỉnh tham số
//     public PageResponse<ProblemDTO> searchProblems(Integer problemId, List<Integer> subjectIds, List<Integer> tagIds,
//                                                    boolean matchAll, int page, int size, String sort) {
    	

//     	Sort sortOrder = Sort.by(Sort.Direction.DESC, "lastUpdated");
//         if (sort != null) {
//              try {
//                  String[] parts = sort.split(",");
//                  if (parts.length == 2) {
//                      sortOrder = Sort.by(new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]));
//                  } 
//              } catch (Exception e) {}
//         } 
//         System.out.println(sortOrder);
        
//         Pageable pageable = PageRequest.of(page, size, sortOrder);

//         Page<ProblemEntity> problemPage;
        
//         if( problemId != null ) {
//             ProblemDTO problem = getProblemDetails(problemId, false);
//             PageResponse<ProblemDTO> response = PageResponse.<ProblemDTO>builder()
//                     .content(Collections.singletonList(problem))
//                     .pageNumber(1)
//                     .pageSize(1)
//                     .totalElements(1)
//                     .totalPages(1)
//                     .isLast(true)
//                     .build();
//             return response;
//         }else if (tagIds != null && !tagIds.isEmpty()) {
//         	System.out.println(" vao else if 1");
//             // Ưu tiên tag: nếu tag có dữ liệu, bỏ qua subject
//             problemPage = problemRepository.searchByTagIds(tagIds, matchAll, pageable);
//         } else if (subjectIds != null && !subjectIds.isEmpty()) {
//             // Nếu chỉ có subject
//         	System.out.println(" vao else if 2");
//             problemPage = problemRepository.searchBySubjectIds(subjectIds, pageable);
//         } else {
//             // Không có filter
//         	System.out.println(" vao else");
//             problemPage = problemRepository.findAllProblemsWithDetails(pageable);
//         }

//         List<ProblemDTO> dtos = problemPage.getContent().stream()
//                 .map(problemMapper::toListDto)
//                 .collect(Collectors.toList());

//         return PageResponse.<ProblemDTO>builder()
//                 .content(dtos)
//                 .pageNumber(problemPage.getNumber() + 1)
//                 .pageSize(problemPage.getSize())
//                 .totalElements(problemPage.getTotalElements())
//                 .totalPages(problemPage.getTotalPages())
//                 .isLast(problemPage.isLast())
//                 .build();
//     }
    
//     @Transactional
//     public ProblemDTO createProblem(ProblemRequest request) {
//         SubjectEntity subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
//         ProblemEntity problem = ProblemEntity.builder().title(request.getTitle()).description(request.getDescription()).solution(request.getSolution()).difficulty(request.getDifficulty()).subject(subject).build();
//         Set<TagEntity> tags = resolveTags(request.getTags());
//         problem.setProblemTagEntities(syncTags(problem, tags));
//         ProblemEntity savedProblem = problemRepository.save(problem);
//         return problemMapper.toDetailDto(savedProblem, true);
//     }

//     @Transactional
//     public ProblemDTO updateProblem(int id, ProblemRequest request) {
//         ProblemEntity problem = problemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Problem", "id", id));
//         SubjectEntity subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
//         problem.setTitle(request.getTitle());
//         problem.setDescription(request.getDescription());
//         problem.setSolution(request.getSolution());
//         problem.setDifficulty(request.getDifficulty());
//         problem.setSubject(subject);
//         Set<TagEntity> tags = resolveTags(request.getTags());
//         problem.getProblemTagEntities().clear();
//         problem.getProblemTagEntities().addAll(syncTags(problem, tags));
//         ProblemEntity updatedProblem = problemRepository.save(problem);
//         return problemMapper.toDetailDto(updatedProblem, true);
//     }
    
//     public void deleteProblem(int id) {
//         if (!problemRepository.existsById(id)) throw new ResourceNotFoundException("Problem", "id", id);
//         problemRepository.deleteById(id);
//     }

//     private Set<TagEntity> resolveTags(List<String> tagIdentifiers) {
//         if (CollectionUtils.isEmpty(tagIdentifiers)) return new HashSet<>();
//         Set<Integer> tagIds = tagIdentifiers.stream().filter(t -> t.matches("\\d+")).map(Integer::parseInt).collect(Collectors.toSet());
//         Set<String> tagNames = tagIdentifiers.stream().filter(t -> !t.matches("\\d+")).map(String::toLowerCase).collect(Collectors.toSet());
//         Set<TagEntity> existingTags = new HashSet<>();
//         if(!tagIds.isEmpty()) {
//             Set<TagEntity> foundByIds = tagRepository.findByIdIn(tagIds);
//             if(foundByIds.size() != tagIds.size()) throw new ResourceNotFoundException("One or more tags not found by ID.", null, foundByIds);
//             existingTags.addAll(foundByIds);
//         }
//         if(!tagNames.isEmpty()) {
//             List<TagEntity> foundByNames = tagRepository.findByNameIn(tagNames);
//             existingTags.addAll(foundByNames);
//             Set<String> foundNames = foundByNames.stream().map(TagEntity::getName).collect(Collectors.toSet());
//             tagNames.stream().filter(name -> !foundNames.contains(name)).map(newName -> TagEntity.builder().name(newName).build()).forEach(newTag -> existingTags.add(tagRepository.save(newTag)));
//         }
//         return existingTags;
//     }

//     private Set<ProblemTagEntity> syncTags(ProblemEntity problem, Set<TagEntity> tags) {
//         return tags.stream().map(tag -> ProblemTagEntity.builder().id(new ProblemTagId(problem.getId(), tag.getId())).problem(problem).tag(tag).build()).collect(Collectors.toSet());
//     }
// }

// @Service
// @RequiredArgsConstructor
// class TagService {
//     private final TagRepository tagRepository;

//     public List<TagDTO> getAllTags(String query) {
//         List<TagEntity> tags = StringUtils.hasText(query) ? tagRepository.findByNameContainsIgnoreCase(query) : tagRepository.findAll();
//         return tags.stream().map(this::toDto).collect(Collectors.toList());
//     }
    
//     public TagDTO createTag(TagRequest request) {
//         if(tagRepository.findByNameIgnoreCase(request.getName()).isPresent()) throw new ResourceConflictException("Tag with name '" + request.getName() + "' already exists.");
//         TagEntity tag = TagEntity.builder().name(request.getName()).build();
//         return toDto(tagRepository.save(tag));
//     }
    
//     public void deleteTag(int id) {
//         if (!tagRepository.existsById(id)) throw new ResourceNotFoundException("Tag", "id", id);
//         tagRepository.deleteById(id);
//     }
    
//     private TagDTO toDto(TagEntity entity) {
//         return TagDTO.builder().id(entity.getId()).name(entity.getName()).build();
//     }
// }

// @Service
// @RequiredArgsConstructor
// class SubjectService {
//     private final SubjectRepository subjectRepository;
    
//     public List<SubjectDTO> getAllSubjects() {
//         return subjectRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
//     }
    
//     public SubjectDTO createSubject(SubjectRequest request) {
//         if(subjectRepository.existsByName(request.getName())) throw new ResourceConflictException("Subject with name '" + request.getName() + "' already exists.");
//         SubjectEntity subject = SubjectEntity.builder().name(request.getName()).build();
//         return toDto(subjectRepository.save(subject));
//     }
    
//     public void deleteSubject(int id) {
//         if(subjectRepository.isSubjectInUse(id)) throw new ResourceConflictException("Subject is in use by problems and cannot be deleted.");
//         if (!subjectRepository.existsById(id)) throw new ResourceNotFoundException("Subject", "id", id);
//         subjectRepository.deleteById(id);
//     }
    
//     private SubjectDTO toDto(SubjectEntity entity) {
//         return SubjectDTO.builder().id(entity.getId()).name(entity.getName()).build();
//     }
// }

// @Service
// @RequiredArgsConstructor
// class SubmissionService {
//     private final SubmissionRepository submissionRepository;
//     private final All problemRepository;
//     private final UserRepository userRepository;
//     private final SubmissionMapper submissionMapper;

//     @Transactional
//     public SubmissionCreatedResponse createSubmission(CreateSubmissionRequest request, String username) {
//         UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
//         ProblemEntity problem = problemRepository.findById(request.getProblemId()).orElseThrow(() -> new ResourceNotFoundException("Problem", "id", request.getProblemId()));
//         SubmissionEntity submission = SubmissionEntity.builder().user(user).problem(problem).yourSolution(request.getYourSolution()).status(SubmissionEntity.SubmissionStatus.PENDING).build();
//         SubmissionEntity saved = submissionRepository.save(submission);
//         return new SubmissionCreatedResponse(saved.getId(), saved.getStatus());
//     }
    
//     public PageResponse<SubmissionDTO> getSubmissions(Integer userId, Integer problemId, Pageable pageable) {
//         Specification<SubmissionEntity> spec = SubmissionSpecification.withCriteria(userId, problemId);
//         Page<SubmissionEntity> page = submissionRepository.findAll(spec, pageable);
//         List<SubmissionDTO> dtos = page.getContent().stream().map(submissionMapper::toDto).collect(Collectors.toList());
//         return PageResponse.<SubmissionDTO>builder().content(dtos).pageNumber(page.getNumber() + 1).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).isLast(page.isLast()).build();
//     }
    
//     public SubmissionDTO getSubmissionDetails(int id) {
//         SubmissionEntity submission = submissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
//         return submissionMapper.toDto(submission);
//     }

//     public void requestRegrade(int id) {
//         SubmissionEntity submission = submissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
//         submission.setStatus(SubmissionEntity.SubmissionStatus.PENDING);
//         submissionRepository.save(submission);
//     }
// }

// // =================================================================================
// // SECTION: Controllers
// // =================================================================================

// @RestController
// @RequestMapping("/api/problems")
// @RequiredArgsConstructor
// class ProblemController {
//     private final ProblemService problemService;

//     @GetMapping
//     public ResponseEntity<PageResponse<ProblemDTO>> searchProblems(
//             @RequestParam(required = false) Integer problemId, 
//             @RequestParam(required = false) List<Integer> subject, 
//             @RequestParam(required = false) List<Integer> tag,
//             @RequestParam(defaultValue = "false") boolean match,
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "20") int size,
//             @RequestParam(defaultValue = "lastUpdated,desc") String sort) {

//         page = Math.max(1, page);
//         size = Math.min(100, Math.max(1, size));
//         PageResponse<ProblemDTO> response = problemService.searchProblems(problemId, subject, tag, match, page - 1, size, sort);
//         return ResponseEntity.ok(response);
//     }
    
//     @GetMapping("/{id}")
//     public ResponseEntity<ProblemDTO> getProblemById(@PathVariable int id) {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         boolean isAdmin = authentication != null && authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals);
//         ProblemDTO problem = problemService.getProblemDetails(id, isAdmin);
//         return ResponseEntity.ok(problem);
//     }
    
//     @PostMapping
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ProblemDTO> createProblem(@Valid @RequestBody ProblemRequest request) {
//         ProblemDTO createdProblem = problemService.createProblem(request);
//         return new ResponseEntity<>(createdProblem, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ProblemDTO> updateProblem(@PathVariable int id, @Valid @RequestBody ProblemRequest request) {
//         ProblemDTO updatedProblem = problemService.updateProblem(id, request);
//         return ResponseEntity.ok(updatedProblem);
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<Void> deleteProblem(@PathVariable int id) {
//         problemService.deleteProblem(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// @RestController
// @RequestMapping("/api/tags")
// @RequiredArgsConstructor
// class TagController {
//     private final TagService tagService;

//     @GetMapping
//     public ResponseEntity<List<TagDTO>> getTags(@RequestParam(required = false) String q) {
//         return ResponseEntity.ok(tagService.getAllTags(q));
//     }
    
//     @PostMapping
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagRequest request) {
//         return new ResponseEntity<>(tagService.createTag(request), HttpStatus.CREATED);
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<Void> deleteTag(@PathVariable int id) {
//         tagService.deleteTag(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// @RestController
// @RequestMapping("/api/subjects")
// @RequiredArgsConstructor
// class SubjectController {
//     private final SubjectService subjectService;

//     @GetMapping
//     public ResponseEntity<List<SubjectDTO>> getSubjects() {
//         return ResponseEntity.ok(subjectService.getAllSubjects());
//     }

//     @PostMapping
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectRequest request) {
//         return new ResponseEntity<>(subjectService.createSubject(request), HttpStatus.CREATED);
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<Void> deleteSubject(@PathVariable int id) {
//         subjectService.deleteSubject(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// @RestController
// @RequestMapping("/api/submissions")
// @RequiredArgsConstructor
// class SubmissionController {
//     private final SubmissionService submissionService;

//     @PostMapping
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<SubmissionCreatedResponse> createSubmission(@Valid @RequestBody CreateSubmissionRequest request, Authentication authentication) {
//         SubmissionCreatedResponse response = submissionService.createSubmission(request, authentication.getName());
//         return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
//     }

//     @GetMapping
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<PageResponse<SubmissionDTO>> getSubmissions(
//             @RequestParam(required = false) Integer userId,
//             @RequestParam(required = false) Integer problemId,
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "20") int size,
//             @RequestParam(defaultValue = "submittedAt,desc") String[] sort,
//             Authentication authentication) {
//         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//         Integer finalUserId = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ? userId : userDetails.getId();
//         Sort sortOrder = Sort.by(Arrays.stream(sort).map(s -> s.split(",")).map(parts -> new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0])).collect(Collectors.toList()));
//         Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(100, size), sortOrder);
//         return ResponseEntity.ok(submissionService.getSubmissions(finalUserId, problemId, pageable));
//     }
    
//     @GetMapping("/{id}")
//     @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable int id, Authentication authentication) {
//         SubmissionDTO submission = submissionService.getSubmissionDetails(id);
//         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//         boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//         if (!isAdmin && userDetails.getId() != submission.getUserId()) {
//             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//         }
//         return ResponseEntity.ok(submission);
//     }

//     @PostMapping("/{id}/regrade")
//     @PreAuthorize("hasRole('ADMIN') or @submissionSecurity.isOwner(authentication, #id)")
//     public ResponseEntity<Void> regradeSubmission(@PathVariable int id) {
//         submissionService.requestRegrade(id);
//         return ResponseEntity.accepted().build();
//     }
// }

// @Component("submissionSecurity")
// class SubmissionSecurity {
//     @Autowired SubmissionRepository submissionRepository;
//     public boolean isOwner(Authentication authentication, int submissionId) {
//         UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//         return submissionRepository.findById(submissionId).map(s -> s.getUser().getId() == userDetails.getId()).orElse(false);
//     }
// }

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// class AuthController {
//     private final AuthenticationManager authenticationManager;
//     private final UserRepository userRepository;
//     private final PasswordEncoder encoder;
//     private final JwtUtils jwtUtils;

//     @PostMapping("/login")
//     public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//         Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//         SecurityContextHolder.getContext().setAuthentication(authentication);
//         String jwt = jwtUtils.generateJwtToken(authentication);
//         return ResponseEntity.ok(new JwtResponse(jwt));
//     }

//     @PostMapping("/register")
//     public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
//         if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên người dùng đã tồn tại. Vui lòng chọn một tên khác.");
//         }

//         if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng. Vui lòng chọn một email khác.");
//         }
//         String encodedPassword = encoder.encode(signUpRequest.getPassword());

//         String finalDisplayName = signUpRequest.getDisplayName();
//         if (finalDisplayName == null || finalDisplayName.isEmpty()) {
//             finalDisplayName = generateRandomString(10);
//         }

//         UserEntity newUser = UserEntity.builder()
//             .username(signUpRequest.getUsername())
//             .password(encodedPassword)
//             .email(signUpRequest.getEmail())
//             .fullName(signUpRequest.getFullName())
//             .displayName(finalDisplayName)
//             .role(UserEntity.Role.USER)
//             .build();

//         // 6. Lưu người dùng và trả về phản hồi thành công
//         userRepository.save(newUser);
//         return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký người dùng thành công!");
//     }

//     // Phương thức hỗ trợ tạo chuỗi ngẫu nhiên
//     private String generateRandomString(int length) {
//         String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//         StringBuilder result = new StringBuilder();
//         java.util.Random random = new java.util.Random();
//         for (int i = 0; i < length; i++) {
//             result.append(characters.charAt(random.nextInt(characters.length())));
//         }
//         return result.toString();
//     }
// }

// // =================================================================================
// // SECTION: Exception Handling
// // =================================================================================

// @ResponseStatus(HttpStatus.NOT_FOUND)
// @Getter
// class ResourceNotFoundException extends RuntimeException {
//     public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
//         super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
//     }
// }

// @ResponseStatus(HttpStatus.CONFLICT)
// class ResourceConflictException extends RuntimeException {
//     public ResourceConflictException(String message) { super(message); }
// }

// @ControllerAdvice
// class GlobalExceptionHandler {
//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
//         return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
//     }

//     @ExceptionHandler(ResourceConflictException.class)
//     public ResponseEntity<Object> handleResourceConflictException(ResourceConflictException ex, WebRequest request) {
//         return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
//     }
    
//     private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
//         Map<String, Object> body = new HashMap<>();
//         body.put("timestamp", LocalDateTime.now());
//         body.put("status", status.value());
//         body.put("error", status.getReasonPhrase());
//         body.put("message", ex.getMessage());
//         body.put("path", request.getDescription(false).substring(4));
//         return new ResponseEntity<>(body, status);
//     }
// }