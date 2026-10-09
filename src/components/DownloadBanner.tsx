/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { ArrowRight, Sparkles } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

export const DownloadBanner: React.FC = () => {
  return (
    <section className="py-12 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Wide Purple Banner Card */}
        <div className="relative rounded-3xl p-6 sm:p-10 lg:p-12 bg-gradient-to-r from-purple-600 via-indigo-600 to-purple-700 text-white shadow-xl overflow-hidden flex flex-col md:flex-row items-center justify-between gap-8">
          
          {/* Subtle decorative sparkles in corners */}
          <Sparkles className="absolute top-4 left-4 w-4 h-4 text-white/20" />
          <Sparkles className="absolute bottom-4 left-8 w-3 h-3 text-white/20" />
          <Sparkles className="absolute top-4 right-8 w-4 h-4 text-white/20" />
          <Sparkles className="absolute bottom-4 right-12 w-3 h-3 text-white/20" />

          {/* Left + Middle Content: App Icon + Headline */}
          <div className="flex items-center gap-5 sm:gap-6 text-center sm:text-left flex-col sm:flex-row">
            
            {/* App Icon */}
            <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl sm:rounded-3xl bg-slate-950 p-2.5 flex items-center justify-center gap-1.5 shadow-lg border border-purple-300/30 shrink-0">
              <div className="w-2.5 h-full rounded-b-full bg-slate-800 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
                <div className="h-2/3 bg-gradient-to-t from-orange-500 to-amber-400 rounded-b-full" />
              </div>
              <div className="w-2.5 h-full rounded-b-full bg-slate-800 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
                <div className="h-3/4 bg-gradient-to-t from-sky-500 to-cyan-300 rounded-b-full" />
              </div>
              <div className="w-2.5 h-full rounded-b-full bg-slate-800 border border-white/20 flex flex-col-reverse p-[1px] overflow-hidden">
                <div className="h-1/2 bg-gradient-to-t from-purple-500 to-pink-500 rounded-b-full" />
              </div>
            </div>

            {/* Title & Subtitle */}
            <div className="space-y-1">
              <h3 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-white">
                Color Sort Puzzle
              </h3>
              <p className="text-sm sm:text-base text-purple-100/90 font-normal">
                Download now and start your color sorting adventure!
              </p>
            </div>

          </div>

          {/* Right: White Button with Google Play Logo + Arrow */}
          <div className="shrink-0 w-full sm:w-auto">
            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-3 px-6 py-4 bg-white hover:bg-slate-50 active:scale-95 text-slate-950 font-bold rounded-2xl shadow-lg transition-all group"
            >
              {/* Google Play Icon */}
              <svg className="w-6 h-6 shrink-0" viewBox="0 0 24 24" fill="none">
                <path d="M3.6 1.7L13.8 12 3.6 22.3c-.4-.4-.6-.9-.6-1.5V3.2c0-.6.2-1.1.6-1.5z" fill="#00C1DE"/>
                <path d="M17.2 8.6L13.8 12l3.4 3.4 3.9-2.2c1.1-.6 1.1-1.7 0-2.4l-3.9-2.2z" fill="#FFC900"/>
                <path d="M3.6 22.3L13.8 12l3.4 3.4-11.8 6.7c-.7.4-1.4.3-1.8.2z" fill="#FF3A44"/>
                <path d="M3.6 1.7C4 1.6 4.7 1.5 5.4 1.9L17.2 8.6 13.8 12 3.6 1.7z" fill="#00E676"/>
              </svg>

              <div className="text-left leading-tight">
                <div className="text-[10px] text-slate-500 font-semibold uppercase">Get it on</div>
                <div className="text-sm font-extrabold text-slate-950">Google Play</div>
              </div>

              <ArrowRight className="w-4 h-4 text-slate-700 ml-1 group-hover:translate-x-1 transition-transform" />
            </a>
          </div>

        </div>

      </div>
    </section>
  );
};
