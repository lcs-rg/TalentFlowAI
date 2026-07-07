"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";
import { AuthProvider } from "@/lib/auth";
import { Toaster } from "react-hot-toast";

export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(() => new QueryClient({
    defaultOptions: { queries: { staleTime: 30_000, retry: 1 } },
  }));

  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        {children}
        <Toaster
          position="bottom-center"
          toastOptions={{
            style: {
              background: "#22161B",
              color: "#F7F3F4",
              border: "1px solid #3A2530",
              borderRadius: "10px",
              fontSize: "13px",
              padding: "10px 16px",
            },
          }}
        />
      </AuthProvider>
    </QueryClientProvider>
  );
}
