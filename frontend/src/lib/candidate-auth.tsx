"use client";

import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { api } from "./api";

interface CandidateUser {
  id: string;
  name: string;
  email: string;
  phone?: string;
  resumeUrl?: string;
}

interface CandidateAuthContextType {
  candidate: CandidateUser | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const CandidateAuthContext = createContext<CandidateAuthContextType | null>(null);

export function CandidateAuthProvider({ children }: { children: ReactNode }) {
  const [candidate, setCandidate] = useState<CandidateUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem("talentflow-candidate");
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        setCandidate(parsed);
        api.defaults.headers.common["Authorization"] = `Bearer ${parsed._token}`;
      } catch { /* ignore */ }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    const { data: res } = await api.post("/api/v1/candidate/auth/login", { email, password });
    const { accessToken, candidate: c } = res.data;
    const user = { ...c, _token: accessToken };
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem("talentflow-candidate", JSON.stringify(user));
    setCandidate(user);
  };

  const register = async (name: string, email: string, password: string) => {
    const { data: res } = await api.post("/api/v1/candidate/auth/register", { name, email, password });
    const { accessToken, candidate: c } = res.data;
    const user = { ...c, _token: accessToken };
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    localStorage.setItem("talentflow-candidate", JSON.stringify(user));
    setCandidate(user);
  };

  const logout = () => {
    delete api.defaults.headers.common["Authorization"];
    localStorage.removeItem("talentflow-candidate");
    setCandidate(null);
  };

  return (
    <CandidateAuthContext.Provider value={{ candidate, loading, login, register, logout, isAuthenticated: !!candidate }}>
      {children}
    </CandidateAuthContext.Provider>
  );
}

export function useCandidateAuth() {
  const ctx = useContext(CandidateAuthContext);
  if (!ctx) throw new Error("useCandidateAuth must be inside CandidateAuthProvider");
  return ctx;
}
