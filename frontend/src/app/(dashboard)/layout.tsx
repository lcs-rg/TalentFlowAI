"use client";

import { useAuth } from "@/lib/auth";
import { useRouter, usePathname } from "next/navigation";
import { useEffect, ReactNode, useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "motion/react";
import {
  LayoutDashboard, Briefcase, Users, Calendar, Building2, LogOut, Menu, X,
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
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (!loading && !isAuthenticated) router.push("/login");
  }, [loading, isAuthenticated, router]);

  // Close sidebar on route change (mobile)
  useEffect(() => { setOpen(false); }, [pathname]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-bg">
        <div className="h-5 w-5 animate-spin rounded-full border-2 border-accent border-t-transparent" />
      </div>
    );
  }

  const SidebarContent = () => (
    <>
      {/* Logo */}
      <div className="flex h-14 items-center justify-between px-5 border-b border-border">
        <div className="flex items-center gap-3">
          <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-primary">
            <span className="text-xs font-semibold text-text-primary">TF</span>
          </div>
          <span className="text-sm font-semibold text-text-primary">TalentFlow</span>
        </div>
        {/* Close on mobile */}
        <button onClick={() => setOpen(false)} className="lg:hidden p-1 rounded hover:bg-bg-elevated-2 text-text-disabled">
          <X size={16} />
        </button>
      </div>

      {/* Nav */}
      <nav className="flex-1 space-y-0.5 px-3 py-4">
        {NAV_ITEMS.map((item) => {
          const active = pathname === item.href || (item.href !== "/" && pathname.startsWith(item.href));
          return (
            <Link key={item.href} href={item.href} onClick={() => setOpen(false)}>
              <motion.div
                whileTap={{ scale: 0.98 }}
                className={`flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-colors ${
                  active
                    ? "bg-primary/10 text-accent font-medium"
                    : "text-text-secondary hover:text-text-primary hover:bg-bg-elevated-2"
                }`}
              >
                <item.icon size={15} />
                <span>{item.label}</span>
              </motion.div>
            </Link>
          );
        })}
      </nav>

      {/* User */}
      <div className="border-t border-border px-3 py-3">
        <div className="flex items-center gap-3 px-3 py-2 rounded-md hover:bg-bg-elevated-2 transition-colors">
          <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary text-xs font-medium text-text-primary shrink-0">
            {user?.name?.[0]?.toUpperCase() || "U"}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-medium text-text-primary truncate">{user?.name || ""}</p>
            <p className="text-[11px] text-text-disabled truncate">{user?.email || ""}</p>
          </div>
          <button onClick={logout} className="p-1 rounded hover:bg-bg text-text-disabled hover:text-status-danger transition-colors" title="Sair">
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
        {open && (
          <motion.div
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 z-40 bg-black/60 lg:hidden"
            onClick={() => setOpen(false)}
          />
        )}
      </AnimatePresence>

      {/* Sidebar — mobile: slide overlay, desktop: fixed */}
      <AnimatePresence>
        {open && (
          <motion.aside
            initial={{ x: -280 }} animate={{ x: 0 }} exit={{ x: -280 }}
            transition={{ type: "spring", damping: 25, stiffness: 200 }}
            className="fixed left-0 top-0 z-50 flex h-full w-[240px] flex-col bg-bg-elevated border-r border-border lg:hidden"
          >
            <SidebarContent />
          </motion.aside>
        )}
      </AnimatePresence>

      {/* Desktop sidebar */}
      <aside className="hidden lg:flex fixed left-0 top-0 z-40 h-full w-[220px] flex-col bg-bg-elevated border-r border-border">
        <SidebarContent />
      </aside>

      {/* Top bar (mobile) */}
      <div className="lg:hidden fixed top-0 left-0 right-0 z-30 flex h-14 items-center gap-3 px-4 bg-bg-elevated border-b border-border">
        <button onClick={() => setOpen(true)} className="p-1.5 rounded-lg hover:bg-bg-elevated-2 text-text-secondary">
          <Menu size={18} />
        </button>
        <div className="flex items-center gap-2">
          <div className="flex h-6 w-6 items-center justify-center rounded-md bg-primary">
            <span className="text-[10px] font-semibold text-text-primary">TF</span>
          </div>
          <span className="text-sm font-semibold text-text-primary">TalentFlow</span>
        </div>
      </div>

      {/* Main content */}
      <main className="flex-1 lg:ml-[220px] pt-14 lg:pt-0">
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.15 }}>
          {children}
        </motion.div>
      </main>
    </div>
  );
}
