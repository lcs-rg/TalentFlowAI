"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useCandidateAuth } from "@/lib/candidate-auth";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton, EmptyState } from "@/components/ui/primitives";
import { Briefcase, MapPin, Clock, LogOut } from "lucide-react";
import Link from "next/link";
import { useEffect } from "react";

const STATUS_STYLES: Record<string, { label: string; variant: "default" | "success" | "warning" | "accent" }> = {
  NEW: { label: "Novo", variant: "default" },
  SCREENING: { label: "Triagem", variant: "accent" },
  INTERVIEWING: { label: "Entrevistando", variant: "warning" },
  OFFERED: { label: "Proposta", variant: "success" },
  HIRED: { label: "Contratado", variant: "success" },
  REJECTED: { label: "Não selecionado", variant: "default" },
};

export default function CandidateApplicationsPage() {
  const router = useRouter();
  const { logout, isAuthenticated, loading: authLoading } = useCandidateAuth();

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.push("/candidate/login");
  }, [authLoading, isAuthenticated, router]);

  const { data: applications, isLoading } = useQuery({
    queryKey: ["candidate-applications"],
    queryFn: async () => {
      const { data: res } = await api.get("/api/v1/candidate/applications?size=50");
      return res.data;
    },
    enabled: isAuthenticated,
  });

  if (authLoading || isLoading) {
    return (
      <div className="max-w-lg mx-auto p-8 space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-20 w-full" />
        <Skeleton className="h-20 w-full" />
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto p-4 md:p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold text-primary">Minhas Candidaturas</h1>
          <p className="text-xs text-tertiary mt-0.5">Acompanhe o status das suas aplicações</p>
        </div>
        <Button variant="ghost" size="sm" onClick={() => { logout(); router.push("/candidate/login"); }} leftIcon={<LogOut size={12} />}>
          Sair
        </Button>
      </div>

      <div className="flex gap-2 border-b border-border pb-3">
        <Link href="/candidate/profile" className="text-sm text-tertiary hover:text-secondary">Perfil</Link>
        <Link href="/candidate/applications" className="text-sm font-medium text-accent">Candidaturas</Link>
      </div>

      {!applications || applications.length === 0 ? (
        <EmptyState
          icon={<Briefcase size={18} />}
          title="Nenhuma candidatura"
          description="Você ainda não se candidatou a nenhuma vaga."
        />
      ) : (
        <div className="space-y-2">
          {(applications as any[]).map((app) => (
            <Card key={app.id} padding="md">
              <div className="flex items-start justify-between">
                <div className="min-w-0 flex-1">
                  <h3 className="text-sm font-medium text-primary">{app.jobTitle || `Vaga #${app.jobId?.toString().slice(0, 8)}`}</h3>
                  <div className="flex items-center gap-3 mt-1 text-xs text-tertiary">
                    {app.jobDepartment && <span className="flex items-center gap-1"><Briefcase size={11} /> {app.jobDepartment}</span>}
                    {app.jobLocation && <span className="flex items-center gap-1"><MapPin size={11} /> {app.jobLocation}</span>}
                  </div>
                </div>
                <Badge variant={STATUS_STYLES[app.status]?.variant ?? "default"}>
                  {STATUS_STYLES[app.status]?.label ?? app.status}
                </Badge>
              </div>
              {app.score != null && (
                <div className="mt-2 flex items-center gap-2">
                  <div className="flex-1 h-1 rounded-full bg-tertiary">
                    <div className="h-full rounded-full bg-accent-500" style={{ width: `${app.score * 100}%` }} />
                  </div>
                  <span className="text-[11px] text-tertiary tabular-nums">{Math.round(app.score * 100)}%</span>
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
