"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useCandidateAuth } from "@/lib/candidate-auth";
import { api } from "@/lib/api";
import { Button, Card, Input, Skeleton } from "@/components/ui/primitives";
import { User, Mail, Phone, LogOut, Briefcase, FileText, Save } from "lucide-react";
import Link from "next/link";
import { useState, useEffect } from "react";
import toast from "react-hot-toast";

export default function CandidateProfilePage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { candidate, logout, isAuthenticated, loading: authLoading } = useCandidateAuth();
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [resumeText, setResumeText] = useState("");

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.push("/candidate/login");
  }, [authLoading, isAuthenticated, router]);

  const { data: profile, isLoading } = useQuery({
    queryKey: ["candidate-profile"],
    queryFn: async () => {
      const { data: res } = await api.get("/api/v1/candidate/me");
      return res.data;
    },
    enabled: isAuthenticated,
  });

  useEffect(() => {
    if (profile) {
      setName(profile.name || "");
      setPhone(profile.phone || "");
      setResumeText(profile.resumeText || "");
    }
  }, [profile]);

  const updateMutation = useMutation({
    mutationFn: async () => {
      await api.put("/api/v1/candidate/me", { name, phone, resumeText });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["candidate-profile"] });
      toast.success("Perfil atualizado!");
    },
    onError: () => toast.error("Erro ao atualizar"),
  });

  if (authLoading || isLoading) {
    return (
      <div className="max-w-lg mx-auto p-8 space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto p-4 md:p-8 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold text-primary">Meu Perfil</h1>
          <p className="text-xs text-tertiary mt-0.5">{candidate?.email}</p>
        </div>
        <Button variant="ghost" size="sm" onClick={() => { logout(); router.push("/candidate/login"); }} leftIcon={<LogOut size={12} />}>
          Sair
        </Button>
      </div>

      {/* Nav */}
      <div className="flex gap-2 border-b border-border pb-3">
        <Link href="/candidate/profile" className="text-sm font-medium text-accent">Perfil</Link>
        <Link href="/candidate/applications" className="text-sm text-tertiary hover:text-secondary">Candidaturas</Link>
      </div>

      {/* Profile form */}
      <Card padding="lg">
        <div className="space-y-4">
          <Input label="Nome" value={name} onChange={(e) => setName(e.target.value)} />
          <Input label="Telefone" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="(11) 99999-9999" />
          <div className="space-y-1.5">
            <label className="block text-xs font-medium text-secondary">Currículo (texto)</label>
            <textarea
              value={resumeText}
              onChange={(e) => setResumeText(e.target.value)}
              rows={8}
              placeholder="Cole o texto do seu currículo aqui..."
              className="w-full rounded-lg bg-tertiary px-3 py-2 text-sm text-primary placeholder:text-tertiary border border-primary focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500 resize-y"
            />
          </div>
          <Button onClick={() => updateMutation.mutate()} loading={updateMutation.isPending} leftIcon={<Save size={14} />}>
            Salvar
          </Button>
        </div>
      </Card>
    </div>
  );
}
