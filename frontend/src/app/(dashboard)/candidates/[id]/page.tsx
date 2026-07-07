"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useParams, useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton } from "@/components/ui/primitives";
import { ArrowLeft, User, Mail, Phone, Calendar, Briefcase, Trash2, Sparkles } from "lucide-react";
import Link from "next/link";
import { useState } from "react";
import toast from "react-hot-toast";

interface CandidateResponse {
  id: string;
  name: string;
  email: string;
  phone: string;
  resumeUrl: string;
  resumeText: string;
  tags: string[];
  notes: string;
  createdAt: string;
  updatedAt: string;
  applications: { id: string; jobId: string; status: string; stageId: string }[];
}

const STATUS_STYLES: Record<string, { label: string; variant: "default" | "success" | "warning" | "accent" }> = {
  NEW: { label: "Novo", variant: "default" },
  SCREENING: { label: "Triagem", variant: "accent" },
  INTERVIEWING: { label: "Entrevistando", variant: "warning" },
  OFFERED: { label: "Proposta", variant: "success" },
  HIRED: { label: "Contratado", variant: "success" },
  REJECTED: { label: "Rejeitado", variant: "default" },
};

export default function CandidateDetailPage() {
  const params = useParams();
  const router = useRouter();
  const queryClient = useQueryClient();
  const id = params.id as string;

  const { data: candidate, isLoading } = useQuery({
    queryKey: ["candidate", id],
    queryFn: async () => {
      const { data: res } = await api.get(`/api/v1/candidates/${id}`);
      return res.data as CandidateResponse;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async () => {
      await api.delete(`/api/v1/candidates/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["candidates"] });
      router.push("/candidates");
    },
  });

  const aiMatchMutation = useMutation({
    mutationFn: async (jobId: string) => {
      const { data: res } = await api.post(`/api/v1/ai/match/${jobId}/${id}`);
      return res.data;
    },
    onSuccess: (data) => {
      toast.success(`Score de compatibilidade: ${Math.round((data?.overall_score ?? 0) * 100)}%`);
      queryClient.invalidateQueries({ queryKey: ["candidate", id] });
    },
    onError: () => toast.error("Erro na análise de compatibilidade"),
  });

  if (isLoading) {
    return (
      <div className="max-w-2xl space-y-8">
        <Skeleton className="h-8 w-64" />
        <Card padding="lg">
          <Skeleton className="h-5 w-48 mb-4" />
          <Skeleton className="h-4 w-full mb-2" />
          <Skeleton className="h-4 w-3/4" />
        </Card>
      </div>
    );
  }

  if (!candidate) {
    return (
      <div className="max-w-2xl space-y-8">
        <Link href="/candidates" className="flex items-center gap-2 text-sm text-tertiary hover:text-primary">
          <ArrowLeft size={14} /> Voltar
        </Link>
        <EmptyState icon={<User size={18} />} title="Candidato não encontrado" description="O candidato solicitado não existe." />
      </div>
    );
  }

  return (
    <div className="max-w-2xl space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link href="/candidates" className="p-1.5 rounded-lg hover:bg-bg-elevated-2 text-text-secondary">
            <ArrowLeft size={16} />
          </Link>
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10 text-accent text-sm font-medium">
              {candidate.name[0]?.toUpperCase()}
            </div>
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-primary">{candidate.name}</h1>
              <div className="flex items-center gap-3 mt-0.5 text-xs text-tertiary">
                <span className="flex items-center gap-1"><Mail size={11} /> {candidate.email}</span>
                {candidate.phone && <span className="flex items-center gap-1"><Phone size={11} /> {candidate.phone}</span>}
              </div>
            </div>
          </div>
        </div>
        <Button
          variant="danger"
          size="sm"
          leftIcon={<Trash2 size={12} />}
          loading={deleteMutation.isPending}
          onClick={() => { if (confirm("Remover candidato?")) deleteMutation.mutate(); }}
        >
          Remover
        </Button>
      </div>

      {/* Info */}
      <Card padding="lg">
        <h3 className="text-sm font-medium text-primary mb-3">Informações</h3>
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p className="text-xs text-tertiary">Cadastrado em</p>
            <p className="text-primary">{new Date(candidate.createdAt).toLocaleDateString("pt-BR")}</p>
          </div>
          <div>
            <p className="text-xs text-tertiary">Candidaturas</p>
            <p className="text-primary">{candidate.applications?.length ?? 0}</p>
          </div>
        </div>
      </Card>

      {/* Applications */}
      {candidate.applications && candidate.applications.length > 0 && (
        <div className="space-y-2">
          <h3 className="text-sm font-medium text-primary">Candidaturas</h3>
          {candidate.applications.map((app) => (
            <Card key={app.id} padding="md">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Briefcase size={14} className="text-tertiary" />
                  <span className="text-sm text-primary">Vaga #{app.jobId.toString().slice(0, 8)}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    leftIcon={<Sparkles size={11} />}
                    onClick={() => aiMatchMutation.mutate(app.jobId)}
                    loading={aiMatchMutation.isPending}
                  >
                    Analisar
                  </Button>
                  <Badge variant={STATUS_STYLES[app.status]?.variant ?? "default"}>
                    {STATUS_STYLES[app.status]?.label ?? app.status}
                  </Badge>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Resume */}
      {candidate.resumeText && (
        <Card padding="lg">
          <h3 className="text-sm font-medium text-primary mb-3">Currículo</h3>
          <pre className="text-xs text-secondary whitespace-pre-wrap font-mono max-h-48 overflow-y-auto">
            {candidate.resumeText}
          </pre>
        </Card>
      )}
    </div>
  );
}

function EmptyState({ icon, title, description }: { icon: React.ReactNode; title: string; description: string }) {
  return (
    <Card padding="lg">
      <div className="flex flex-col items-center py-8 text-center">
        <div className="rounded-full bg-tertiary p-2.5 mb-3">{icon}</div>
        <p className="text-sm font-medium text-primary">{title}</p>
        <p className="mt-1 text-xs text-tertiary">{description}</p>
      </div>
    </Card>
  );
}
