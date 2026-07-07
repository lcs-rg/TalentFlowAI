import { ReactNode } from "react";

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-secondary px-4">
      <div className="relative w-full max-w-sm">
        {/* Subtle gradient glow behind the card */}
        <div className="absolute -inset-10 bg-accent-500/5 blur-3xl rounded-full" />
        <div className="relative">{children}</div>
      </div>
    </div>
  );
}
