"use client";

import { useQuery } from "@tanstack/react-query";
import { motion } from "motion/react";
import { api } from "@/lib/api";
import { Card, Badge, Skeleton, EmptyState } from "@/components/ui/primitives";
import { Calendar, Clock, Video, Phone, MapPin, User } from "lucide-react";
import Link from "next/link";

interface InterviewItem {
  id: string;
  candidateId: string;
  type: string;
  status: string;
  scheduledAt: string;
  createdAt: string;
  candidateName?: string;
}

const TYPE_ICONS: Record<string, React.ReactNode> = {
  VIDEO: <Video size={12} />,
  PHONE: <Phone size={12} />,
  IN_PERSON: <MapPin size={12} />,
  TECHNICAL: <Video size={12} />,
};

const STATUS_STYLES: Record<string, { label: string; variant: "default" | "success" | "warning" | "accent" }> = {
  SCHEDULED: { label: "Agendada", variant: "accent" },
  CONFIRMED: { label: "Confirmada", variant: "success" },
  COMPLETED: { label: "Realizada", variant: "default" },
  CANCELLED: { label: "Cancelada", variant: "default" },
  NO_SHOW: { label: "Não compareceu", variant: "warning" },
};

export default function InterviewsPage() {
  // V1: load interviews via jobs/applications
  const { data: jobs, isLoading: jobsLoading } = useQuery({
    queryKey: ["jobs"],
    queryFn: async () => {
      const { data: res } = await api.get("/api/v1/jobs?size=50");
      return res.data as any[];
    },
  });

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-lg font-semibold tracking-tight text-primary">Entrevistas</h1>
        <p className="mt-1 text-sm text-tertiary">
          Gerencie as entrevistas agendadas
        </p>
      </div>

      {jobsLoading ? (
        <div className="space-y-2">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-20 w-full" />
          ))}
        </div>
      ) : (
        <EmptyState
          icon={<Calendar size={18} />}
          title="Nenhuma entrevista"
          description="As entrevistas são agendadas a partir do pipeline de cada vaga."
          action={
            <Link href="/jobs">
              <span className="text-sm text-accent hover:underline">Ver vagas</span>
            </Link>
          }
        />
      )}
    </div>
  );
}
