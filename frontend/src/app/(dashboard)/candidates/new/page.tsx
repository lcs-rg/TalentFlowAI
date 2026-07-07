"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useMutation } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { Button, Card, Input } from "@/components/ui/primitives";
import { ArrowLeft, User } from "lucide-react";
import Link from "next/link";

export default function NewCandidatePage() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [error, setError] = useState("");

  const createMutation = useMutation({
    mutationFn: async () => {
      const { data: res } = await api.post("/api/v1/candidates", { name, email, phone });
      return res.data;
    },
    onSuccess: (data) => {
      router.push(`/candidates/${data.id}`);
    },
    onError: (err: any) => {
      setError(err.response?.data?.error?.message || "Erro ao criar candidato");
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !email.trim()) {
      setError("Nome e email são obrigatórios");
      return;
    }
    setError("");
    createMutation.mutate();
  };

  return (
    <div className="max-w-lg space-y-8">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Link href="/candidates" className="p-1.5 rounded-lg hover:bg-bg-elevated-2 text-text-secondary">
          <ArrowLeft size={16} />
        </Link>
        <div>
          <h1 className="text-lg font-semibold tracking-tight text-primary">Novo candidato</h1>
          <p className="mt-1 text-sm text-tertiary">Cadastre um novo candidato no sistema</p>
        </div>
      </div>

      <Card padding="lg">
        <form onSubmit={handleSubmit} className="space-y-5">
          <Input
            label="Nome"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Nome completo do candidato"
          />
          <Input
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="email@exemplo.com"
          />
          <Input
            label="Telefone"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="(11) 99999-9999"
          />

          {error && (
            <div className="rounded-lg bg-status-danger/10 border border-status-danger/20 px-3 py-2">
              <p className="text-xs text-status-danger">{error}</p>
            </div>
          )}

          <div className="flex gap-3 pt-2">
            <Button type="submit" loading={createMutation.isPending}>
              Criar candidato
            </Button>
            <Link href="/candidates">
              <Button variant="ghost" type="button">Cancelar</Button>
            </Link>
          </div>
        </form>
      </Card>
    </div>
  );
}
