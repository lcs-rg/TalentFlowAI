import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  withCredentials: true, // Send httpOnly cookies
});

// Request interceptor: attach JWT from memory (set by AuthProvider)
api.interceptors.request.use((config) => {
  // Token is set on api.defaults.headers by AuthProvider after login/register
  // No localStorage access per frontend.md
  return config;
});

// Response interceptor: auto-refresh on 401
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const { data } = await axios.post(`${API_BASE_URL}/api/v1/auth/refresh`, {}, { withCredentials: true });
        api.defaults.headers.common["Authorization"] = `Bearer ${data.data.accessToken}`;
        original.headers["Authorization"] = `Bearer ${data.data.accessToken}`;
        return api(original);
      } catch {
        // Refresh failed — redirect to login
        if (typeof window !== "undefined") {
          localStorage.removeItem("talentflow-user");
          window.location.href = "/login";
        }
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);
