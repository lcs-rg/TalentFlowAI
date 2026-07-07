"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useCandidateAuth } from "@/lib/candidate-auth";
import { api } from "@/lib/api";
import { Button, Card, Badge, Skeleton, EmptyState } from "@/components/ui/primitives";
import { Briefcase, MapPin, LogOut, Sparkles } from "lucide-react";
import Link from "next/link";
import { useEffect } from "react";

export default function CandidateJobsPage() {
  const router = useRouter();
  const { logout, isAuthenticated, loading: authLoading } = useCandidateAuth();

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.push("/candidate/login");
  }, [authLoading, isAuthenticated, router]);

  const { data: jobs, isLoading } = useQuery({
    queryKey: ["candidate-recommended-jobs"],
    queryFn: async () => {
      // If candidate is logged in, get personalized recommendations
      const stored = localStorage.getItem("talentflow-candidate");
      if (stored) {
        const c = JSON.parse(stored);
        try {
          const { data: res } = await api.get(`/api/v1/ai/recommend-jobs/${c.id}?topK=10`);
          return res.data as any[];
        } catch { /* fallback to empty */ }
      }
      return [];
    },
    enabled: isAuthenticated,
  });

  if (authLoading) return null;

  return (
    <div className="max-w-lg mx-auto p-4 md:p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold text-primary">Vagas para você</h1>
          <p className="text-xs text-tertiary mt-0.5">Recomendações baseadas no seu perfil</p>
        </div>
        <Button variant="ghost" size="sm" onClick={() => { logout(); router.push("/candidate/login"); }} leftIcon={<LogOut size={12} />}>
          Sair
        </Button>
      </div>

      <div className="flex gap-2 border-b border-border pb-3">
        <Link href="/candidate/profile" className="text-sm text-tertiary hover:text-secondary">Perfil</Link>
        <Link href="/candidate/applications" className="text-sm text-tertiary hover:text-secondary">Candidaturas</Link>
        <Link href="/candidate/jobs" className="text-sm font-medium text-accent">Vagas</Link>
      </div>

      {isLoading ? (
        <div className="space-y-2">
          {[1, 2, 3].map((i) => <Skeleton key={i} className="h-20 w-full" />)}
        </div>
      ) : !jobs || jobs.length === 0 ? (
        <EmptyState
          icon={<Sparkles size={18} />}
          title="Nenhuma recomendação ainda"
          description="Complete seu perfil com seu currículo para receber recomendações personalizadas."
        />
      ) : (
        <div className="space-y-2">
          {(jobs as any[]).map((job) => (
            <Card key={job.id} padding="md">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-sm font-medium text-primary">{job.title}</h3>
                  <div className="flex items-center gap-2 mt-1 text-xs text-tertiary">
                    {job.department && <span className="flex items-center gap-1"><Briefcase size={11} /> {job.department}</span>}
                    {job.location && <span className="flex items-center gap-1"><MapPin size={11} /> {job.location}</span>}
                  </div>
                </div>
                <Badge variant={job.status === "PUBLISHED" ? "success" : "default"}>
                  {job.status === "PUBLISHED" ? "Publicada" : job.status}
                </Badge>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
