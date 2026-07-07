"use client";

import { CandidateAuthProvider } from "@/lib/candidate-auth";
import { ReactNode } from "react";

export default function CandidateLayout({ children }: { children: ReactNode }) {
  return (
    <CandidateAuthProvider>
      <div className="min-h-screen bg-bg">
        {children}
      </div>
    </CandidateAuthProvider>
  );
}
