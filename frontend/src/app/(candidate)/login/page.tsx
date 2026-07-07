"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useCandidateAuth } from "@/lib/candidate-auth";
import { Button, Card, Input } from "@/components/ui/primitives";
import { LogIn, UserPlus } from "lucide-react";
import Link from "next/link";

export default function CandidateLoginPage() {
  const router = useRouter();
  const { login, isAuthenticated } = useCandidateAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    router.push("/candidate/profile");
    return null;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      router.push("/candidate/profile");
    } catch (err: any) {
      setError(err.response?.data?.error?.message || "Credenciais inválidas");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <Card padding="lg" className="w-full max-w-sm">
        <div className="text-center mb-6">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary mx-auto mb-3">
            <span className="text-sm font-semibold text-text-primary">TF</span>
          </div>
          <h1 className="text-lg font-semibold text-primary">Portal do Candidato</h1>
          <p className="text-xs text-tertiary mt-1">Entre para gerenciar suas candidaturas</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
          />
          <Input
            label="Senha"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••"
          />

          {error && (
            <div className="rounded-lg bg-status-danger/10 border border-status-danger/20 px-3 py-2">
              <p className="text-xs text-status-danger">{error}</p>
            </div>
          )}

          <Button type="submit" className="w-full" loading={loading} leftIcon={<LogIn size={14} />}>
            Entrar
          </Button>
        </form>

        <p className="text-center text-xs text-tertiary mt-4">
          Não tem conta?{" "}
          <Link href="/candidate/register" className="text-accent hover:underline">
            Cadastre-se
          </Link>
        </p>
      </Card>
    </div>
  );
}
