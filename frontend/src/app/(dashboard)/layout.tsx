"use client";

import { useAuth } from "@/lib/auth";
import { useRouter, usePathname } from "next/navigation";
import { useEffect, ReactNode, useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "motion/react";
import {
  LayoutDashboard, Briefcase, Users, Calendar, Building2,
  LogOut, Menu, X, ChevronRight, Sparkles,
} from "lucide-react";

const NAV_ITEMS = [
  { href: "/", label: "Dashboard", icon: LayoutDashboard },
  { href: "/jobs", label: "Vagas", icon: Briefcase },
  { href: "/candidates", label: "Candidatos", icon: Users },
  { href: "/interviews", label: "Entrevistas", icon: Calendar },
  { href: "/company", label: "Empresa", icon: Building2 },
];

export default function DashboardLayout({ children }: { children: ReactNode }) {
  const { user, loading, isAuthenticated, logout } = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    if (!loading && !isAuthenticated) router.push("/login");
  }, [loading, isAuthenticated, router]);

  useEffect(() => { setSidebarOpen(false); }, [pathname]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-bg">
        <div className="flex flex-col items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
            <span className="text-sm font-semibold text-text-primary">TF</span>
          </div>
          <div className="h-4 w-5 animate-spin rounded-full border-2 border-accent border-t-transparent" />
        </div>
      </div>
    );
  }

  const NavItems = () => (
    <nav className="flex-1 space-y-0.5 px-3 py-4">
      <div className="px-3 pb-2">
        <p className="text-[10px] font-medium uppercase tracking-widest text-text-disabled">Menu</p>
      </div>
      {NAV_ITEMS.map((item) => {
        const active = pathname === item.href || (item.href !== "/" && pathname.startsWith(item.href));
        return (
          <Link key={item.href} href={item.href}>
            <motion.div
              whileTap={{ scale: 0.98 }}
              className={`flex items-center gap-3 px-3 py-2 rounded-lg text-[13px] transition-all duration-150 ${
                active
                  ? "bg-accent-muted text-accent font-medium"
                  : "text-text-secondary hover:text-text-primary hover:bg-bg-elevated-2"
              }`}
            >
              <item.icon size={15} className="shrink-0" />
              <span className="truncate">{item.label}</span>
              {active && <ChevronRight size={12} className="ml-auto text-accent/60" />}
            </motion.div>
          </Link>
        );
      })}
    </nav>
  );

  const Sidebar = () => (
    <>
      {/* Logo */}
      <div className="flex h-14 items-center gap-3 px-4 border-b border-border">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary shadow-sm">
          <span className="text-[11px] font-semibold text-text-primary">TF</span>
        </div>
        <div>
          <span className="text-[13px] font-semibold text-text-primary tracking-tight">TalentFlow</span>
          <p className="text-[10px] text-text-tertiary leading-none">Recrutamento Inteligente</p>
        </div>
        <button onClick={() => setSidebarOpen(false)} className="lg:hidden ml-auto p-1 rounded-md hover:bg-bg-elevated-2 text-text-disabled">
          <X size={15} />
        </button>
      </div>

      <NavItems />

      {/* User footer */}
      <div className="border-t border-border p-3">
        <div className="flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-bg-elevated-2 transition-colors group">
          <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary text-[11px] font-medium text-text-primary shrink-0 ring-2 ring-primary/30">
            {user?.name?.[0]?.toUpperCase() || "U"}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-[12px] font-medium text-text-primary truncate">{user?.name || "Usuário"}</p>
            <p className="text-[10px] text-text-disabled truncate">{user?.email || ""}</p>
          </div>
          <button
            onClick={logout}
            className="p-1.5 rounded-md hover:bg-status-danger/10 text-text-disabled hover:text-status-danger transition-colors opacity-0 group-hover:opacity-100"
            title="Sair"
          >
            <LogOut size={13} />
          </button>
        </div>
      </div>
    </>
  );

  return (
    <div className="flex min-h-screen bg-bg">
      {/* Mobile overlay */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.div
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            transition={{ duration: 0.15 }}
            className="fixed inset-0 z-40 bg-bg-overlay backdrop-blur-sm lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}
      </AnimatePresence>

      {/* Mobile sidebar */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.aside
            initial={{ x: -280 }} animate={{ x: 0 }} exit={{ x: -280 }}
            transition={{ type: "spring", damping: 25, stiffness: 200 }}
            className="fixed left-0 top-0 z-50 flex h-full w-[260px] flex-col bg-bg-elevated border-r border-border lg:hidden shadow-2xl"
          >
            <Sidebar />
          </motion.aside>
        )}
      </AnimatePresence>

      {/* Desktop sidebar */}
      <aside className="hidden lg:flex fixed left-0 top-0 z-40 h-full w-[230px] flex-col bg-bg-elevated border-r border-border">
        <Sidebar />
      </aside>

      {/* Mobile top bar */}
      <div className="lg:hidden fixed top-0 left-0 right-0 z-30 flex h-14 items-center gap-3 px-4 bg-bg-elevated/80 backdrop-blur-md border-b border-border">
        <button onClick={() => setSidebarOpen(true)} className="p-1.5 rounded-lg hover:bg-bg-elevated-2 text-text-secondary">
          <Menu size={18} />
        </button>
        <div className="flex items-center gap-2.5">
          <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary">
            <span className="text-[10px] font-semibold text-text-primary">TF</span>
          </div>
          <span className="text-[13px] font-semibold text-text-primary">TalentFlow</span>
        </div>
      </div>

      {/* Main content */}
      <main className="flex-1 lg:ml-[230px] pt-14 lg:pt-0 min-h-screen">
        <div className="animate-fade-in p-4 lg:p-6 max-w-[1400px]">
          {children}
        </div>
      </main>
    </div>
  );
}
