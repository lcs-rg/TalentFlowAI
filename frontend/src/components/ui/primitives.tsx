"use client";

import { ReactNode, ButtonHTMLAttributes, InputHTMLAttributes } from "react";
import { motion, HTMLMotionProps } from "motion/react";
import { cn } from "@/lib/utils";
import { Loader2 } from "lucide-react";

/* ─── Button ─────────────────────────────────────────── */

type ButtonVariant = "primary" | "secondary" | "ghost" | "danger";
type ButtonSize = "xs" | "sm" | "md" | "lg";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
}

const buttonVariants: Record<ButtonVariant, string> = {
  primary: "bg-primary text-text-primary hover:bg-primary-hover active:scale-[0.98] shadow-sm",
  secondary: "bg-bg-elevated-2 text-text-secondary hover:text-text-primary hover:bg-bg-elevated border border-border",
  ghost: "text-text-secondary hover:text-text-primary hover:bg-bg-elevated",
  danger: "bg-status-danger/10 text-status-danger hover:bg-status-danger/20 border border-status-danger/20",
};

const buttonSizes: Record<ButtonSize, string> = {
  xs: "h-7 px-2.5 text-[11px] gap-1.5 rounded-md",
  sm: "h-8 px-3 text-xs gap-1.5 rounded-lg",
  md: "h-9 px-4 text-[13px] gap-2 rounded-lg",
  lg: "h-10 px-5 text-sm gap-2 rounded-lg",
};

export function Button({
  variant = "primary", size = "md", loading = false,
  leftIcon, rightIcon, className, disabled, children, ...props
}: ButtonProps) {
  return (
    <motion.button
      whileTap={{ scale: 0.97 }}
      transition={{ duration: 0.1 }}
      className={cn(
        "inline-flex items-center justify-center font-medium transition-all duration-150",
        "focus-visible:outline-2 focus-visible:outline-accent focus-visible:outline-offset-2",
        "disabled:opacity-40 disabled:cursor-not-allowed disabled:pointer-events-none",
        buttonVariants[variant], buttonSizes[size], className
      )}
      disabled={disabled || loading}
      {...(props as any)}
    >
      {loading ? <Loader2 size={size === "xs" ? 11 : 13} className="animate-spin shrink-0" /> : leftIcon}
      {children}
      {!loading && rightIcon}
    </motion.button>
  );
}

/* ─── Input ──────────────────────────────────────────── */

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  hint?: string;
  error?: string;
  leftIcon?: ReactNode;
}

export function Input({ label, hint, error, leftIcon, className, id, ...props }: InputProps) {
  const inputId = id || label?.toLowerCase().replace(/\s+/g, "-");
  return (
    <div className="space-y-1.5">
      {label && (
        <label htmlFor={inputId} className="block text-[11px] font-medium text-text-secondary tracking-wide">
          {label}
        </label>
      )}
      <div className="relative">
        {leftIcon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-text-tertiary">{leftIcon}</div>
        )}
        <input
          id={inputId}
          className={cn(
            "w-full h-9 rounded-lg bg-bg-elevated px-3 text-[13px] text-text-primary placeholder:text-text-disabled",
            "border border-border transition-all duration-150",
            "hover:border-border-strong",
            "focus:outline-none focus:ring-2 focus:ring-accent/20 focus:border-accent",
            error && "border-status-danger focus:ring-status-danger/20 focus:border-status-danger",
            leftIcon && "pl-9", className
          )}
          {...props}
        />
      </div>
      {hint && !error && <p className="text-[11px] text-text-tertiary">{hint}</p>}
      {error && <p className="text-[11px] text-status-danger">{error}</p>}
    </div>
  );
}

/* ─── Card ────────────────────────────────────────────── */

type CardPadding = "none" | "sm" | "md" | "lg";

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  padding?: CardPadding;
  onClick?: () => void;
}

const cardPaddings: Record<CardPadding, string> = {
  none: "", sm: "p-3", md: "p-4", lg: "p-6",
};

export function Card({ children, className, hover = false, padding = "md", onClick }: CardProps) {
  const Comp = onClick ? motion.button : motion.div;
  return (
    <Comp
      whileHover={hover ? { y: -1, borderColor: "rgba(255,255,255,0.12)" } : undefined}
      transition={{ duration: 0.15 }}
      className={cn(
        "rounded-xl bg-bg-elevated border border-border w-full text-left",
        "transition-all duration-150",
        hover && "cursor-pointer hover:bg-bg-elevated-2",
        cardPaddings[padding], className
      )}
      onClick={onClick as any}
    >
      {children}
    </Comp>
  );
}

/* ─── Badge ───────────────────────────────────────────── */

type BadgeVariant = "default" | "success" | "warning" | "danger" | "accent" | "info";
type BadgeSize = "sm" | "md";

const badgeVariants: Record<BadgeVariant, string> = {
  default: "bg-bg-elevated-2 text-text-secondary border border-border",
  success: "bg-status-success/10 text-status-success border border-status-success/20",
  warning: "bg-status-warning/10 text-status-warning border border-status-warning/20",
  danger: "bg-status-danger/10 text-status-danger border border-status-danger/20",
  accent: "bg-accent-muted text-accent border border-accent/20",
  info: "bg-status-info/10 text-status-info border border-status-info/20",
};

const badgeSizes: Record<BadgeSize, string> = {
  sm: "px-1.5 py-0.5 text-[10px]",
  md: "px-2 py-0.5 text-[11px]",
};

interface BadgeProps {
  children: ReactNode;
  variant?: BadgeVariant;
  size?: BadgeSize;
  className?: string;
}

export function Badge({ children, variant = "default", size = "sm", className }: BadgeProps) {
  return (
    <span className={cn("inline-flex items-center font-medium rounded-md", badgeVariants[variant], badgeSizes[size], className)}>
      {children}
    </span>
  );
}

/* ─── Skeleton ────────────────────────────────────────── */

export function Skeleton({ className }: { className?: string }) {
  return <div className={cn("skeleton", className)} />;
}

/* ─── EmptyState ──────────────────────────────────────── */

interface EmptyStateProps {
  icon?: ReactNode;
  title: string;
  description: string;
  action?: ReactNode;
}

export function EmptyState({ icon, title, description, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4 text-center animate-fade-in">
      {icon && (
        <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-bg-elevated-2 border border-border text-text-tertiary">
          {icon}
        </div>
      )}
      <h3 className="font-display text-lg text-text-primary">{title}</h3>
      <p className="mt-1.5 text-[13px] text-text-tertiary max-w-xs leading-relaxed">{description}</p>
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}

/* ─── Divider ─────────────────────────────────────────── */

export function Divider({ className }: { className?: string }) {
  return <hr className={cn("border-border", className)} />;
}

/* ─── Page Header ─────────────────────────────────────── */

interface PageHeaderProps {
  title: string;
  description?: string;
  actions?: ReactNode;
}

export function PageHeader({ title, description, actions }: PageHeaderProps) {
  return (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 className="text-[15px] font-semibold tracking-tight text-text-primary">{title}</h1>
        {description && <p className="mt-1 text-[12px] text-text-tertiary">{description}</p>}
      </div>
      {actions && <div className="flex items-center gap-2">{actions}</div>}
    </div>
  );
}
