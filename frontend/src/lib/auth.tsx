"use client";

import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { api } from "./api";

interface User {
  id: string;
  tenantId: string;
  email: string;
  name: string;
  role: string;
  avatarUrl?: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string, tenantSlug: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
}

interface RegisterData {
  email: string;
  password: string;
  name: string;
  companyName: string;
  companySlug: string;
}

const AuthContext = createContext<AuthContextType | null>(null);

const TOKEN_KEY = "talentflow-token";
const USER_KEY = "talentflow-user";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Rehydrate session on mount
  useEffect(() => {
    const storedUser = localStorage.getItem(USER_KEY);
    const storedToken = localStorage.getItem(TOKEN_KEY);
    if (storedUser && storedToken) {
      try {
        setUser(JSON.parse(storedUser));
        api.defaults.headers.common["Authorization"] = `Bearer ${storedToken}`;
      } catch { /* ignore */ }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string, tenantSlug: string) => {
    const payload: any = { email, password };
    if (tenantSlug) payload.tenantSlug = tenantSlug;
    const { data: res } = await api.post("/api/v1/auth/login", payload);
    const { accessToken, refreshToken, user: u } = res.data;

    // Store token for session persistence
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem(TOKEN_KEY, accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(u));

    // Refresh token cookie (no Secure in dev, so it works on localhost)
    document.cookie = `refreshToken=${refreshToken}; path=/; max-age=604800; SameSite=Lax`;

    setUser(u);
  };

  const register = async (data: RegisterData) => {
    const { data: res } = await api.post("/api/v1/auth/register", data);
    const { accessToken, refreshToken, user: u } = res.data;

    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem(TOKEN_KEY, accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(u));

    document.cookie = `refreshToken=${refreshToken}; path=/; max-age=604800; SameSite=Lax`;

    setUser(u);
  };

  const logout = async () => {
    try { await api.post("/api/v1/auth/logout"); } catch { /* ignore */ }
    delete api.defaults.headers.common["Authorization"];
    document.cookie = "refreshToken=; path=/; max-age=0";
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
}
