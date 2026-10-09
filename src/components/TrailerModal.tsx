/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useEffect, useState } from 'react';
import { X, ArrowRight, FlaskConical, Sparkles } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

interface TrailerModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const TrailerModal: React.FC<TrailerModalProps> = ({ isOpen, onClose }) => {
  const [step, setStep] = useState(0);

  useEffect(() => {
    if (!isOpen) {
      setStep(0);
      return;
    }
    const interval = setInterval(() => {
      setStep((prev) => (prev + 1) % 4);
    }, 2400);
    return () => clearInterval(interval);
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div
      role="dialog"
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/80 backdrop-blur-sm animate-fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-2xl bg-slate-900 rounded-3xl border border-slate-800 shadow-2xl overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Top Header */}
        <div className="px-6 py-4 border-b border-slate-800 flex items-center justify-between text-white">
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse" />
            <span className="text-sm font-bold">Color Sort Puzzle — Gameplay Preview</span>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-white rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Video / Animated Preview Screen */}
        <div className="relative aspect-16/9 bg-gradient-to-b from-sky-400 via-sky-300 to-amber-100 flex flex-col items-center justify-center p-6 text-center select-none overflow-hidden">
          
          {/* Animated Tubes Gameplay Showcase */}
          <div className="flex items-end justify-center gap-4 sm:gap-6 my-auto">
            {/* Tube 1 */}
            <div className={`w-10 sm:w-12 h-36 rounded-b-2xl border-2 border-white bg-white/20 backdrop-blur-xs flex flex-col-reverse p-1 shadow-md transition-transform duration-500 ${
              step === 0 ? '-translate-y-4' : ''
            }`}>
              <div className="h-1/4 bg-purple-600 rounded-b-xl" />
              <div className="h-1/4 bg-emerald-500" />
              <div className="h-1/4 bg-pink-500" />
              <div className={`h-1/4 bg-orange-500 transition-all ${step >= 1 ? 'opacity-0 scale-90' : ''}`} />
            </div>

            {/* Pouring Stream Animation with Lucide icon */}
            {step === 1 && (
              <div className="flex items-center gap-1 text-orange-600 animate-bounce -translate-y-6">
                <FlaskConical className="w-6 h-6 rotate-45" />
                <ArrowRight className="w-5 h-5" />
              </div>
            )}

            {/* Tube 2: Receiving liquid */}
            <div className={`w-10 sm:w-12 h-36 rounded-b-2xl border-2 border-white bg-white/20 backdrop-blur-xs flex flex-col-reverse p-1 shadow-md transition-transform duration-500 ${
              step === 1 ? 'scale-105 ring-2 ring-orange-400' : ''
            }`}>
              <div className="h-1/4 bg-amber-400 rounded-b-xl" />
              <div className="h-1/4 bg-blue-600" />
              <div className="h-1/4 bg-orange-500" />
              {step >= 1 && (
                <div className="h-1/4 bg-orange-500 animate-pulse rounded-t-sm" />
              )}
            </div>

            {/* Tube 3: Fully sorted victory */}
            <div className={`w-10 sm:w-12 h-36 rounded-b-2xl border-2 border-white bg-white/20 backdrop-blur-xs flex flex-col-reverse p-1 shadow-md ${
              step === 3 ? 'ring-4 ring-emerald-400 shadow-emerald-500/30' : ''
            }`}>
              <div className="h-1/4 bg-emerald-500 rounded-b-xl" />
              <div className="h-1/4 bg-emerald-500" />
              <div className="h-1/4 bg-emerald-500" />
              <div className="h-1/4 bg-emerald-500 rounded-t-sm" />
            </div>
          </div>

          {/* Subtitle Bar */}
          <div className="bg-slate-950/80 backdrop-blur-md px-4 py-2 rounded-xl text-white text-xs font-semibold max-w-sm">
            {step === 0 && 'Step 1: Tap source tube to lift color layer'}
            {step === 1 && 'Step 2: Pour color into matching recipient tube'}
            {step === 2 && 'Step 3: Plan moves strategically to free blocked colors'}
            {step === 3 && 'Level Complete! 3 Stars and rewards unlocked!'}
          </div>

        </div>

        {/* Bottom CTA Bar */}
        <div className="p-4 bg-slate-950 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs">
          <span className="text-slate-400">
            Full game available free on Google Play
          </span>
          <a
            href={GAME_INFO.playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="px-5 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-bold rounded-xl shadow-md transition-all"
          >
            Install on Google Play
          </a>
        </div>

      </div>
    </div>
  );
};
