
export interface TagDTO {
  id: number;
  name: string;
}

export interface SubjectDTO {
  id: number;
  name: string;
}

export interface ProblemDTO {
  id: number;
  title: string;
  difficulty: string; // 'byte' in java, represented as string here usually
  lastUpdated: string;
  subject?: SubjectDTO;
  tags: TagDTO[];
  description: string;
  solution?: string;
}

export interface SubmissionDTO {
  id: number;
  userId: number;
  problemId: number;
  yourSolution: string;
  score: number;
  submittedAt: string;
  status: 'Pending' | 'Completed';
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface LoginResponse {
  token: string; // Assuming standard JWT response structure
  username: string;
  role?: string;
}

export interface CreateSubmissionRequest {
  problemId: number;
  yourSolution: string;
}

export interface SubmissionCreatedResponse {
  submissionId: number;
  status: string;
}

export interface SubjectRequest {
  name: string;
}

export interface ProblemRequest {
  title: string;
  description: string;
  solution: string;
  difficulty: string;
  subjectId: number;
  tags: string[];
}

export interface ProblemSearchParams {
  page?: number;
  size?: number;
  id?: number;
  subject?: number[];
  tag?: number[];
  sort?: string;
}
