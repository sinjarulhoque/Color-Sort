/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import {
  Coins,
  Heart,
  Settings,
  RotateCcw,
  Lightbulb,
  Plus,
  Trophy,
  User,
  Award,
  Palette,
  ShoppingCart,
  Calendar,
  Flame,
  Gift,
  Lock,
  Sparkles,
  Play,
  Home,
  Layers,
  Users,
  Mail,
  Star,
  Check,
} from 'lucide-react';

/* -------------------------------------------------------------------------- */
/* PHONE FRAME WRAPPER                                                        */
/* -------------------------------------------------------------------------- */
export const PhoneFrame: React.FC<{
  children: React.ReactNode;
  className?: string;
  glow?: boolean;
}> = ({ children, className = '', glow = false }) => {
  return (
    <div
      className={`relative rounded-[38px] p-2 sm:p-2.5 bg-slate-950 border-[5px] sm:border-[6px] border-slate-800/90 shadow-2xl overflow-hidden select-none ${
        glow ? 'shadow-[0_20px_50px_rgba(245,158,11,0.25)]' : ''
      } ${className}`}
    >
      {/* Notch */}
      <div className="absolute top-3 sm:top-3.5 left-1/2 -translate-x-1/2 w-20 sm:w-24 h-4 sm:h-4.5 bg-black rounded-full z-30 flex items-center justify-end pr-2">
        <div className="w-2 h-2 rounded-full bg-slate-900 border border-slate-800" />
      </div>

      {/* Screen Content */}
      <div className="w-full h-full rounded-[28px] overflow-hidden bg-slate-950 text-white relative flex flex-col">
        {children}
      </div>
    </div>
  );
};
