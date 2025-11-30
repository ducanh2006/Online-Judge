
import axios from 'axios';
import { 
  LoginResponse, 
  PageResponse, 
  ProblemDTO, 
  SubmissionDTO, 
  SubmissionCreatedResponse,
  CreateSubmissionRequest,
  SubjectDTO,
  SubjectRequest,
  ProblemRequest,
  TagDTO,
  ProblemSearchParams
} from '../types';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
});

// Request interceptor to add JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && token !== 'undefined' && token !== 'null') {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// Auth Services
export const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    const response = await api.post('/auth/login', { username, password });
    const data = response.data;
    
    // Robustly extract token
    // The backend might return { token: "..." } or { accessToken: "..." } or just the string
    let token = '';
    if (data && typeof data === 'object') {
        token = data.token || data.accessToken || data.jwt;
    } else if (typeof data === 'string') {
        token = data;
    }

    if (!token) {
        throw new Error("Token not found in login response");
    }

    // Return structured response
    return { 
        token, 
        username: (data && data.username) ? data.username : username,
        role: (data && data.role) ? data.role : undefined
    }; 
  },
  register: async (data: any) => {
    return api.post('/auth/register', data);
  },
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  }
};

// Tag Services
export const tagService = {
  getAll: async (q?: string): Promise<TagDTO[]> => {
    const response = await api.get('/tags', { params: { q } });
    return response.data;
  }
};

// Subject Services
export const subjectService = {
  getAll: async (): Promise<SubjectDTO[]> => {
    const response = await api.get('/subjects');
    return response.data;
  },
  create: async (data: SubjectRequest): Promise<SubjectDTO> => {
    const response = await api.post('/subjects', data);
    return response.data;
  }
};

// Problem Services
export const problemService = {
  getAll: async (searchParams: ProblemSearchParams = {}): Promise<PageResponse<ProblemDTO>> => {
    // Default params
    const params = {
      page: 1,
      size: 20,
      sort: 'lastUpdated,desc',
      ...searchParams
    };

    const response = await api.get('/problems', { 
      params,
      // Custom serializer to handle arrays (subject=1&subject=2) correctly for Spring Boot
      paramsSerializer: (params) => {
        const searchParams = new URLSearchParams();
        for (const key in params) {
          const value = params[key];
          if (Array.isArray(value)) {
            value.forEach(v => searchParams.append(key, v.toString()));
          } else if (value !== undefined && value !== null && value !== '') {
            searchParams.append(key, value.toString());
          }
        }
        return searchParams.toString();
      }
    });
    return response.data;
  },
  getById: async (id: number): Promise<ProblemDTO> => {
    // Search by ID using the search endpoint
    const response = await api.get('/problems', { params: { id } });
    // The API returns a PageResponse, so we extract the first item from the content list.
    if (response.data && response.data.content && Array.isArray(response.data.content)) {
      if (response.data.content.length > 0) {
        return response.data.content[0];
      }
      throw new Error("Problem not found");
    }
    // Fallback if the backend returns the object directly
    return response.data;
  },
  create: async (data: ProblemRequest): Promise<ProblemDTO> => {
    const response = await api.post('/problems', data);
    return response.data;
  }
};

// Submission Services
export const submissionService = {
  create: async (data: CreateSubmissionRequest): Promise<SubmissionCreatedResponse> => {
    const response = await api.post('/submissions', data);
    return response.data;
  },
  getAll: async (page = 1, size = 20): Promise<PageResponse<SubmissionDTO>> => {
    const response = await api.get('/submissions', { params: { page, size } });
    return response.data;
  },
  // The stream URL is used directly in EventSource
  getStreamUrl: (id: number, type: number, token: string) => {
    return `${API_URL}/submissions/stream?id=${id}&type=${type}&token=${encodeURIComponent(token)}`;
  }
};

export default api;
