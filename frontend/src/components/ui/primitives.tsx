"use client";

import { ReactNode, ButtonHTMLAttributes, InputHTMLAttributes } from "react";
import { motion } from "motion/react";
import { cn } from "@/lib/utils";
import { Loader2 } from "lucide-react";

/* ─── Button ─────────────────────────────────────────── */

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md" | "lg";
  loading?: boolean;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
}

const buttonVariants = {
  primary:
    "bg-accent-500 text-white hover:bg-accent-600 active:bg-accent-700 shadow-xs",
  secondary:
    "bg-tertiary text-primary hover:bg-neutral-200 dark:hover:bg-neutral-700 border border-primary",
  ghost:
    "text-secondary hover:text-primary hover:bg-hover",
  danger:
    "bg-error-500/10 text-error-500 hover:bg-error-500/20 border border-error-500/20",
};

const buttonSizes = {
  sm: "h-7 px-2.5 text-xs gap-1.5 rounded-md",
  md: "h-8 px-3.5 text-sm gap-2 rounded-lg",
  lg: "h-10 px-5 text-sm gap-2.5 rounded-lg",
};

export function Button({
  variant = "primary",
  size = "md",
  loading = false,
  leftIcon,
  rightIcon,
  className,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <motion.button
      whileTap={{ scale: 0.985 }}
      transition={{ duration: 0.1 }}
      className={cn(
        "inline-flex items-center justify-center font-medium transition-all duration-150",
        "focus-visible:outline-2 focus-visible:outline-accent-500 focus-visible:outline-offset-2",
        "disabled:opacity-40 disabled:cursor-not-allowed",
        buttonVariants[variant],
        buttonSizes[size],
        className
      )}
      disabled={disabled || loading}
      {...(props as any)}
    >
      {loading ? (
        <Loader2 size={size === "sm" ? 12 : 14} className="animate-spin" />
      ) : (
        leftIcon
      )}
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

export function Input({
  label,
  hint,
  error,
  leftIcon,
  className,
  id,
  ...props
}: InputProps) {
  const inputId = id || label?.toLowerCase().replace(/\s+/g, "-");

  return (
    <div className="space-y-1.5">
      {label && (
        <label
          htmlFor={inputId}
          className="block text-xs font-medium text-secondary"
        >
          {label}
        </label>
      )}
      <div className="relative">
        {leftIcon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-tertiary">
            {leftIcon}
          </div>
        )}
        <input
          id={inputId}
          className={cn(
            "w-full h-9 rounded-lg bg-tertiary px-3 text-sm text-primary placeholder:text-tertiary",
            "border border-primary transition-all duration-150",
            "hover:border-neutral-300 dark:hover:border-neutral-700",
            "focus:outline-none focus:ring-2 focus:ring-accent-500/20 focus:border-accent-500",
            error && "border-error-500 focus:ring-error-500/20 focus:border-error-500",
            leftIcon && "pl-9",
            className
          )}
          {...props}
        />
      </div>
      {hint && !error && (
        <p className="text-xs text-tertiary">{hint}</p>
      )}
      {error && (
        <p className="text-xs text-error-500">{error}</p>
      )}
    </div>
  );
}

/* ─── Card ────────────────────────────────────────────── */

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  padding?: "none" | "sm" | "md" | "lg";
  onClick?: () => void;
}

const cardPaddings = {
  none: "",
  sm: "p-3",
  md: "p-4",
  lg: "p-6",
};

export function Card({ children, className, hover = false, padding = "md", onClick }: CardProps) {
  const Component = onClick ? motion.button : motion.div;

  return (
    <Component
      whileHover={hover ? { y: -1 } : undefined}
      transition={{ duration: 0.15 }}
      className={cn(
        "rounded-xl bg-secondary border border-primary",
        "transition-all duration-150",
        hover && "cursor-pointer hover:shadow-md hover:border-neutral-300 dark:hover:border-neutral-700",
        cardPaddings[padding],
        className
      )}
      onClick={onClick as any}
    >
      {children}
    </Component>
  );
}

/* ─── Badge ───────────────────────────────────────────── */

interface BadgeProps {
  children: ReactNode;
  variant?: "default" | "success" | "warning" | "error" | "accent";
  size?: "sm" | "md";
}

const badgeVariants = {
  default: "bg-neutral-200 dark:bg-neutral-800 text-secondary",
  success: "bg-success-400/10 text-success-600 dark:text-success-400",
  warning: "bg-warning-400/10 text-warning-600 dark:text-warning-400",
  error: "bg-error-400/10 text-error-600 dark:text-error-400",
  accent: "bg-accent-400/10 text-accent-600 dark:text-accent-400",
};

const badgeSizes = {
  sm: "px-1.5 py-0.5 text-[11px]",
  md: "px-2 py-0.5 text-xs",
};

export function Badge({ children, variant = "default", size = "sm" }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center font-medium rounded-md",
        badgeVariants[variant],
        badgeSizes[size]
      )}
    >
      {children}
    </span>
  );
}

/* ─── Divider ─────────────────────────────────────────── */

export function Divider({ className }: { className?: string }) {
  return <hr className={cn("border-secondary", className)} />;
}

/* ─── Skeleton ────────────────────────────────────────── */

export function Skeleton({ className }: { className?: string }) {
  return (
    <div
      className={cn(
        "animate-pulse rounded-lg bg-neutral-200 dark:bg-neutral-800",
        className
      )}
    />
  );
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
    <div className="flex flex-col items-center justify-center py-16 text-center">
      {icon && (
        <div className="mb-4 rounded-full bg-tertiary p-3 text-tertiary">
          {icon}
        </div>
      )}
      <h3 className="text-sm font-medium text-primary">{title}</h3>
      <p className="mt-1 text-xs text-tertiary max-w-sm">{description}</p>
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}
